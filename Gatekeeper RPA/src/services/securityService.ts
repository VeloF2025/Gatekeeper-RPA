import { createHash, randomBytes, createCipheriv, createDecipheriv, scrypt } from 'crypto';
import { logger } from '@/lib/logger';
import { db } from '@/database/database';
import { workflowEvents } from '@/database/schemas';
import { eq } from 'drizzle-orm';

export interface AuditTrailEvent {
  type: string;
  data: Record<string, unknown>;
  timestamp: number;
  userId?: string;
  ipAddress?: string;
  userAgent?: string;
}

export interface SecurityConfig {
  encryptionKey: string;
  jwtSecret: string;
  corsOrigins: string[];
  rateLimiting: {
    enabled: boolean;
    maxRequests: number;
    windowMs: number;
  };
}

export interface MediaFile {
  id: string;
  path: string;
  size: number;
  type: string;
}

export interface ValidationResult {
  valid: boolean;
  sanitized?: string;
  error?: string;
  threats?: string[];
}

export class SecurityService {
  private encryptionKey: string;
  private algorithm = 'aes-256-gcm';
  private blockedIPs: Set<string> = new Set();
  private suspiciousActivities: Map<string, number> = new Map();

  constructor() {
    this.encryptionKey = process.env.ENCRYPTION_KEY ||
      crypto.getRandomValues(new Uint8Array(32)).join('');
  }

  /**
   * Log audit trail event following Zero Trust principles
   */
  async logAuditTrail(event: AuditTrailEvent): Promise<{
    id: string;
    success: boolean;
    encrypted: boolean;
    timestamp: Date;
  }> {
    try {
      const eventId = this.generateEventId();
      const timestamp = new Date(event.timestamp);

      // Encrypt sensitive data in audit trail
      const encryptedData = await this.encryptData(event.data);

      await db.insert(workflowEvents).values({
        ticketId: null, // Can be linked to specific ticket if available
        eventType: event.type,
        eventData: encryptedData as any,
        eventTimestamp: timestamp,
        eventSource: 'security_service'
      });

      logger.info('Audit trail event logged', {
        eventId,
        type: event.type,
        userId: event.userId,
        timestamp: timestamp.toISOString()
      });

      return {
        id: eventId,
        success: true,
        encrypted: true,
        timestamp
      };
    } catch (error) {
      logger.error('Failed to log audit trail', {
        error: error instanceof Error ? error.message : 'Unknown error',
        eventType: event.type
      });

      return {
        id: this.generateEventId(),
        success: false,
        encrypted: false,
        timestamp: new Date()
      };
    }
  }

  /**
   * Sanitize message content to prevent injection attacks
   */
  async sanitizeMessage(content: string): Promise<string> {
    // Remove HTML/XML tags
    let sanitized = content.replace(/<[^>]*>/g, '');

    // Escape special characters
    sanitized = sanitized
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#x27;')
      .replace(/\//g, '&#x2F;');

    // Remove potential SQL injection patterns
    sanitized = sanitized
      .replace(/(\s|^)(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE)(\s|$)/gi, '$1$3')
      .replace(/(\s|^)(UNION\s+ALL|UNION\s+SELECT)(\s|$)/gi, '$1$3');

    // Remove JavaScript patterns
    sanitized = sanitized
      .replace(/javascript:/gi, '')
      .replace(/on\w+\s*=/gi, '')
      .replace(/eval\((.*?)\)/gi, '');

    // Remove template injection patterns
    sanitized = sanitized.replace(/\$\{.*?\}/g, '');

    // Remove excessive whitespace
    sanitized = sanitized.replace(/\s+/g, ' ').trim();

    return sanitized;
  }

  /**
   * Validate input data against injection attacks
   */
  async validateInput(input: string): Promise<ValidationResult> {
    const threats: string[] = [];
    let sanitized = input;

    // Check for XSS patterns
    const xssPatterns = [
      /<script[^>]*>.*?<\/script>/gi,
      /javascript:/gi,
      /on\w+\s*=/gi,
      /<iframe[^>]*>/gi,
      /<object[^>]*>/gi,
      /<embed[^>]*>/gi
    ];

    xssPatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('XSS pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    // Check for SQL injection
    const sqlPatterns = [
      /(\s|^)(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE)(\s|$)/gi,
      /(\s|^)(UNION\s+ALL|UNION\s+SELECT)(\s|$)/gi,
      /(\s|^)(OR\s+1\s*=\s*1)(\s|$)/gi,
      /(\s|^)(AND\s+1\s*=\s*1)(\s|$)/gi,
      /;\s*(DROP|DELETE)/gi
    ];

    sqlPatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('SQL injection pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    // Check for command injection
    const cmdPatterns = [
      /[;&|`$(){}[\]]/,
      /\/bin\/sh/,
      /cmd\.exe/,
      /powershell/i,
      /bash/i,
      /sh\s+-c/i
    ];

    cmdPatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('Command injection pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    // Check for LDAP injection
    const ldapPatterns = [
      /\*\)/,
      /\)\s*\(/,
      /ldap:\/\/.*/i,
      /ldaps:\/\/.*/i
    ];

    ldapPatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('LDAP injection pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    // Check for NoSQL injection
    const nosqlPatterns = [
      /\$\s*where/gi,
      /\$\s*ne/gi,
      /\$\s*gt/gi,
      /\$\s*lt/gi,
      /\{\s*\$ne/gi
    ];

    nosqlPatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('NoSQL injection pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    // Check for template injection
    const templatePatterns = [
      /\$\{.*?\}/g,
      /\{\{.*?\}\}/g,
      /\{%.*?%\}/g,
      /\{\#.*?\#\}/g
    ];

    templatePatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('Template injection pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    // Check for path traversal
    const pathTraversalPatterns = [
      /\.\.\//g,
      /\.\.\\/,
      /~\//g,
      /~\\/,
      /\/etc\//,
      /C:\\Windows\\/,
      /%2e%2e%2f/gi,
      /%2e%2e\\/gi
    ];

    pathTraversalPatterns.forEach(pattern => {
      if (pattern.test(input)) {
        threats.push('Path traversal pattern detected');
        sanitized = sanitized.replace(pattern, '');
      }
    });

    return {
      valid: threats.length === 0,
      sanitized: sanitized !== input ? sanitized : undefined,
      error: threats.length > 0 ? `Security threats detected: ${threats.join(', ')}` : undefined,
      threats: threats.length > 0 ? threats : undefined
    };
  }

  /**
   * Validate media file for security compliance
   */
  async validateMediaFile(file: {
    type: string;
    size: number;
  }): Promise<ValidationResult> {
    const threats: string[] = [];
    const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    const ALLOWED_TYPES = [
      'image/jpeg',
      'image/png',
      'image/gif',
      'image/webp',
      'application/pdf',
      'text/plain'
    ];

    // Check file size
    if (file.size > MAX_FILE_SIZE) {
      threats.push('File size exceeds maximum limit');
    }

    // Check file type
    if (!ALLOWED_TYPES.includes(file.type)) {
      threats.push('File type not allowed');
    }

    // Check for suspicious file types
    const suspiciousTypes = [
      'application/x-executable',
      'application/x-sharedlib',
      'application/x-object',
      'application/x-shellscript',
      'text/x-php',
      'text/x-python',
      'application/javascript',
      'application/x-javascript'
    ];

    if (suspiciousTypes.includes(file.type)) {
      threats.push('Suspicious file type detected');
    }

    return {
      valid: threats.length === 0,
      error: threats.length > 0 ? threats.join(', ') : undefined,
      threats: threats.length > 0 ? threats : undefined
    };
  }

  /**
   * Scan media for security threats
   */
  async scanMedia(media: MediaFile): Promise<{
    scanned: boolean;
    threatsDetected: string[];
    safeToProcess: boolean;
    scanTime: number;
  }> {
    const startTime = Date.now();
    const threats: string[] = [];

    try {
      // Check for suspicious file extensions
      const suspiciousExtensions = [
        '.exe', '.bat', '.cmd', '.com', '.scr', '.pif',
        '.js', '.vbs', '.wsf', '.ps1', '.sh', '.php',
        '.asp', '.aspx', '.jsp', '.cgi', '.pl'
      ];

      const fileName = media.path.toLowerCase();
      const hasSuspiciousExtension = suspiciousExtensions.some(ext =>
        fileName.endsWith(ext)
      );

      if (hasSuspiciousExtension) {
        threats.push('Suspicious file extension detected');
      }

      // Check for embedded scripts in files (basic check)
      if (media.type.startsWith('image/') || media.type.includes('pdf')) {
        const scriptPatterns = [
          /<script[^>]*>.*?<\/script>/gi,
          /javascript:/gi,
          /eval\((.*?)\)/gi,
          /document\.write/gi,
          /document\.cookie/gi
        ];

        // In a real implementation, you would actually read and scan the file content
        // For now, we'll simulate the scan
        const fileContent = await this.simulateFileContent(media);

        scriptPatterns.forEach(pattern => {
          if (pattern.test(fileContent)) {
            threats.push('Embedded script detected');
          }
        });
      }

      // Check for file signature mismatches
      const signatureMismatch = await this.checkFileSignature(media);
      if (signatureMismatch) {
        threats.push('File signature mismatch detected');
      }

      const scanTime = Date.now() - startTime;

      return {
        scanned: true,
        threatsDetected: threats,
        safeToProcess: threats.length === 0,
        scanTime
      };
    } catch (error) {
      logger.error('Media scan failed', {
        mediaId: media.id,
        error: error instanceof Error ? error.message : 'Unknown error'
      });

      return {
        scanned: false,
        threatsDetected: ['Scan failed'],
        safeToProcess: false,
        scanTime: Date.now() - startTime
      };
    }
  }

  /**
   * Encrypt sensitive data at rest
   */
  async encryptData(data: Record<string, unknown>): Promise<{
    encryptedData: string;
    iv: string;
    tag: string;
    algorithm: string;
  }> {
    try {
      const iv = randomBytes(16);
      const key = await this.deriveKey(this.encryptionKey);
      const cipher = createCipheriv(this.algorithm, key, iv);

      const jsonData = JSON.stringify(data);
      let encrypted = cipher.update(jsonData, 'utf8', 'hex');
      encrypted += cipher.final('hex');

      const tag = cipher.getAuthTag();

      return {
        encryptedData: encrypted,
        iv: iv.toString('hex'),
        tag: tag.toString('hex'),
        algorithm: this.algorithm
      };
    } catch (error) {
      logger.error('Data encryption failed', {
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Decrypt encrypted data
   */
  async decryptData(encrypted: {
    encryptedData: string;
    iv: string;
    tag: string;
    algorithm: string;
  }): Promise<Record<string, unknown>> {
    try {
      const key = await this.deriveKey(this.encryptionKey);
      const iv = Buffer.from(encrypted.iv, 'hex');
      const tag = Buffer.from(encrypted.tag, 'hex');

      const decipher = createDecipheriv(encrypted.algorithm, key, iv);
      decipher.setAuthTag(tag);

      let decrypted = decipher.update(encrypted.encryptedData, 'hex', 'utf8');
      decrypted += decipher.final('utf8');

      return JSON.parse(decrypted);
    } catch (error) {
      logger.error('Data decryption failed', {
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Log operation for audit trail
   */
  async logOperation(operation: {
    type: string;
    data: Record<string, unknown>;
  }): Promise<{
    id: string;
    success: boolean;
    correlationId: string;
    consistent: boolean;
    timestamp: Date;
  }> {
    const correlationId = this.generateCorrelationId();
    const timestamp = new Date();

    try {
      await this.logAuditTrail({
        type: operation.type,
        data: {
          ...operation.data,
          correlationId
        },
        timestamp: timestamp.getTime()
      });

      return {
        id: this.generateEventId(),
        success: true,
        correlationId,
        consistent: true,
        timestamp
      };
    } catch (error) {
      logger.error('Operation logging failed', {
        operationType: operation.type,
        correlationId,
        error: error instanceof Error ? error.message : 'Unknown error'
      });

      return {
        id: this.generateEventId(),
        success: false,
        correlationId,
        consistent: false,
        timestamp
      };
    }
  }

  /**
   * Check if IP address is blocked
   */
  isIPBlocked(ipAddress: string): boolean {
    return this.blockedIPs.has(ipAddress);
  }

  /**
   * Block IP address
   */
  blockIP(ipAddress: string, reason?: string): void {
    this.blockedIPs.add(ipAddress);
    logger.warn('IP address blocked', { ipAddress, reason });
  }

  /**
   * Record suspicious activity
   */
  recordSuspiciousActivity(source: string): void {
    const current = this.suspiciousActivities.get(source) || 0;
    this.suspiciousActivities.set(source, current + 1);

    if (current + 1 >= 5) { // Block after 5 suspicious activities
      this.blockIP(source, 'Excessive suspicious activities');
    }
  }

  // Helper methods
  private generateEventId(): string {
    return `evt_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  private generateCorrelationId(): string {
    return `corr_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  private async deriveKey(password: string): Promise<Buffer> {
    return new Promise((resolve, reject) => {
      scrypt(password, 'salt', 32, (err, derivedKey) => {
        if (err) reject(err);
        else resolve(derivedKey);
      });
    });
  }

  private async simulateFileContent(media: MediaFile): Promise<string> {
    // In a real implementation, this would read and return the actual file content
    // For security testing purposes, we'll return a sample content
    return 'Sample file content for scanning';
  }

  private async checkFileSignature(media: MediaFile): Promise<boolean> {
    // In a real implementation, this would check the file's magic number
    // against its declared MIME type
    return false; // Placeholder
  }
}

// Export singleton instance
export const securityService = new SecurityService();