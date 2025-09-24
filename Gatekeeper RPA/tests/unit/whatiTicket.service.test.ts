import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { WhatiTicketService } from '@/services/whatiTicketService';
import { securityService } from '@/services/securityService';
import { db } from '@/database/database';
import { eq, and, desc } from 'drizzle-orm';
import { tickets, auditPhotos, workflowEvents } from '@/database/schema';

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
    insert: vi.fn().mockReturnValue({
      values: vi.fn().mockReturnValue({
        returning: vi.fn().mockReturnValue([{
          id: 'ticket-123',
          ticketNumber: 'TICKET-20250923-1234',
          drNumber: 'DR12345678',
          technicianNumber: '+27821234567',
          status: 'pending',
          priority: 'normal',
          externalSystem: 'whatiTicket',
          syncStatus: 'pending',
          createdAt: new Date(),
          updatedAt: new Date()
        }])
      })
    }),
    select: vi.fn().mockReturnValue({
      from: vi.fn().mockReturnValue({
        where: vi.fn().mockReturnValue({
          limit: vi.fn().mockReturnValue([])
        })
      })
    }),
    execute: vi.fn().mockResolvedValue([])
  }
}));

// Mock schema
vi.mock('@/database/schema', () => ({
  tickets: 'tickets',
  auditPhotos: 'auditPhotos',
  workflowEvents: 'workflowEvents',
  eq: vi.fn(),
  and: vi.fn(),
  desc: vi.fn()
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

describe('WhatiTicketService', () => {
  let service: WhatiTicketService;
  let mockSecurityService: any;
  let mockDb: any;

  beforeEach(() => {
    vi.clearAllMocks();
    service = new WhatiTicketService();
    mockSecurityService = securityService;
    mockDb = db;

    // Setup default mock returns
    mockSecurityService.sanitizeMessage.mockImplementation((msg: string) => msg);
    mockSecurityService.validateInput.mockResolvedValue({ valid: true });
    mockSecurityService.validateMediaFile.mockResolvedValue({ valid: true });
    mockSecurityService.scanMedia.mockResolvedValue({ safeToProcess: true, threatsDetected: [] });
    mockSecurityService.logAuditTrail.mockResolvedValue();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('Installation', () => {
    const validConfig = {
      version: '1.0.0',
      database: 'postgres',
      redis: true,
      security: {
        encryption: 'aes-256-gcm',
        authentication: 'jwt'
      }
    };

    it('should install WhatiTicket successfully with valid configuration', async () => {
      const result = await service.install(validConfig);

      expect(result.success).toBe(true);
      expect(result.version).toBe('1.0.0');
      expect(result.databaseConnected).toBe(true);
      expect(result.redisConnected).toBe(true);
      expect(result.securityConfigured).toBe(true);

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatiTicket_installed',
        data: expect.objectContaining({
          version: '1.0.0',
          database: 'postgres',
          redis: true,
          securityEnabled: true
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should reject invalid installation configuration', async () => {
      const invalidConfig = {
        version: '',
        database: 'postgres',
        redis: true,
        security: {
          encryption: '',
          authentication: 'jwt'
        }
      };

      const result = await service.install(invalidConfig);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Invalid installation configuration');

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'whatiTicket_install_failed',
        data: expect.objectContaining({
          error: 'Invalid installation configuration'
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should handle database connection failure', async () => {
      mockDb.execute.mockRejectedValue(new Error('Database connection failed'));

      const result = await service.install(validConfig);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Database connection failed');
      expect(result.databaseConnected).toBe(false);
    });

    it('should handle Redis connection failure gracefully', async () => {
      // Mock Redis test to fail
      const originalTestRedis = (service as any).testRedisConnection;
      (service as any).testRedisConnection = vi.fn().mockResolvedValue(false);

      const result = await service.install(validConfig);

      expect(result.success).toBe(true);
      expect(result.redisConnected).toBe(false);

      // Restore original method
      (service as any).testRedisConnection = originalTestRedis;
    });
  });

  describe('Complete Workflow Processing', () => {
    const validMessage = {
      id: 'msg-123',
      from: '+27821234567',
      content: 'DR12345678 Please check this installation',
      timestamp: Date.now(),
      messageType: 'text' as const
    };

    const messageWithMedia = {
      ...validMessage,
      messageType: 'image' as const,
      media: [{
        id: 'media-123',
        type: 'image',
        mimeType: 'image/jpeg',
        fileSize: 1024000,
        url: 'https://example.com/image.jpg'
      }]
    };

    it('should process complete workflow successfully', async () => {
      const result = await service.processCompleteWorkflow(validMessage);

      expect(result.success).toBe(true);
      expect(result.ticketId).toBe('ticket-123');
      expect(result.mediaProcessed).toBe(false);
      expect(result.responseSent).toBe(true);
      expect(result.confidence).toBeGreaterThan(0.9);
      expect(result.auditTrail).toHaveLength(7);

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'ticket_created',
        data: expect.objectContaining({
          ticketId: 'ticket-123',
          drNumber: 'DR12345678',
          source: 'whatsapp'
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });

    it('should process workflow with media successfully', async () => {
      const result = await service.processCompleteWorkflow(messageWithMedia);

      expect(result.success).toBe(true);
      expect(result.ticketId).toBe('ticket-123');
      expect(result.mediaProcessed).toBe(true);
      expect(result.responseSent).toBe(true);
      expect(result.auditTrail).toHaveLength(8);
    });

    it('should handle message validation failure', async () => {
      mockSecurityService.validateInput.mockResolvedValue({
        valid: false,
        error: 'Suspicious content detected'
      });

      const result = await service.processCompleteWorkflow(validMessage);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Suspicious content detected');
      expect(result.confidence).toBeLessThan(0.8);
    });

    it('should handle missing DR number', async () => {
      const messageWithoutDR = {
        ...validMessage,
        content: 'Hello world'
      };

      const result = await service.processCompleteWorkflow(messageWithoutDR);

      expect(result.success).toBe(false);
      expect(result.error).toBe('No DR number found in message');
      expect(result.confidence).toBeLessThan(0.9);
    });

    it('should detect and handle duplicate tickets', async () => {
      // Mock duplicate ticket found
      mockDb.select.mockReturnValue({
        from: vi.fn().mockReturnValue({
          where: vi.fn().mockReturnValue({
            limit: vi.fn().mockReturnValue([{
              id: 'existing-ticket',
              drNumber: 'DR12345678',
              technicianNumber: '+27821234567'
            }])
          })
        })
      });

      const result = await service.processCompleteWorkflow(validMessage);

      expect(result.success).toBe(true);
      expect(result.ticketId).toBe('existing-ticket');
      expect(result.mediaProcessed).toBe(false);
      expect(result.confidence).toBeLessThan(0.7);
    });

    it('should handle media processing failure', async () => {
      mockSecurityService.validateMediaFile.mockResolvedValue({
        valid: false,
        error: 'File too large'
      });

      const result = await service.processCompleteWorkflow(messageWithMedia);

      expect(result.success).toBe(true);
      expect(result.mediaProcessed).toBe(false);
      expect(result.confidence).toBeLessThan(0.95);
    });

    it('should handle auto-response failure', async () => {
      // Mock auto-response to fail
      const originalSendAutoResponse = (service as any).sendAutoResponse;
      (service as any).sendAutoResponse = vi.fn().mockResolvedValue(false);

      const result = await service.processCompleteWorkflow(validMessage);

      expect(result.success).toBe(true);
      expect(result.responseSent).toBe(false);
      expect(result.confidence).toBeLessThan(0.9);

      // Restore original method
      (service as any).sendAutoResponse = originalSendAutoResponse;
    });
  });

  describe('Message Validation', () => {
    const validMessage = {
      id: 'msg-123',
      from: '+27821234567',
      content: 'DR12345678',
      timestamp: Date.now(),
      messageType: 'text' as const
    };

    it('should validate message with valid content', async () => {
      const result = await (service as any).validateMessage(validMessage);

      expect(result.isValid).toBe(true);
      expect(result.sanitized).toEqual(validMessage);
    });

    it('should reject message with invalid structure', async () => {
      const invalidMessage = {
        ...validMessage,
        id: ''
      };

      const result = await (service as any).validateMessage(invalidMessage);

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Invalid message structure');
    });

    it('should reject message with invalid phone number', async () => {
      const invalidMessage = {
        ...validMessage,
        from: 'invalid-phone'
      };

      const result = await (service as any).validateMessage(invalidMessage);

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Invalid phone number format');
    });

    it('should reject message that exceeds length limit', async () => {
      const longMessage = {
        ...validMessage,
        content: 'a'.repeat(6000)
      };

      const result = await (service as any).validateMessage(longMessage);

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Message too long');
    });

    it('should detect and reject suspicious content', async () => {
      const suspiciousMessage = {
        ...validMessage,
        content: 'DR12345678 <script>alert("xss")</script>'
      };

      const result = await (service as any).validateMessage(suspiciousMessage);

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Message contains suspicious content');
    });

    it('should apply rate limiting', async () => {
      // Mock rate limit exceeded
      const originalCheckRateLimit = (service as any).checkRateLimit;
      (service as any).checkRateLimit = vi.fn().mockResolvedValue(false);

      const result = await (service as any).validateMessage(validMessage);

      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Rate limit exceeded');

      // Restore original method
      (service as any).checkRateLimit = originalCheckRateLimit;
    });

    it('should sanitize message content', async () => {
      const sanitizedContent = 'DR12345678 sanitized';
      mockSecurityService.sanitizeMessage.mockReturnValue(sanitizedContent);

      const result = await (service as any).validateMessage(validMessage);

      expect(result.isValid).toBe(true);
      expect(result.sanitized?.content).toBe(sanitizedContent);
    });
  });

  describe('DR Number Extraction', () => {
    it('should extract DR number from simple format', () => {
      const result = (service as any).extractDRNumber('Please check DR12345678');
      expect(result).toBe('12345678');
    });

    it('should extract DR number with space', () => {
      const result = (service as any).extractDRNumber('Please check DR 12345678');
      expect(result).toBe('12345678');
    });

    it('should extract DR number from lowercase', () => {
      const result = (service as any).extractDRNumber('please check dr12345678');
      expect(result).toBe('12345678');
    });

    it('should extract DR number from drop reference', () => {
      const result = (service as any).extractDRNumber('Drop Reference 12345678');
      expect(result).toBe('12345678');
    });

    it('should extract DR number from installation', () => {
      const result = (service as any).extractDRNumber('Installation 12345678');
      expect(result).toBe('12345678');
    });

    it('should extract DR number from job number', () => {
      const result = (service as any).extractDRNumber('Job 12345678');
      expect(result).toBe('12345678');
    });

    it('should normalize DR number to 8 digits', () => {
      const result = (service as any).extractDRNumber('DR123');
      expect(result).toBe('00000123');
    });

    it('should return null for no DR number found', () => {
      const result = (service as any).extractDRNumber('Hello world');
      expect(result).toBeNull();
    });

    it('should return null for invalid format', () => {
      const result = (service as any).extractDRNumber('DRABC123');
      expect(result).toBeNull();
    });
  });

  describe('Media Processing', () => {
    const validMediaItems = [
      {
        id: 'media-123',
        type: 'image',
        mimeType: 'image/jpeg',
        fileSize: 1024000,
        url: 'https://example.com/image.jpg'
      }
    ];

    it('should process valid media items successfully', async () => {
      const result = await (service as any).processMedia(validMediaItems, 'ticket-123');

      expect(result.success).toBe(true);
      expect(result.processed).toBe(1);

      expect(mockDb.insert).toHaveBeenCalledWith(auditPhotos);
    });

    it('should reject invalid media files', async () => {
      mockSecurityService.validateMediaFile.mockResolvedValue({
        valid: false,
        error: 'File type not supported'
      });

      const result = await (service as any).processMedia(validMediaItems, 'ticket-123');

      expect(result.success).toBe(false);
      expect(result.processed).toBe(0);
    });

    it('should reject media with security threats', async () => {
      mockSecurityService.scanMedia.mockResolvedValue({
        safeToProcess: false,
        threatsDetected: ['malware detected']
      });

      const result = await (service as any).processMedia(validMediaItems, 'ticket-123');

      expect(result.success).toBe(false);
      expect(result.processed).toBe(0);
    });

    it('should handle processing errors gracefully', async () => {
      mockDb.insert.mockRejectedValue(new Error('Database error'));

      const result = await (service as any).processMedia(validMediaItems, 'ticket-123');

      expect(result.success).toBe(false);
      expect(result.processed).toBe(0);
    });

    it('should process multiple media items', async () => {
      const multipleMediaItems = [
        ...validMediaItems,
        {
          id: 'media-456',
          type: 'document',
          mimeType: 'application/pdf',
          fileSize: 2048000,
          url: 'https://example.com/document.pdf'
        }
      ];

      const result = await (service as any).processMedia(multipleMediaItems, 'ticket-123');

      expect(result.success).toBe(true);
      expect(result.processed).toBe(2);
    });
  });

  describe('Ticket Creation', () => {
    const validMessage = {
      id: 'msg-123',
      from: '+27821234567',
      content: 'DR12345678',
      timestamp: Date.now(),
      messageType: 'text' as const
    };

    it('should create ticket with valid data', async () => {
      const result = await (service as any).createTicketFromMessage(validMessage, 'DR12345678');

      expect(result).toEqual(expect.objectContaining({
        ticketNumber: expect.stringMatching(/^TICKET-\d{8}-\d{4}$/),
        drNumber: 'DR12345678',
        technicianNumber: '+27821234567',
        status: 'pending',
        priority: 'normal',
        externalSystem: 'whatiTicket',
        syncStatus: 'pending'
      }));

      expect(mockDb.insert).toHaveBeenCalledWith(tickets);
    });

    it('should determine priority from message content', async () => {
      const urgentMessage = {
        ...validMessage,
        content: 'DR12345678 URGENT emergency situation'
      };

      const result = await (service as any).createTicketFromMessage(urgentMessage, 'DR12345678');

      expect(result.priority).toBe('urgent');
    });

    it('should log ticket creation event', async () => {
      await (service as any).createTicketFromMessage(validMessage, 'DR12345678');

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith({
        type: 'ticket_created',
        data: expect.objectContaining({
          drNumber: 'DR12345678',
          source: 'whatsapp'
        }),
        timestamp: expect.any(Number),
        userId: 'system'
      });
    });
  });

  describe('Health Status', () => {
    it('should return healthy status when all services are connected', async () => {
      const result = await service.getHealthStatus();

      expect(result.status).toBe('healthy');
      expect(result.services.database).toBe('connected');
      expect(result.services.redis).toBe('connected');
      expect(result.services.whatsapp).toBe('connected');
      expect(result.services.security).toBe('enabled');
      expect(result.metrics.uptime).toBeGreaterThan(0);
    });

    it('should return degraded status when database is disconnected', async () => {
      mockDb.execute.mockRejectedValue(new Error('Database connection failed'));

      const result = await service.getHealthStatus();

      expect(result.status).toBe('degraded');
      expect(result.services.database).toBe('disconnected');
    });

    it('should return unhealthy status on error', async () => {
      mockDb.execute.mockImplementation(() => {
        throw new Error('Unexpected error');
      });

      const result = await service.getHealthStatus();

      expect(result.status).toBe('unhealthy');
      expect(result.services.database).toBe('disconnected');
      expect(result.services.redis).toBe('disconnected');
      expect(result.services.whatsapp).toBe('disconnected');
    });
  });

  describe('Security and Rate Limiting', () => {
    it('should validate phone number format', () => {
      const serviceInstance = service as any;

      expect(serviceInstance.validatePhoneNumber('+27821234567')).toBe(true);
      expect(serviceInstance.validatePhoneNumber('27821234567')).toBe(true);
      expect(serviceInstance.validatePhoneNumber('0821234567')).toBe(true);
      expect(serviceInstance.validatePhoneNumber('invalid')).toBe(false);
      expect(serviceInstance.validatePhoneNumber('123')).toBe(false);
    });

    it('should detect suspicious patterns in content', () => {
      const serviceInstance = service as any;

      expect(serviceInstance.containsSuspiciousPatterns('<script>alert("xss")</script>')).toBe(true);
      expect(serviceInstance.containsSuspiciousPatterns('javascript:alert("xss")')).toBe(true);
      expect(serviceInstance.containsSuspiciousPatterns('data:text/html')).toBe(true);
      expect(serviceInstance.containsSuspiciousPatterns('union select * from users')).toBe(true);
      expect(serviceInstance.containsSuspiciousPatterns('Normal message content')).toBe(false);
    });

    it('should apply rate limiting correctly', async () => {
      const serviceInstance = service as any;
      const phoneNumber = '+27821234567';

      // First request should pass
      let result = await serviceInstance.checkRateLimit(phoneNumber);
      expect(result).toBe(true);

      // Second request should pass
      result = await serviceInstance.checkRateLimit(phoneNumber);
      expect(result).toBe(true);

      // Simulate rate limit exceeded
      for (let i = 0; i < 10; i++) {
        await serviceInstance.checkRateLimit(phoneNumber);
      }

      result = await serviceInstance.checkRateLimit(phoneNumber);
      expect(result).toBe(false);
    });
  });
});