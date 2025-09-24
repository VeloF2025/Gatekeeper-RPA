import { z } from 'zod';
import { ValidationResult } from '@/types';

// Phone number validation schema
const phoneSchema = z.string()
  .regex(/^\+[1-9]\d{1,14}$/, 'Invalid phone number format')
  .transform(val => val.replace(/\s+/g, '').trim());

// DR number validation schema
const drNumberSchema = z.string()
  .regex(/^DR\d{7}$/, 'DR number must be in format DR1234567')
  .transform(val => val.toUpperCase());

// Email validation schema
const emailSchema = z.string()
  .email('Invalid email format')
  .transform(val => val.toLowerCase().trim());

// Ticket validation schema
export const ticketSchema = z.object({
  drNumber: drNumberSchema,
  technicianNumber: phoneSchema,
  technicianName: z.string().min(1, 'Technician name is required').max(100),
  messageContent: z.string().min(1, 'Message content is required').max(1000),
  priority: z.enum(['low', 'normal', 'high', 'urgent']).optional().default('normal')
});

// User validation schema
export const userSchema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters').max(50),
  email: emailSchema,
  password: z.string().min(8, 'Password must be at least 8 characters'),
  role: z.enum(['admin', 'auditor', 'technician', 'viewer'])
});

// Audit result validation schema
export const auditResultSchema = z.object({
  ticketId: z.string().uuid('Invalid ticket ID'),
  propertyId: z.string().min(1, 'Property ID is required'),
  jobId: z.string().min(1, 'Job ID is required'),
  address: z.string().min(1, 'Address is required'),
  installationStatus: z.string().min(1, 'Installation status is required'),
  photosRequired: z.array(z.enum(['trench', 'ONT', 'termination', 'property'])),
  photosFound: z.array(z.enum(['trench', 'ONT', 'termination', 'property', 'additional'])),
  photosMissing: z.array(z.enum(['trench', 'ONT', 'termination', 'property'])),
  complianceScore: z.number().min(0).max(100),
  auditDetails: z.object({
    totalPhotos: z.number().min(0),
    auditTimestamp: z.string(),
    photos: z.array(z.object({
      id: z.string().optional(),
      type: z.enum(['trench', 'ONT', 'termination', 'property', 'additional']),
      url: z.string().url(),
      uploadTime: z.string(),
      fileSize: z.number().min(0)
    })),
    compliance: z.array(z.object({
      type: z.string(),
      passed: z.boolean(),
      message: z.string(),
      score: z.number().min(0).max(100)
    }))
  })
});

// WhatsApp message validation schema
export const whatsappMessageSchema = z.object({
  from: phoneSchema,
  to: phoneSchema.optional(),
  body: z.string().min(1, 'Message body is required').max(1000),
  type: z.enum(['text', 'image', 'document', 'location']).optional().default('text'),
  timestamp: z.string().datetime().optional()
});

// Input sanitization utilities
export class InputSanitizer {
  /**
   * Sanitize string input to prevent XSS
   */
  static sanitizeString(input: string): string {
    return input
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;')
      .replace(/\//g, '&#x2F;');
  }

  /**
   * Sanitize HTML content
   */
  static sanitizeHTML(html: string): string {
    return html
      .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
      .replace(/<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi, '')
      .replace(/on\w+="[^"]*"/g, '')
      .replace(/javascript:/gi, '');
  }

  /**
   * Validate and sanitize DR number
   */
  static validateDRNumber(drNumber: string): string | null {
    try {
      return drNumberSchema.parse(drNumber);
    } catch {
      return null;
    }
  }

  /**
   * Validate and sanitize phone number
   */
  static validatePhoneNumber(phoneNumber: string): string | null {
    try {
      return phoneSchema.parse(phoneNumber);
    } catch {
      return null;
    }
  }

  /**
   * Validate and sanitize email
   */
  static validateEmail(email: string): string | null {
    try {
      return emailSchema.parse(email);
    } catch {
      return null;
    }
  }

  /**
   * Extract DR number from text
   */
  static extractDRNumber(text: string): string | null {
    const drMatch = text.match(/\bDR\d{7}\b/i);
    return drMatch ? drMatch[0].toUpperCase() : null;
  }

  /**
   * Validate file upload
   */
  static validateFile(file: Express.Multer.File, allowedTypes: string[], maxSize: number): ValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];

    if (!file) {
      errors.push('No file provided');
      return { isValid: false, errors, warnings };
    }

    if (file.size > maxSize) {
      errors.push(`File size exceeds maximum allowed size of ${maxSize / 1024 / 1024}MB`);
    }

    if (!allowedTypes.includes(file.mimetype)) {
      errors.push(`File type ${file.mimetype} is not allowed. Allowed types: ${allowedTypes.join(', ')}`);
    }

    return {
      isValid: errors.length === 0,
      errors,
      warnings
    };
  }
}

// Validation utilities
export class ValidationUtils {
  /**
   * Validate input against schema
   */
  static validate<T>(data: unknown, schema: z.ZodSchema<T>): ValidationResult {
    try {
      schema.parse(data);
      return { isValid: true, errors: [], warnings: [] };
    } catch (error) {
      if (error instanceof z.ZodError) {
        const errors = error.errors.map(err => `${err.path.join('.')}: ${err.message}`);
        return { isValid: false, errors, warnings: [] };
      }
      return {
        isValid: false,
        errors: ['Unknown validation error'],
        warnings: []
      };
    }
  }

  /**
   * Validate multiple inputs
   */
  static validateMultiple(validations: Array<{
    data: unknown;
    schema: z.ZodSchema;
    field: string;
  }>): ValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];

    for (const validation of validations) {
      const result = this.validate(validation.data, validation.schema);
      if (!result.isValid) {
        errors.push(...result.errors.map(err => `${validation.field}: ${err}`));
      }
      warnings.push(...result.warnings);
    }

    return {
      isValid: errors.length === 0,
      errors,
      warnings
    };
  }

  /**
   * Validate required fields
   */
  static validateRequired(data: Record<string, unknown>, requiredFields: string[]): ValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];

    for (const field of requiredFields) {
      if (data[field] === undefined || data[field] === null || data[field] === '') {
        errors.push(`${field} is required`);
      }
    }

    return {
      isValid: errors.length === 0,
      errors,
      warnings
    };
  }

  /**
   * Validate enum values
   */
  static validateEnum(value: unknown, enumValues: readonly string[], fieldName: string): ValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];

    if (!enumValues.includes(value as string)) {
      errors.push(`${fieldName} must be one of: ${enumValues.join(', ')}`);
    }

    return {
      isValid: errors.length === 0,
      errors,
      warnings
    };
  }
}

export {
  phoneSchema,
  drNumberSchema,
  emailSchema
};