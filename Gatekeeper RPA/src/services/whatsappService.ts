import { db } from '@/database/database';
import { tickets, workflowEvents } from '@/database/schemas';
import { eq, and } from 'drizzle-orm';
import { logger } from '@/lib/logger';
import { securityService } from './securityService';
import axios from 'axios';

export interface WhatsAppConfig {
  phoneNumberId: string;
  accessToken: string;
  businessAccountId: string;
  webhookUrl: string;
  verifyToken: string;
  apiVersion: string;
}

export interface WhatsAppMessage {
  from: string;
  body: string;
  timestamp: number;
  messageType: 'text' | 'image' | 'document' | 'audio' | 'video';
  messageId?: string;
  mediaId?: string;
  mimeType?: string;
  caption?: string;
}

export interface MediaItem {
  id: string;
  type: string;
  mimeType: string;
  fileSize: number;
  url?: string;
  caption?: string;
}

export interface WebhookPayload {
  object: string;
  entry: Array<{
    id: string;
    changes: Array<{
      value: {
        messaging_product: string;
        metadata: {
          display_phone_number: string;
          phone_number_id: string;
        };
        contacts?: Array<{
          wa_id: string;
          profile: {
            name: string;
          };
        }>;
        messages: Array<{
          from: string;
          id: string;
          timestamp: string;
          text?: {
            body: string;
          };
          image?: {
            caption?: string;
            mime_type: string;
            sha256: string;
            id: string;
          };
          document?: {
            caption?: string;
            mime_type: string;
            sha256: string;
            id: string;
            filename: string;
          };
          audio?: {
            mime_type: string;
            sha256: string;
            id: string;
          };
          video?: {
            caption?: string;
            mime_type: string;
            sha256: string;
            id: string;
          };
        }>;
      };
      field: string;
    }>;
  }>;
}

export class WhatsAppService {
  private config: WhatsAppConfig | null = null;
  private apiBase: string;
  private rateLimiter: Map<string, { count: number; resetTime: number }> = new Map();

  constructor() {
    this.apiBase = 'https://graph.facebook.com/v18.0';
  }

  /**
   * Initialize WhatsApp Business API with secure connection
   * Follows Zero Trust principles - validate all inputs
   */
  async initialize(config: WhatsAppConfig): Promise<{
    success: boolean;
    webhookConfigured: boolean;
    apiVersion: string;
    rateLimits: {
      messagesPerSecond: number;
      messagesPerDay: number;
    };
    error?: string;
  }> {
    try {
      // Validate configuration
      const validationResult = await this.validateConfig(config);
      if (!validationResult.valid) {
        throw new Error(validationResult.error);
      }

      this.config = config;

      logger.info('Initializing WhatsApp Business API', {
        phoneNumberId: config.phoneNumberId,
        apiVersion: config.apiVersion
      });

      // Test API connection
      const apiTest = await this.testApiConnection();
      if (!apiTest.success) {
        throw new Error(apiTest.error || 'API connection failed');
      }

      // Configure webhook
      const webhookResult = await this.configureWebhook();
      if (!webhookResult.success) {
        throw new Error(webhookResult.error || 'Webhook configuration failed');
      }

      // Log initialization event
      await securityService.logAuditTrail({
        type: 'whatsapp_api_initialized',
        data: {
          phoneNumberId: config.phoneNumberId,
          apiVersion: config.apiVersion,
          webhookConfigured: webhookResult.success
        },
        timestamp: Date.now(),
        userId: 'system'
      });

      return {
        success: true,
        webhookConfigured: webhookResult.success,
        apiVersion: config.apiVersion,
        rateLimits: {
          messagesPerSecond: 50,
          messagesPerDay: 1000
        }
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      logger.error('WhatsApp API initialization failed', { error: errorMessage });

      await securityService.logAuditTrail({
        type: 'whatsapp_api_init_failed',
        data: {
          error: errorMessage,
          config: this.sanitizeConfigForLogging(config)
        },
        timestamp: Date.now(),
        userId: 'system'
      });

      return {
        success: false,
        webhookConfigured: false,
        apiVersion: config.apiVersion || 'unknown',
        rateLimits: {
          messagesPerSecond: 0,
          messagesPerDay: 0
        },
        error: errorMessage
      };
    }
  }

  /**
   * Validate incoming WhatsApp webhook requests
   */
  async validateWebhub(hubChallenge: string, hubVerifyToken: string): Promise<boolean> {
    try {
      if (!this.config) {
        logger.error('WhatsApp service not initialized');
        return false;
      }

      // Verify token matches
      if (hubVerifyToken !== this.config.verifyToken) {
        logger.warn('Invalid webhook verify token');
        return false;
      }

      // Validate challenge format
      if (!hubChallenge || hubChallenge.length === 0) {
        logger.warn('Invalid webhook challenge');
        return false;
      }

      logger.info('Webhook validation successful');
      return true;
    } catch (error) {
      logger.error('Webhook validation failed', {
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      return false;
    }
  }

  /**
   * Process incoming WhatsApp message securely
   */
  async processMessage(message: WhatsAppMessage): Promise<{
    id: string;
    processed: boolean;
    validated: boolean;
    sanitized: boolean;
    drNumber?: string;
    error?: string;
    retryCount: number;
    status: 'success' | 'failed' | 'retry_scheduled';
  }> {
    const messageId = message.messageId || this.generateMessageId();
    let retryCount = 0;

    try {
      logger.info('Processing WhatsApp message', {
        messageId,
        from: message.from,
        messageType: message.messageType
      });

      // Step 1: Rate limiting
      if (!await this.checkRateLimit(message.from)) {
        throw new Error('Rate limit exceeded');
      }

      // Step 2: Validate message structure
      const validation = await this.validateMessageStructure(message);
      if (!validation.valid) {
        throw new Error(validation.error);
      }

      // Step 3: Sanitize message content
      const sanitizedContent = await securityService.sanitizeMessage(message.body);

      // Step 4: Extract DR number
      const drNumber = this.extractDRNumber(sanitizedContent);

      // Step 5: Check for duplicates
      const duplicateCheck = await this.checkForDuplicate(message.from, drNumber);
      if (duplicateCheck.isDuplicate) {
        logger.info('Duplicate message detected', {
          messageId,
          from: message.from,
          originalMessageId: duplicateCheck.originalMessageId
        });
      }

      // Step 6: Log message processing
      await securityService.logAuditTrail({
        type: 'whatsapp_message_processed',
        data: {
          messageId,
          from: message.from,
          messageType: message.messageType,
          hasDR: !!drNumber,
          isDuplicate: duplicateCheck.isDuplicate
        },
        timestamp: Date.now(),
        userId: 'system'
      });

      return {
        id: messageId,
        processed: true,
        validated: true,
        sanitized: true,
        drNumber,
        retryCount,
        status: 'success'
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      logger.error('WhatsApp message processing failed', {
        messageId,
        from: message.from,
        error: errorMessage
      });

      // Determine if we should retry
      const shouldRetry = await this.shouldRetry(errorMessage, retryCount);
      const status = shouldRetry ? 'retry_scheduled' : 'failed';

      if (shouldRetry) {
        retryCount++;
        await this.scheduleRetry(message, retryCount);
      }

      return {
        id: messageId,
        processed: false,
        validated: false,
        sanitized: false,
        retryCount,
        status,
        error: errorMessage
      };
    }
  }

  /**
   * Parse message content to extract structured data
   */
  async parseMessage(content: string): Promise<{
    drNumber?: string;
    content: string;
    isValid: boolean;
    categories: string[];
    priority: 'low' | 'normal' | 'high' | 'urgent';
  }> {
    try {
      const sanitizedContent = await securityService.sanitizeMessage(content);
      const drNumber = this.extractDRNumber(sanitizedContent);
      const categories = this.categorizeMessage(sanitizedContent);
      const priority = this.determinePriority(sanitizedContent);

      return {
        drNumber,
        content: sanitizedContent,
        isValid: true,
        categories,
        priority
      };
    } catch (error) {
      logger.error('Message parsing failed', {
        content: content.substring(0, 100),
        error: error instanceof Error ? error.message : 'Unknown error'
      });

      return {
        content,
        isValid: false,
        categories: [],
        priority: 'normal'
      };
    }
  }

  /**
   * Validate message format and content
   */
  async validateMessage(message: string): Promise<{
    isValid: boolean;
    drNumber?: string;
    error?: string;
    warnings?: string[];
  }> {
    const warnings: string[] = [];

    try {
      // Basic validation
      if (!message || message.trim().length === 0) {
        return { isValid: false, error: 'Message is empty' };
      }

      if (message.length > 5000) {
        return { isValid: false, error: 'Message too long' };
      }

      // Sanitize and validate content
      const sanitized = await securityService.sanitizeMessage(message);
      const validation = await securityService.validateInput(sanitized);

      if (!validation.valid) {
        return {
          isValid: false,
          error: validation.error,
          warnings: validation.threats
        };
      }

      // Extract DR number
      const drNumber = this.extractDRNumber(sanitized);

      // Additional validations
      if (sanitized.toLowerCase().includes('test') && sanitized.length < 10) {
        warnings.push('Possible test message');
      }

      if (!drNumber && !sanitized.toLowerCase().includes('help') && !sanitized.toLowerCase().includes('support')) {
        warnings.push('No DR number found and not a support request');
      }

      return {
        isValid: true,
        drNumber,
        warnings: warnings.length > 0 ? warnings : undefined
      };
    } catch (error) {
      logger.error('Message validation failed', {
        message: message.substring(0, 100),
        error: error instanceof Error ? error.message : 'Unknown error'
      });

      return {
        isValid: false,
        error: 'Validation failed'
      };
    }
  }

  /**
   * Process media files from WhatsApp
   */
  async processMedia(mediaMessage: {
    from: string;
    mediaId: string;
    mediaType: string;
    mimeType: string;
    fileSize: number;
    timestamp: number;
  }): Promise<{
    id: string;
    mediaType: string;
    fileSize: number;
    storagePath?: string;
    virusScanned: boolean;
    threats?: string[];
    error?: string;
  }> {
    const mediaId = this.generateMediaId();

    try {
      logger.info('Processing WhatsApp media', {
        mediaId,
        from: mediaMessage.from,
        mediaType: mediaMessage.mediaType,
        mimeType: mediaMessage.mimeType,
        fileSize: mediaMessage.fileSize
      });

      // Validate media file
      const validation = await securityService.validateMediaFile({
        type: mediaMessage.mimeType,
        size: mediaMessage.fileSize
      });

      if (!validation.valid) {
        throw new Error(validation.error);
      }

      // Download media from WhatsApp API
      const mediaData = await this.downloadMedia(mediaMessage.mediaId);

      // Scan for threats
      const scanResult = await securityService.scanMedia({
        id: mediaId,
        path: mediaData.filePath,
        size: mediaMessage.fileSize,
        type: mediaMessage.mimeType
      });

      if (!scanResult.safeToProcess) {
        throw new Error(`Media security scan failed: ${scanResult.threatsDetected.join(', ')}`);
      }

      // Store media securely
      const storagePath = await this.storeMediaSecurely(mediaData, mediaMessage.mimeType);

      // Log media processing
      await securityService.logAuditTrail({
        type: 'whatsapp_media_processed',
        data: {
          mediaId,
          from: mediaMessage.from,
          mediaType: mediaMessage.mediaType,
          fileSize: mediaMessage.fileSize,
          threatsDetected: scanResult.threatsDetected,
          scanTime: scanResult.scanTime
        },
        timestamp: Date.now(),
        userId: 'system'
      });

      return {
        id: mediaId,
        mediaType: mediaMessage.mediaType,
        fileSize: mediaMessage.fileSize,
        storagePath,
        virusScanned: true,
        threats: scanResult.threatsDetected.length > 0 ? scanResult.threatsDetected : undefined
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      logger.error('WhatsApp media processing failed', {
        mediaId,
        from: mediaMessage.from,
        error: errorMessage
      });

      return {
        id: mediaId,
        mediaType: mediaMessage.mediaType,
        fileSize: mediaMessage.fileSize,
        virusScanned: false,
        error: errorMessage
      };
    }
  }

  /**
   * Send response message via WhatsApp
   */
  async sendResponse(to: string, message: string): Promise<{
    success: boolean;
    messageId?: string;
    error?: string;
  }> {
    try {
      if (!this.config) {
        throw new Error('WhatsApp service not initialized');
      }

      // Rate limiting check
      if (!await this.checkRateLimit(to)) {
        throw new Error('Rate limit exceeded');
      }

      // Validate and sanitize message
      const sanitized = await securityService.sanitizeMessage(message);
      if (!sanitized || sanitized.length === 0) {
        throw new Error('Invalid message content');
      }

      const url = `${this.apiBase}/${this.config.phoneNumberId}/messages`;
      const headers = {
        'Authorization': `Bearer ${this.config.accessToken}`,
        'Content-Type': 'application/json'
      };

      const payload = {
        messaging_product: 'whatsapp',
        to: to,
        text: {
          body: sanitized
        }
      };

      const response = await axios.post(url, payload, { headers });
      const messageId = response.data?.messages?.[0]?.id;

      logger.info('WhatsApp response sent', {
        to,
        messageId,
        messageLength: sanitized.length
      });

      return {
        success: true,
        messageId
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      logger.error('Failed to send WhatsApp response', {
        to,
        error: errorMessage
      });

      return {
        success: false,
        error: errorMessage
      };
    }
  }

  // Helper methods
  private async validateConfig(config: WhatsAppConfig): Promise<{
    valid: boolean;
    error?: string;
  }> {
    if (!config.phoneNumberId) {
      return { valid: false, error: 'Phone number ID is required' };
    }

    if (!config.accessToken) {
      return { valid: false, error: 'Access token is required' };
    }

    if (!config.businessAccountId) {
      return { valid: false, error: 'Business account ID is required' };
    }

    if (!config.webhookUrl) {
      return { valid: false, error: 'Webhook URL is required' };
    }

    if (!config.verifyToken) {
      return { valid: false, error: 'Verify token is required' };
    }

    return { valid: true };
  }

  private async testApiConnection(): Promise<{
    success: boolean;
    error?: string;
  }> {
    try {
      if (!this.config) {
        return { success: false, error: 'Configuration not set' };
      }

      const url = `${this.apiBase}/${this.config.phoneNumberId}/`;
      const headers = {
        'Authorization': `Bearer ${this.config.accessToken}`
      };

      await axios.get(url, { headers });
      return { success: true };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      return { success: false, error: errorMessage };
    }
  }

  private async configureWebhook(): Promise<{
    success: boolean;
    error?: string;
  }> {
    try {
      if (!this.config) {
        return { success: false, error: 'Configuration not set' };
      }

      logger.info('Configuring WhatsApp webhook', {
        webhookUrl: this.config.webhookUrl
      });

      // In a real implementation, this would make API calls to configure the webhook
      // For now, we'll simulate successful configuration
      return { success: true };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      return { success: false, error: errorMessage };
    }
  }

  private async validateMessageStructure(message: WhatsAppMessage): Promise<{
    valid: boolean;
    error?: string;
  }> {
    if (!message.from || !message.from.match(/^\d+$/)) {
      return { valid: false, error: 'Invalid phone number format' };
    }

    if (!message.body || message.body.trim().length === 0) {
      return { valid: false, error: 'Message body is empty' };
    }

    if (!message.messageType) {
      return { valid: false, error: 'Message type is required' };
    }

    const validTypes = ['text', 'image', 'document', 'audio', 'video'];
    if (!validTypes.includes(message.messageType)) {
      return { valid: false, error: 'Invalid message type' };
    }

    return { valid: true };
  }

  private extractDRNumber(content: string): string | null {
    const patterns = [
      /\bDR\s*(\d{5,8})\b/i,
      /\bDR(\d{5,8})\b/i,
      /\bDrop\s*Ref(?:erence)?\s*(\d{5,8})\b/i,
      /\bInstallation\s*(\d{5,8})\b/i,
      /\bJob\s*(\d{5,8})\b/i,
    ];

    for (const pattern of patterns) {
      const match = content.match(pattern);
      if (match && match[1]) {
        return match[1].padStart(8, '0');
      }
    }

    return null;
  }

  private categorizeMessage(content: string): string[] {
    const categories: string[] = [];
    const lowerContent = content.toLowerCase();

    if (lowerContent.includes('urgent') || lowerContent.includes('emergency')) {
      categories.push('urgent');
    }

    if (lowerContent.includes('install') || lowerContent.includes('setup')) {
      categories.push('installation');
    }

    if (lowerContent.includes('problem') || lowerContent.includes('issue') || lowerContent.includes('error')) {
      categories.push('problem');
    }

    if (lowerContent.includes('help') || lowerContent.includes('support')) {
      categories.push('support');
    }

    if (lowerContent.includes('photo') || lowerContent.includes('picture') || lowerContent.includes('image')) {
      categories.push('photo');
    }

    return categories;
  }

  private determinePriority(content: string): 'low' | 'normal' | 'high' | 'urgent' {
    const lowerContent = content.toLowerCase();

    if (lowerContent.includes('urgent') || lowerContent.includes('emergency')) {
      return 'urgent';
    }

    if (lowerContent.includes('high') || lowerContent.includes('critical')) {
      return 'high';
    }

    if (lowerContent.includes('low') || lowerContent.includes('minor')) {
      return 'low';
    }

    return 'normal';
  }

  private async checkRateLimit(phoneNumber: string): Promise<boolean> {
    const now = Date.now();
    const windowMs = 60 * 1000; // 1 minute
    const maxRequests = 10; // 10 requests per minute

    const key = `whatsapp_rate_limit:${phoneNumber}`;
    const record = this.rateLimiter.get(key);

    if (!record) {
      this.rateLimiter.set(key, { count: 1, resetTime: now + windowMs });
      return true;
    }

    if (now > record.resetTime) {
      this.rateLimiter.set(key, { count: 1, resetTime: now + windowMs });
      return true;
    }

    if (record.count >= maxRequests) {
      return false;
    }

    record.count++;
    return true;
  }

  private async checkForDuplicate(phoneNumber: string, drNumber?: string): Promise<{
    isDuplicate: boolean;
    originalMessageId?: string;
  }> {
    try {
      if (!drNumber) {
        return { isDuplicate: false };
      }

      const recentThreshold = new Date(Date.now() - 24 * 60 * 60 * 1000); // 24 hours ago

      const result = await db.select()
        .from(tickets)
        .where(and(
          eq(tickets.drNumber, drNumber),
          eq(tickets.technicianNumber, phoneNumber),
          // @ts-ignore - Date comparison
          tickets.createdAt > recentThreshold
        ))
        .limit(1);

      if (result[0]) {
        return {
          isDuplicate: true,
          originalMessageId: result[0].whatsappMessageId
        };
      }

      return { isDuplicate: false };
    } catch (error) {
      logger.error('Duplicate check failed', {
        phoneNumber,
        drNumber,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      return { isDuplicate: false };
    }
  }

  private async shouldRetry(errorMessage: string, retryCount: number): Promise<boolean> {
    const retryableErrors = [
      'network error',
      'timeout',
      'rate limit',
      'temporary',
      'unavailable'
    ];

    const isRetryable = retryableErrors.some(error =>
      errorMessage.toLowerCase().includes(error)
    );

    return isRetryable && retryCount < 3;
  }

  private async scheduleRetry(message: WhatsAppMessage, retryCount: number): Promise<void> {
    const delay = Math.pow(2, retryCount) * 1000; // Exponential backoff

    setTimeout(async () => {
      logger.info('Retrying message processing', {
        messageId: message.messageId,
        retryCount
      });

      await this.processMessage(message);
    }, delay);
  }

  private async downloadMedia(mediaId: string): Promise<{
    filePath: string;
    data: Buffer;
  }> {
    if (!this.config) {
      throw new Error('WhatsApp service not initialized');
    }

    const url = `${this.apiBase}/${mediaId}`;
    const headers = {
      'Authorization': `Bearer ${this.config.accessToken}`
    };

    const response = await axios.get(url, { headers, responseType: 'arraybuffer' });
    const data = Buffer.from(response.data);
    const filePath = `/tmp/whatsapp_media_${mediaId}`;

    return { filePath, data };
  }

  private async storeMediaSecurely(data: { data: Buffer }, mimeType: string): Promise<string> {
    // In a real implementation, this would store the media securely
    // For now, we'll return a placeholder path
    const fileName = `media_${Date.now()}_${Math.random().toString(36).substr(2, 9)}.${mimeType.split('/')[1]}`;
    return `/secure_storage/${fileName}`;
  }

  private sanitizeConfigForLogging(config: WhatsAppConfig): Partial<WhatsAppConfig> {
    return {
      phoneNumberId: config.phoneNumberId,
      businessAccountId: config.businessAccountId,
      webhookUrl: config.webhookUrl,
      apiVersion: config.apiVersion
      // Exclude sensitive data like accessToken and verifyToken
    };
  }

  private generateMessageId(): string {
    return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  private generateMediaId(): string {
    return `media_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }
}

export const whatsappService = new WhatsAppService();