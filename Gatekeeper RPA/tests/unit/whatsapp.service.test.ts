import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { WhatsAppService } from '@/services/whatsappService';
import { securityService } from '@/services/securityService';
import { db } from '@/database/database';
import { eq, and } from 'drizzle-orm';
import { tickets } from '@/database/schema';

// Mock security service
vi.mock('@/services/securityService', () => ({
  securityService: {
    sanitizeMessage: vi.fn(),
    validateInput: vi.fn(),
    validateMediaFile: vi.fn(),
    scanMedia: vi.fn(),
    logAuditTrail: vi.fn()
  }
}));

// Mock database
vi.mock('@/database/database', () => ({
  db: {
    select: vi.fn(),
    insert: vi.fn()
  }
}));

// Mock schema
vi.mock('@/database/schema', () => ({
  tickets: {},
  eq: vi.fn(),
  and: vi.fn()
}));

// Mock logger
vi.mock('@/lib/logger', () => ({
  logger: {
    info: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    debug: vi.fn()
  }
}));

// Mock axios
vi.mock('axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn()
  }
}));

describe('WhatsAppService', () => {
  let service: WhatsAppService;
  let mockSecurityService: any;
  let mockDb: any;
  let mockAxios: any;

  beforeEach(() => {
    vi.clearAllMocks();
    service = new WhatsAppService();
    mockSecurityService = securityService;
    mockDb = db;
    mockAxios = require('axios').default;

    // Setup default mock returns
    mockSecurityService.sanitizeMessage.mockImplementation((msg: string) => msg);
    mockSecurityService.validateInput.mockResolvedValue({ valid: true });
    mockSecurityService.validateMediaFile.mockResolvedValue({ valid: true });
    mockSecurityService.scanMedia.mockResolvedValue({ safeToProcess: true, threatsDetected: [] });
    mockSecurityService.logAuditTrail.mockResolvedValue();

    mockDb.select.mockReturnValue({
      from: vi.fn().mockReturnValue({
        where: vi.fn().mockReturnValue({
          limit: vi.fn().mockReturnValue([])
        })
      })
    });

    mockDb.insert.mockReturnValue({
      returning: vi.fn().mockReturnValue([{
        id: 'ticket-123',
        ticketNumber: 'TICKET-20250923-1234',
        drNumber: 'DR12345678',
        technicianNumber: '+27821234567',
        status: 'pending'
      }])
    });

    mockAxios.get.mockResolvedValue({});
    mockAxios.post.mockResolvedValue({
      data: { messages: [{ id: 'msg-123' }] }
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('Initialization', () => {
    const validConfig = {
      phoneNumberId: 'phone-123',
      accessToken: 'token-123',
      businessAccountId: 'business-123',
      webhookUrl: 'https://example.com/webhook',
      verifyToken: 'verify-token-123',
      apiVersion: 'v18.0'
    };

    it('should initialize WhatsApp service successfully', async () => {
      const result = await service.initialize(validConfig);

      expect(result.success).toBe(true);
      expect(result.webhookConfigured).toBe(true);
      expect(result.apiVersion).toBe('v18.0');
      expect(result.rateLimits.messagesPerSecond).toBe(50);
      expect(result.rateLimits.messagesPerDay).toBe(1000);

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatsapp_api_initialized',
        data: expect.objectContaining({
          phoneNumberId: 'phone-123',
          apiVersion: 'v18.0',
          webhookConfigured: true
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should reject invalid configuration', async () => {
      const invalidConfig = {
        ...validConfig,
        phoneNumberId: ''
      };

      const result = await service.initialize(invalidConfig);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Phone number ID is required');

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatsapp_api_init_failed',
        data: expect.objectContaining({
          error: 'Phone number ID is required'
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should handle API connection failure', async () => {
      mockAxios.get.mockRejectedValue(new Error('API connection failed'));

      const result = await service.initialize(validConfig);

      expect(result.success).toBe(false);
      expect(result.error).toBe('API connection failed');
    });

    it('should handle webhook configuration failure', async () => {
      // Mock webhook configuration to fail
      const originalConfigureWebhook = (service as any).configureWebhook;
      (service as any).configureWebhook = vi.fn().mockResolvedValue({
        success: false,
        error: 'Webhook configuration failed'
      });

      const result = await service.initialize(validConfig);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Webhook configuration failed');

      // Restore original method
      (service as any).configureWebhook = originalConfigureWebhook;
    });
  });

  describe('Webhook Validation', () => {
    beforeEach(async () => {
      await service.initialize({
        phoneNumberId: 'phone-123',
        accessToken: 'token-123',
        businessAccountId: 'business-123',
        webhookUrl: 'https://example.com/webhook',
        verifyToken: 'verify-token-123',
        apiVersion: 'v18.0'
      });
    });

    it('should validate webhook with correct token', async () => {
      const result = await service.validateWebhub('challenge-123', 'verify-token-123');

      expect(result).toBe(true);
    });

    it('should reject webhook with incorrect token', async () => {
      const result = await service.validateWebhub('challenge-123', 'wrong-token');

      expect(result).toBe(false);
    });

    it('should reject webhook with empty challenge', async () => {
      const result = await service.validateWebhub('', 'verify-token-123');

      expect(result).toBe(false);
    });

    it('should reject webhook when service not initialized', async () => {
      // Create new service instance without initialization
      const newService = new WhatsAppService();
      const result = await newService.validateWebhub('challenge-123', 'verify-token-123');

      expect(result).toBe(false);
    });
  });

  describe('Message Processing', () => {
    const validMessage = {
      from: '+27821234567',
      body: 'DR12345678',
      timestamp: Date.now(),
      messageType: 'text' as const,
      messageId: 'msg-123'
    };

    beforeEach(async () => {
      await service.initialize({
        phoneNumberId: 'phone-123',
        accessToken: 'token-123',
        businessAccountId: 'business-123',
        webhookUrl: 'https://example.com/webhook',
        verifyToken: 'verify-token-123',
        apiVersion: 'v18.0'
      });
    });

    it('should process message successfully', async () => {
      const result = await service.processMessage(validMessage);

      expect(result.processed).toBe(true);
      expect(result.validated).toBe(true);
      expect(result.sanitized).toBe(true);
      expect(result.drNumber).toBe('DR12345678');
      expect(result.status).toBe('success');
      expect(result.retryCount).toBe(0);

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatsapp_message_processed',
        data: expect.objectContaining({
          messageId: 'msg-123',
          from: '+27821234567',
          messageType: 'text',
          hasDR: true,
          isDuplicate: false
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should handle rate limiting', async () => {
      // Mock rate limit exceeded
      const originalCheckRateLimit = (service as any).checkRateLimit;
      (service as any).checkRateLimit = vi.fn().mockResolvedValue(false);

      const result = await service.processMessage(validMessage);

      expect(result.processed).toBe(false);
      expect(result.status).toBe('failed');
      expect(result.error).toBe('Rate limit exceeded');

      // Restore original method
      (service as any).checkRateLimit = originalCheckRateLimit;
    });

    it('should handle message validation failure', async () => {
      // Mock validation to fail
      const originalValidateMessageStructure = (service as any).validateMessageStructure;
      (service as any).validateMessageStructure = vi.fn().mockResolvedValue({
        valid: false,
        error: 'Invalid message structure'
      });

      const result = await service.processMessage(validMessage);

      expect(result.processed).toBe(false);
      expect(result.status).toBe('failed');
      expect(result.error).toBe('Invalid message structure');

      // Restore original method
      (service as any).validateMessageStructure = originalValidateMessageStructure;
    });

    it('should detect duplicate messages', async () => {
      // Mock duplicate found
      mockDb.select.mockReturnValue({
        from: vi.fn().mockReturnValue({
          where: vi.fn().mockReturnValue({
            limit: vi.fn().mockReturnValue([{
              id: 'existing-ticket',
              drNumber: 'DR12345678',
              technicianNumber: '+27821234567',
              whatsappMessageId: 'existing-msg'
            }])
          })
        })
      });

      const result = await service.processMessage(validMessage);

      expect(result.processed).toBe(true);
      expect(result.status).toBe('success');

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatsapp_message_processed',
        data: expect.objectContaining({
          isDuplicate: true,
          originalMessageId: 'existing-msg'
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should handle retry for temporary errors', async () => {
      // Mock temporary failure
      const originalShouldRetry = (service as any).shouldRetry;
      const originalScheduleRetry = (service as any).scheduleRetry;

      (service as any).shouldRetry = vi.fn().mockResolvedValue(true);
      (service as any).scheduleRetry = vi.fn();

      const result = await service.processMessage({
        ...validMessage,
        body: 'DR12345678'
      });

      expect(result.processed).toBe(false);
      expect(result.status).toBe('retry_scheduled');
      expect(result.retryCount).toBe(1);

      expect((service as any).scheduleRetry).toHaveBeenCalledWith(
        expect.objectContaining({
          from: '+27821234567',
          body: 'DR12345678'
        }),
        1
      );

      // Restore original methods
      (service as any).shouldRetry = originalShouldRetry;
      (service as any).scheduleRetry = originalScheduleRetry;
    });
  });

  describe('Message Parsing', () => {
    it('should parse message and extract DR number', async () => {
      const result = await service.parseMessage('DR12345678 Please check this installation');

      expect(result.drNumber).toBe('DR12345678');
      expect(result.content).toBe('DR12345678 Please check this installation');
      expect(result.isValid).toBe(true);
      expect(result.categories).toEqual([]);
      expect(result.priority).toBe('normal');

      expect(mockSecurityService.sanitizeMessage).toHaveBeenCalledWith(
        'DR12345678 Please check this installation'
      );
    });

    it('should categorize message content', async () => {
      const result = await service.parseMessage('URGENT DR12345678 installation problem with photo');

      expect(result.drNumber).toBe('DR12345678');
      expect(result.categories).toEqual(['urgent', 'installation', 'problem', 'photo']);
      expect(result.priority).toBe('urgent');
    });

    it('should handle parsing errors gracefully', async () => {
      mockSecurityService.sanitizeMessage.mockRejectedValue(new Error('Sanitization failed'));

      const result = await service.parseMessage('DR12345678');

      expect(result.isValid).toBe(false);
      expect(result.categories).toEqual([]);
      expect(result.priority).toBe('normal');
    });
  });

  describe('Message Validation', () => {
    it('should validate message with DR number', async () => {
      const result = await service.validateMessage('DR12345678');

      expect(result.isValid).toBe(true);
      expect(result.drNumber).toBe('DR12345678');
    });

    it('should reject empty message', async () => {
      const result = await service.validateMessage('');

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Message is empty');
    });

    it('should reject message that is too long', async () => {
      const longMessage = 'a'.repeat(6000);
      const result = await service.validateMessage(longMessage);

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Message too long');
    });

    it('should reject message with security threats', async () => {
      mockSecurityService.validateInput.mockResolvedValue({
        valid: false,
        error: 'Suspicious content detected',
        threats: ['xss attempt']
      });

      const result = await service.validateMessage('DR12345678 <script>alert("xss")</script>');

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Suspicious content detected');
      expect(result.warnings).toEqual(['xss attempt']);
    });

    it('should include warnings for test messages', async () => {
      const result = await service.validateMessage('test');

      expect(result.isValid).toBe(true);
      expect(result.warnings).toEqual(['Possible test message']);
    });

    it('should include warnings for messages without DR numbers', async () => {
      const result = await service.validateMessage('Hello world');

      expect(result.isValid).toBe(true);
      expect(result.warnings).toEqual(['No DR number found and not a support request']);
    });
  });

  describe('Media Processing', () => {
    const validMediaMessage = {
      from: '+27821234567',
      mediaId: 'media-123',
      mediaType: 'image',
      mimeType: 'image/jpeg',
      fileSize: 1024000,
      timestamp: Date.now()
    };

    beforeEach(async () => {
      await service.initialize({
        phoneNumberId: 'phone-123',
        accessToken: 'token-123',
        businessAccountId: 'business-123',
        webhookUrl: 'https://example.com/webhook',
        verifyToken: 'verify-token-123',
        apiVersion: 'v18.0'
      });

      // Mock media download
      mockAxios.get.mockResolvedValue({
        data: Buffer.from('test image data')
      });
    });

    it('should process media successfully', async () => {
      const result = await service.processMedia(validMediaMessage);

      expect(result.id).toBeDefined();
      expect(result.mediaType).toBe('image');
      expect(result.fileSize).toBe(1024000);
      expect(result.storagePath).toBeDefined();
      expect(result.virusScanned).toBe(true);

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatsapp_media_processed',
        data: expect.objectContaining({
          from: '+27821234567',
          mediaType: 'image',
          fileSize: 1024000,
          threatsDetected: []
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should reject invalid media files', async () => {
      mockSecurityService.validateMediaFile.mockResolvedValue({
        valid: false,
        error: 'File type not supported'
      });

      const result = await service.processMedia(validMediaMessage);

      expect(result.error).toBe('File type not supported');
      expect(result.virusScanned).toBe(false);
    });

    it('should reject media with security threats', async () => {
      mockSecurityService.scanMedia.mockResolvedValue({
        safeToProcess: false,
        threatsDetected: ['malware detected'],
        scanTime: 100
      });

      const result = await service.processMedia(validMediaMessage);

      expect(result.error).toBe('Media security scan failed: malware detected');
      expect(result.virusScanned).toBe(false);
      expect(result.threats).toEqual(['malware detected']);
    });

    it('should handle media download failure', async () => {
      mockAxios.get.mockRejectedValue(new Error('Download failed'));

      const result = await service.processMedia(validMediaMessage);

      expect(result.error).toBe('Download failed');
      expect(result.virusScanned).toBe(false);
    });
  });

  describe('Sending Responses', () => {
    beforeEach(async () => {
      await service.initialize({
        phoneNumberId: 'phone-123',
        accessToken: 'token-123',
        businessAccountId: 'business-123',
        webhookUrl: 'https://example.com/webhook',
        verifyToken: 'verify-token-123',
        apiVersion: 'v18.0'
      });
    });

    it('should send response successfully', async () => {
      const result = await service.sendResponse('+27821234567', 'Test message');

      expect(result.success).toBe(true);
      expect(result.messageId).toBe('msg-123');

      expect(mockAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/messages'),
        {
          messaging_product: 'whatsapp',
          to: '+27821234567',
          text: {
            body: 'Test message'
          }
        },
        {
          Authorization: 'Bearer token-123',
          'Content-Type': 'application/json'
        }
      );
    });

    it('should reject when service not initialized', async () => {
      const newService = new WhatsAppService();
      const result = await newService.sendResponse('+27821234567', 'Test message');

      expect(result.success).toBe(false);
      expect(result.error).toBe('WhatsApp service not initialized');
    });

    it('should handle rate limiting', async () => {
      // Mock rate limit exceeded
      const originalCheckRateLimit = (service as any).checkRateLimit;
      (service as any).checkRateLimit = vi.fn().mockResolvedValue(false);

      const result = await service.sendResponse('+27821234567', 'Test message');

      expect(result.success).toBe(false);
      expect(result.error).toBe('Rate limit exceeded');

      // Restore original method
      (service as any).checkRateLimit = originalCheckRateLimit;
    });

    it('should sanitize message content', async () => {
      mockSecurityService.sanitizeMessage.mockReturnValue('Sanitized message');

      await service.sendResponse('+27821234567', '<script>alert("xss")</script>Test');

      expect(mockSecurityService.sanitizeMessage).toHaveBeenCalledWith('<script>alert("xss")</script>Test');
      expect(mockAxios.post).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({
          text: {
            body: 'Sanitized message'
          }
        }),
        expect.any(Object)
      );
    });

    it('should handle API errors', async () => {
      mockAxios.post.mockRejectedValue(new Error('API error'));

      const result = await service.sendResponse('+27821234567', 'Test message');

      expect(result.success).toBe(false);
      expect(result.error).toBe('API error');
    });
  });

  describe('Helper Methods', () => {
    it('should extract DR number correctly', () => {
      const serviceInstance = service as any;

      expect(serviceInstance.extractDRNumber('DR12345678')).toBe('DR12345678');
      expect(serviceInstance.extractDRNumber('DR 12345678')).toBe('DR12345678');
      expect(serviceInstance.extractDRNumber('dr12345678')).toBe('DR12345678');
      expect(serviceInstance.extractDRNumber('Drop Reference 12345678')).toBe('DR12345678');
      expect(serviceInstance.extractDRNumber('Installation 12345678')).toBe('DR12345678');
      expect(serviceInstance.extractDRNumber('Job 12345678')).toBe('DR12345678');
      expect(serviceInstance.extractDRNumber('Hello world')).toBeNull();
    });

    it('should categorize messages correctly', () => {
      const serviceInstance = service as any;

      expect(serviceInstance.categorizeMessage('URGENT problem with installation')).toEqual(['urgent', 'problem', 'installation']);
      expect(serviceInstance.categorizeMessage('Help needed with photo')).toEqual(['help', 'support', 'photo']);
      expect(serviceInstance.categorizeMessage('Normal message')).toEqual([]);
    });

    it('should determine priority correctly', () => {
      const serviceInstance = service as any;

      expect(serviceInstance.determinePriority('URGENT emergency')).toBe('urgent');
      expect(serviceInstance.determinePriority('HIGH critical issue')).toBe('high');
      expect(serviceInstance.determinePriority('LOW minor problem')).toBe('low');
      expect(serviceInstance.determinePriority('Normal message')).toBe('normal');
    });

    it('should validate phone number format', () => {
      const serviceInstance = service as any;

      expect(serviceInstance.validatePhoneNumber('+27821234567')).toBe(true);
      expect(serviceInstance.validatePhoneNumber('27821234567')).toBe(true);
      expect(serviceInstance.validatePhoneNumber('0821234567')).toBe(true);
      expect(serviceInstance.validatePhoneNumber('invalid')).toBe(false);
      expect(serviceInstance.validatePhoneNumber('123')).toBe(false);
    });

    it('should handle rate limiting correctly', async () => {
      const serviceInstance = service as any;
      const phoneNumber = '+27821234567';

      // First few requests should pass
      for (let i = 0; i < 9; i++) {
        const result = await serviceInstance.checkRateLimit(phoneNumber);
        expect(result).toBe(true);
      }

      // 11th request should be rate limited
      const result = await serviceInstance.checkRateLimit(phoneNumber);
      expect(result).toBe(false);
    });

    it('should detect duplicate tickets correctly', async () => {
      const serviceInstance = service as any;

      // Mock no duplicates found
      mockDb.select.mockReturnValue({
        from: vi.fn().mockReturnValue({
          where: vi.fn().mockReturnValue({
            limit: vi.fn().mockReturnValue([])
          })
        })
      });

      const result = await serviceInstance.checkForDuplicate('+27821234567', 'DR12345678');
      expect(result.isDuplicate).toBe(false);

      // Mock duplicate found
      mockDb.select.mockReturnValue({
        from: vi.fn().mockReturnValue({
          where: vi.fn().mockReturnValue({
            limit: vi.fn().mockReturnValue([{
              id: 'existing-ticket',
              whatsappMessageId: 'existing-msg'
            }])
          })
        })
      });

      const result2 = await serviceInstance.checkForDuplicate('+27821234567', 'DR12345678');
      expect(result2.isDuplicate).toBe(true);
      expect(result2.originalMessageId).toBe('existing-msg');
    });
  });
});