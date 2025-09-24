/**
 * Comprehensive Input Validation and Sanitization
 * Advanced security against injection attacks and malformed data
 */

import { logger } from '@/utils/logger';

export interface ValidationRule {
  required?: boolean;
  type?: 'string' | 'number' | 'boolean' | 'email' | 'url' | 'uuid' | 'date' | 'object' | 'array';
  min?: number;
  max?: number;
  pattern?: RegExp;
  custom?: (value: any) => boolean | Promise<boolean>;
  sanitize?: boolean;
  allowedValues?: any[];
  forbiddenValues?: any[];
}

export interface ValidationError {
  field: string;
  message: string;
  value: any;
  rule?: string;
}

export interface ValidationSchema {
  [key: string]: ValidationRule;
}

export class InputValidator {
  private xssPatterns: RegExp[] = [
    /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
    /javascript:/gi,
    /on\w+\s*=/gi,
    /eval\((.*)\)/gi,
    /expression\((.*)\)/gi,
    /vbscript:/gi,
    /data:\s*text\/html/gi,
    /<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi,
    /<object\b[^<]*(?:(?!<\/object>)<[^<]*)*<\/object>/gi,
    /<embed\b[^<]*(?:(?!<\/embed>)<[^<]*)*<\/embed>/gi,
  ];

  private sqlInjectionPatterns: RegExp[] = [
    /(\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|TRUNCATE)\b)/gi,
    /(\b(UNION\s+ALL|UNION\s+SELECT)\b)/gi,
    /(\b(OR|AND)\s+\d+\s*=\s*\d+\b)/gi,
    /(\b(XP_|SP_|EXEC)\w*\b)/gi,
    /(;|\b(GO|--)\b)/gi,
    /(\b(0x|'|")\s*OR\s*['"]\s*\d+\s*=\s*\d+)/gi,
  ];

  private commandInjectionPatterns: RegExp[] = [
    /[;&|`$(){}]/g,
    /(\b(rm|chmod|chown|su|sudo|bash|sh|cmd|powershell)\b)/gi,
  ];

  /**
   * Validate input data against schema
   */
  async validate(data: any, schema: ValidationSchema): Promise<{ isValid: boolean; errors: ValidationError[]; sanitized?: any }> {
    const errors: ValidationError[] = [];
    const sanitized: any = {};

    for (const [field, rule] of Object.entries(schema)) {
      const value = data[field];
      const result = await this.validateField(field, value, rule);

      if (!result.isValid) {
        errors.push(...result.errors);
      } else {
        sanitized[field] = result.sanitizedValue;
      }
    }

    return {
      isValid: errors.length === 0,
      errors,
      sanitized: errors.length === 0 ? sanitized : undefined,
    };
  }

  /**
   * Validate single field
   */
  async validateField(field: string, value: any, rule: ValidationRule): Promise<{ isValid: boolean; errors: ValidationError[]; sanitizedValue: any }> {
    const errors: ValidationError[] = [];
    let sanitizedValue = value;

    // Check required fields
    if (rule.required && (value === undefined || value === null || value === '')) {
      errors.push({
        field,
        message: `${field} is required`,
        value,
        rule: 'required',
      });
      return { isValid: false, errors, sanitizedValue };
    }

    // Skip validation for optional empty fields
    if (!rule.required && (value === undefined || value === null || value === '')) {
      return { isValid: true, errors: [], sanitizedValue };
    }

    // Type validation
    if (rule.type && !this.validateType(value, rule.type)) {
      errors.push({
        field,
        message: `${field} must be of type ${rule.type}`,
        value,
        rule: 'type',
      });
    }

    // Length/size validation
    if (rule.min !== undefined && !this.validateMin(value, rule.min, rule.type)) {
      errors.push({
        field,
        message: `${field} must be at least ${rule.min} ${this.getTypeUnit(rule.type)}`,
        value,
        rule: 'min',
      });
    }

    if (rule.max !== undefined && !this.validateMax(value, rule.max, rule.type)) {
      errors.push({
        field,
        message: `${field} must be at most ${rule.max} ${this.getTypeUnit(rule.type)}`,
        value,
        rule: 'max',
      });
    }

    // Pattern validation
    if (rule.pattern && !this.validatePattern(value, rule.pattern)) {
      errors.push({
        field,
        message: `${field} format is invalid`,
        value,
        rule: 'pattern',
      });
    }

    // Allowed values validation
    if (rule.allowedValues && !this.validateAllowedValues(value, rule.allowedValues)) {
      errors.push({
        field,
        message: `${field} must be one of: ${rule.allowedValues.join(', ')}`,
        value,
        rule: 'allowedValues',
      });
    }

    // Forbidden values validation
    if (rule.forbiddenValues && this.validateForbiddenValues(value, rule.forbiddenValues)) {
      errors.push({
        field,
        message: `${field} contains forbidden value`,
        value,
        rule: 'forbiddenValues',
      });
    }

    // Custom validation
    if (rule.custom) {
      try {
        const customResult = await rule.custom(value);
        if (!customResult) {
          errors.push({
            field,
            message: `${field} failed custom validation`,
            value,
            rule: 'custom',
          });
        }
      } catch (error) {
        errors.push({
          field,
          message: `${field} custom validation error: ${error instanceof Error ? error.message : error}`,
          value,
          rule: 'custom',
        });
      }
    }

    // Sanitize if requested and valid
    if (rule.sanitize && errors.length === 0) {
      sanitizedValue = this.sanitizeValue(value, rule.type);
    }

    return {
      isValid: errors.length === 0,
      errors,
      sanitizedValue,
    };
  }

  /**
   * Validate data type
   */
  private validateType(value: any, type: string): boolean {
    switch (type) {
      case 'string':
        return typeof value === 'string';
      case 'number':
        return typeof value === 'number' && !isNaN(value);
      case 'boolean':
        return typeof value === 'boolean';
      case 'email':
        return typeof value === 'string' && this.validateEmail(value);
      case 'url':
        return typeof value === 'string' && this.validateUrl(value);
      case 'uuid':
        return typeof value === 'string' && this.validateUuid(value);
      case 'date':
        return value instanceof Date || !isNaN(Date.parse(value));
      case 'object':
        return typeof value === 'object' && value !== null && !Array.isArray(value);
      case 'array':
        return Array.isArray(value);
      default:
        return true;
    }
  }

  /**
   * Validate minimum value/length
   */
  private validateMin(value: any, min: number, type?: string): boolean {
    switch (type) {
      case 'string':
        return typeof value === 'string' && value.length >= min;
      case 'number':
        return typeof value === 'number' && value >= min;
      case 'array':
        return Array.isArray(value) && value.length >= min;
      default:
        return true;
    }
  }

  /**
   * Validate maximum value/length
   */
  private validateMax(value: any, max: number, type?: string): boolean {
    switch (type) {
      case 'string':
        return typeof value === 'string' && value.length <= max;
      case 'number':
        return typeof value === 'number' && value <= max;
      case 'array':
        return Array.isArray(value) && value.length <= max;
      default:
        return true;
    }
  }

  /**
   * Validate pattern
   */
  private validatePattern(value: any, pattern: RegExp): boolean {
    return typeof value === 'string' && pattern.test(value);
  }

  /**
   * Validate allowed values
   */
  private validateAllowedValues(value: any, allowedValues: any[]): boolean {
    return allowedValues.includes(value);
  }

  /**
   * Validate forbidden values
   */
  private validateForbiddenValues(value: any, forbiddenValues: any[]): boolean {
    return forbiddenValues.includes(value);
  }

  /**
   * Validate email format
   */
  private validateEmail(email: string): boolean {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
  }

  /**
   * Validate URL format
   */
  private validateUrl(url: string): boolean {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Validate UUID format
   */
  private validateUuid(uuid: string): boolean {
    const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    return uuidPattern.test(uuid);
  }

  /**
   * Sanitize value based on type
   */
  private sanitizeValue(value: any, type?: string): any {
    if (typeof value !== 'string') {
      return value;
    }

    let sanitized = value;

    // Remove XSS vulnerabilities
    sanitized = this.sanitizeXss(sanitized);

    // Remove SQL injection patterns
    sanitized = this.sanitizeSqlInjection(sanitized);

    // Remove command injection patterns
    sanitized = this.sanitizeCommandInjection(sanitized);

    // Type-specific sanitization
    switch (type) {
      case 'email':
        sanitized = sanitized.toLowerCase().trim();
        break;
      case 'url':
        sanitized = sanitized.trim();
        break;
      case 'number':
        sanitized = sanitized.replace(/[^\d.-]/g, '');
        break;
    }

    return sanitized;
  }

  /**
   * Sanitize XSS vulnerabilities
   */
  private sanitizeXss(input: string): string {
    let sanitized = input;

    // Remove script tags and JavaScript
    for (const pattern of this.xssPatterns) {
      sanitized = sanitized.replace(pattern, '');
    }

    // HTML entity encoding
    sanitized = sanitized.replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#x27;')
      .replace(/\//g, '&#x2F;');

    return sanitized;
  }

  /**
   * Sanitize SQL injection patterns
   */
  private sanitizeSqlInjection(input: string): string {
    let sanitized = input;

    for (const pattern of this.sqlInjectionPatterns) {
      sanitized = sanitized.replace(pattern, '');
    }

    return sanitized;
  }

  /**
   * Sanitize command injection patterns
   */
  private sanitizeCommandInjection(input: string): string {
    let sanitized = input;

    for (const pattern of this.commandInjectionPatterns) {
      sanitized = sanitized.replace(pattern, '');
    }

    return sanitized;
  }

  /**
   * Get type unit for error messages
   */
  private getTypeUnit(type?: string): string {
    switch (type) {
      case 'string':
        return 'characters';
      case 'number':
        return '';
      case 'array':
        return 'items';
      default:
        return '';
    }
  }

  /**
   * Security check for suspicious input
   */
  securityCheck(input: any, field: string = 'input'): { isSuspicious: boolean; threats: string[] } {
    const threats: string[] = [];

    if (typeof input !== 'string') {
      return { isSuspicious: false, threats: [] };
    }

    // Check for XSS patterns
    for (const pattern of this.xssPatterns) {
      if (pattern.test(input)) {
        threats.push('XSS attempt detected');
        break;
      }
    }

    // Check for SQL injection patterns
    for (const pattern of this.sqlInjectionPatterns) {
      if (pattern.test(input)) {
        threats.push('SQL injection attempt detected');
        break;
      }
    }

    // Check for command injection patterns
    for (const pattern of this.commandInjectionPatterns) {
      if (pattern.test(input)) {
        threats.push('Command injection attempt detected');
        break;
      }
    }

    // Check for suspicious characters
    const suspiciousChars = /[<>\"'&;|`$(){}]/g;
    const matches = input.match(suspiciousChars);
    if (matches && matches.length > 5) {
      threats.push('High number of suspicious characters');
    }

    // Check for very long input
    if (input.length > 10000) {
      threats.push('Input length exceeds reasonable limits');
    }

    if (threats.length > 0) {
      logger.warn('Security threats detected in input', {
        field,
        threats,
        inputLength: input.length,
        inputPreview: input.substring(0, 100),
      });
    }

    return {
      isSuspicious: threats.length > 0,
      threats,
    };
  }

  /**
   * Create common validation schemas
   */
  static schemas = {
    // User registration schema
    userRegistration: {
      email: { required: true, type: 'email', sanitize: true },
      password: {
        required: true,
        type: 'string',
        min: 8,
        max: 128,
        pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
        sanitize: true,
      },
      firstName: { required: true, type: 'string', min: 1, max: 50, sanitize: true },
      lastName: { required: true, type: 'string', min: 1, max: 50, sanitize: true },
    },

    // Login schema
    login: {
      email: { required: true, type: 'email', sanitize: true },
      password: { required: true, type: 'string', sanitize: true },
    },

    // Ticket creation schema
    ticketCreation: {
      title: { required: true, type: 'string', min: 1, max: 200, sanitize: true },
      description: { required: true, type: 'string', min: 1, max: 5000, sanitize: true },
      priority: { required: true, type: 'string', allowedValues: ['low', 'medium', 'high', 'critical'] },
      category: { required: true, type: 'string', min: 1, max: 50, sanitize: true },
    },

    // Audit creation schema
    auditCreation: {
      ticketId: { required: true, type: 'string', pattern: /^[0-9a-f-]+$/ },
      auditType: { required: true, type: 'string', allowedValues: ['compliance', 'security', 'quality', 'performance'] },
      notes: { required: true, type: 'string', min: 1, max: 2000, sanitize: true },
    },

    // WhatsApp message schema
    whatsappMessage: {
      to: { required: true, type: 'string', pattern: /^\+?\d{10,15}$/ },
      message: { required: true, type: 'string', min: 1, max: 1600, sanitize: true },
      messageType: { required: true, type: 'string', allowedValues: ['text', 'image', 'document', 'template'] },
    },
  };
}

// Create singleton instance
export const inputValidator = new InputValidator();

// Export for easy access
export type { ValidationRule, ValidationError, ValidationSchema };
export { InputValidator };