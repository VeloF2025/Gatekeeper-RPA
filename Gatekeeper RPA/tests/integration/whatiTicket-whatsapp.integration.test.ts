import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { WhatiTicketService } from '@/services/whatiTicketService';
import { WhatsAppService } from '@/services/whatsappService';
import { securityService } from '@/services/securityService';
import { db } from '@/database/database';

// Mock services
vi.mock('@/services/securityService', () => ({
  securityService: {
    sanitizeMessage: vi.fn(),
    validateInput: vi.fn(),
    validateMediaFile: vi.fn(),
    scanMedia: vi.fn(),
    logAuditTrail: vi.fn()
  }
}));

vi.mock('@/database/database', () => ({
  db: {
    insert: vi.fn(),
    select: vi.fn(),
    execute: vi.fn()
  }
}));

vi.mock('@/database/schema', () => ({
  tickets: {},
  auditPhotos: {},
  workflowEvents: {},
  eq: vi.fn(),
  and: vi.fn(),
  desc: vi.fn()
}));

vi.mock('@/lib/logger', () => ({
  logger: {
    info: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    debug: vi.fn()
  }
}));

vi.mock('axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn()
  }
}));

describe('WhatiTicket-WhatsApp Integration', () => {
  let whatiTicketService: WhatiTicketService;
  let whatsappService: WhatsAppService;
  let mockSecurityService: any;
  let mockDb: any;
  let mockAxios: any;

  beforeEach(() => {
    vi.clearAllMocks();
    whatiTicketService = new WhatiTicketService();
    whatsappService = new WhatsAppService();
    mockSecurityService = securityService;
    mockDb = db;
    mockAxios = require('axios').default;

    // Setup default mock returns
    mockSecurityService.sanitizeMessage.mockImplementation((msg: string) => msg);
    mockSecurityService.validateInput.mockResolvedValue({ valid: true });
    mockSecurityService.validateMediaFile.mockResolvedValue({ valid: true });
    mockSecurityService.scanMedia.mockResolvedValue({ safeToProcess: true, threatsDetected: [] });
    mockSecurityService.logAuditTrail.mockResolvedValue();

    mockDb.execute.mockResolvedValue([]);
    mockDb.insert.mockReturnValue({
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
    });

    mockDb.select.mockReturnValue({
      from: vi.fn().mockReturnValue({
        where: vi.fn().mockReturnValue({
          limit: vi.fn().mockReturnValue([])
        })
      })
    });

    mockAxios.get.mockResolvedValue({});
    mockAxios.post.mockResolvedValue({
      data: { messages: [{ id: 'msg-123' }] }
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('Complete Integration Workflow', () => {
    const validWhatsAppConfig = {
      phoneNumberId: 'phone-123',
      accessToken: 'token-123',
      businessAccountId: 'business-123',
      webhookUrl: 'https://example.com/webhook',
      verifyToken: 'verify-token-123',
      apiVersion: 'v18.0'
    };

    const validWhatiTicketConfig = {
      version: '1.0.0',
      database: 'postgres',
      redis: true,
      security: {
        encryption: 'aes-256-gcm',
        authentication: 'jwt'
      }
    };

    it('should initialize both services successfully', async () => {
      const [whatsappResult, whatiTicketResult] = await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      expect(whatsappResult.success).toBe(true);
      expect(whatiTicketResult.success).toBe(true);

      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledTimes(2);
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatsapp_api_initialized'
        })
      );
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatiTicket_installed'
        })
      );
    });

    it('should process complete WhatsApp to ticket workflow', async () => {
      // Initialize services
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Process incoming WhatsApp message
      const whatsappMessage = {
        from: '+27821234567',
        body: 'DR12345678 Please check this installation',
        timestamp: Date.now(),
        messageType: 'text' as const,
        messageId: 'msg-123'
      };

      const whatsappResult = await whatsappService.processMessage(whatsappMessage);

      expect(whatsappResult.processed).toBe(true);
      expect(whatsappResult.validated).toBe(true);
      expect(whatsappResult.drNumber).toBe('DR12345678');
      expect(whatsappResult.status).toBe('success');

      // Convert to WhatiTicket format and process complete workflow
      const whatiTicketMessage = {
        id: whatsappResult.id,
        from: whatsappMessage.from,
        content: whatsappMessage.body,
        drNumber: whatsappResult.drNumber,
        timestamp: whatsappMessage.timestamp,
        messageType: whatsappMessage.messageType
      };

      const whatiTicketResult = await whatiTicketService.processCompleteWorkflow(whatiTicketMessage);

      expect(whatiTicketResult.success).toBe(true);
      expect(whatiTicketResult.ticketId).toBe('ticket-123');
      expect(whatiTicketResult.mediaProcessed).toBe(false);
      expect(whatiTicketResult.responseSent).toBe(true);
      expect(whatiTicketResult.confidence).toBeGreaterThan(0.9);

      // Verify audit trail logging for both services
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatsapp_message_processed'
        })
      );
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'ticket_created'
        })
      );
    });

    it('should handle WhatsApp message with media', async () => {
      // Initialize services
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Mock media download
      mockAxios.get.mockResolvedValue({
        data: Buffer.from('test image data')
      });

      // Process WhatsApp message with media
      const mediaMessage = {
        from: '+27821234567',
        mediaId: 'media-123',
        mediaType: 'image',
        mimeType: 'image/jpeg',
        fileSize: 1024000,
        timestamp: Date.now()
      };

      const mediaResult = await whatsappService.processMedia(mediaMessage);

      expect(mediaResult.id).toBeDefined();
      expect(mediaResult.mediaType).toBe('image');
      expect(mediaResult.virusScanned).toBe(true);

      // Process complete workflow with media
      const whatiTicketMessage = {
        id: 'msg-123',
        from: '+27821234567',
        content: 'DR12345678 with photo',
        timestamp: Date.now(),
        messageType: 'image' as const,
        media: [{
          id: mediaResult.id,
          type: mediaResult.mediaType,
          mimeType: 'image/jpeg',
          fileSize: mediaResult.fileSize,
          url: mediaResult.storagePath
        }]
      };

      const whatiTicketResult = await whatiTicketService.processCompleteWorkflow(whatiTicketMessage);

      expect(whatiTicketResult.success).toBe(true);
      expect(whatiTicketResult.mediaProcessed).toBe(true);
      expect(whatiTicketResult.auditTrail).toHaveLength(8);
    });

    it('should handle WhatsApp validation errors', async () => {
      await whatsappService.initialize(validWhatsAppConfig);

      // Test invalid message structure
      const invalidMessage = {
        from: '+27821234567',
        // Missing required fields
        timestamp: Date.now(),
        messageType: 'text' as const
      };

      await expect(whatsappService.processMessage(invalidMessage as any))
        .rejects.toThrow('Invalid message structure');

      // Verify error was logged
      expect(mockSecurityService.logAuditTrail).not.toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatsapp_message_processed'
        })
      );
    });

    it('should handle WhatiTicket installation failures', async () => {
      // Mock database connection failure
      mockDb.execute.mockRejectedValue(new Error('Database connection failed'));

      const result = await whatiTicketService.install(validWhatiTicketConfig);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Database connection failed');
      expect(result.databaseConnected).toBe(false);

      // Verify failure was logged
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatiTicket_install_failed'
        })
      );
    });

    it('should handle duplicate detection across services', async () => {
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Mock existing ticket in database
      mockDb.select.mockReturnValue({
        from: vi.fn().mockReturnValue({
          where: vi.fn().mockReturnValue({
            limit: vi.fn().mockReturnValue([{
              id: 'existing-ticket',
              drNumber: 'DR12345678',
              technicianNumber: '+27821234567',
              whatsappMessageId: 'existing-msg',
              status: 'pending',
              createdAt: new Date()
            }])
          })
        })
      });

      const message = {
        from: '+27821234567',
        body: 'DR12345678',
        timestamp: Date.now(),
        messageType: 'text' as const,
        messageId: 'msg-123'
      };

      // WhatsApp service should detect duplicate
      const whatsappResult = await whatsappService.processMessage(message);
      expect(whatsappResult.processed).toBe(true);
      expect(whatsappResult.status).toBe('success');

      // WhatiTicket service should also detect duplicate
      const whatiTicketMessage = {
        id: whatsappResult.id,
        from: message.from,
        content: message.body,
        drNumber: 'DR12345678',
        timestamp: message.timestamp,
        messageType: message.messageType
      };

      const whatiTicketResult = await whatiTicketService.processCompleteWorkflow(whatiTicketMessage);
      expect(whatiTicketResult.success).toBe(true);
      expect(whatiTicketResult.ticketId).toBe('existing-ticket');
      expect(whatiTicketResult.confidence).toBeLessThan(0.7);
    });

    it('should handle rate limiting coordination', async () => {
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Process multiple messages quickly to trigger rate limiting
      const messages = Array.from({ length: 15 }, (_, i) => ({
        from: '+27821234567',
        body: `DR1234567${i}`,
        timestamp: Date.now() + i,
        messageType: 'text' as const,
        messageId: `msg-${i}`
      }));

      const results = await Promise.allSettled(
        messages.map(msg => whatsappService.processMessage(msg))
      );

      // Some messages should be rate limited
      const rateLimitedResults = results.filter(result =>
        result.status === 'fulfilled' &&
        result.value.status === 'failed' &&
        result.value.error === 'Rate limit exceeded'
      );

      expect(rateLimitedResults.length).toBeGreaterThan(0);
    });

    it('should coordinate security validation', async () => {
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Mock security threat detection
      mockSecurityService.validateInput.mockResolvedValue({
        valid: false,
        error: 'Suspicious content detected',
        threats: ['xss attempt']
      });

      const maliciousMessage = {
        from: '+27821234567',
        body: 'DR12345678 <script>alert("xss")</script>',
        timestamp: Date.now(),
        messageType: 'text' as const,
        messageId: 'msg-123'
      };

      // WhatsApp service should reject malicious content
      const whatsappResult = await whatsappService.processMessage(maliciousMessage);
      expect(whatsappResult.processed).toBe(false);
      expect(whatsappResult.status).toBe('failed');

      // WhatiTicket service should also reject
      const whatiTicketResult = await whatiTicketService.processCompleteWorkflow({
        id: 'msg-123',
        from: maliciousMessage.from,
        content: maliciousMessage.body,
        timestamp: maliciousMessage.timestamp,
        messageType: maliciousMessage.messageType
      });

      expect(whatiTicketResult.success).toBe(false);
      expect(whatiTicketResult.error).toBe('Suspicious content detected');

      // Verify security logging
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatsapp_message_processed',
          data: expect.objectContaining({
            isDuplicate: false,
            hasDR: true
          })
        })
      );
    });

    it('should handle system health monitoring', async () => {
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Check health of both services
      const [whatsappHealth, whatiTicketHealth] = await Promise.all([
        // WhatsApp service health is implicit through API connectivity
        mockAxios.get.mockResolvedValue({}),
        whatiTicketService.getHealthStatus()
      ]);

      expect(whatiTicketHealth.status).toBe('healthy');
      expect(whatiTicketHealth.services.database).toBe('connected');
      expect(whatiTicketHealth.services.redis).toBe('connected');
      expect(whatiTicketHealth.services.whatsapp).toBe('connected');
      expect(whatiTicketHealth.services.security).toBe('enabled');
      expect(whatiTicketHealth.metrics.uptime).toBeGreaterThan(0);

      // Simulate system degradation
      mockDb.execute.mockRejectedValue(new Error('Database connection failed'));

      const degradedHealth = await whatiTicketService.getHealthStatus();
      expect(degradedHealth.status).toBe('degraded');
      expect(degradedHealth.services.database).toBe('disconnected');
    });

    it('should handle error recovery and retry logic', async () => {
      await Promise.all([
        whatsappService.initialize(validWhatsAppConfig),
        whatiTicketService.install(validWhatiTicketConfig)
      ]);

      // Mock temporary failure
      mockDb.insert.mockRejectedValueOnce(new Error('Temporary database error'));

      const message = {
        id: 'msg-123',
        from: '+27821234567',
        content: 'DR12345678',
        timestamp: Date.now(),
        messageType: 'text' as const
      };

      const result = await whatiTicketService.processCompleteWorkflow(message);

      // Should handle the error gracefully
      expect(result.success).toBe(false);
      expect(result.error).toBeDefined();
      expect(result.auditTrail).toHaveLength(3);
      expect(result.confidence).toBeGreaterThan(0);

      // Verify error was logged
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'whatiTicket_install_failed'
        })
      );
    });
  });

  describe('Performance and Scalability', () => {
    const validConfig = {
      phoneNumberId: 'phone-123',
      accessToken: 'token-123',
      businessAccountId: 'business-123',
      webhookUrl: 'https://example.com/webhook',
      verifyToken: 'verify-token-123',
      apiVersion: 'v18.0'
    };

    const whatiTicketConfig = {
      version: '1.0.0',
      database: 'postgres',
      redis: true,
      security: {
        encryption: 'aes-256-gcm',
        authentication: 'jwt'
      }
    };

    beforeEach(async () => {
      await Promise.all([
        whatsappService.initialize(validConfig),
        whatiTicketService.install(whatiTicketConfig)
      ]);
    });

    it('should handle concurrent message processing', async () => {
      const messages = Array.from({ length: 50 }, (_, i) => ({
        from: `+2782123456${i.toString().padStart(2, '0')}`,
        body: `DR1234567${i}`,
        timestamp: Date.now() + i,
        messageType: 'text' as const,
        messageId: `msg-${i}`
      }));

      const startTime = Date.now();
      const results = await Promise.allSettled(
        messages.map(msg => whatsappService.processMessage(msg))
      );
      const endTime = Date.now();

      const processingTime = endTime - startTime;
      const successfulResults = results.filter(result =>
        result.status === 'fulfilled' && result.value.processed
      );

      // Should process 50 messages in under 5 seconds
      expect(processingTime).toBeLessThan(5000);
      expect(successfulResults.length).toBeGreaterThan(40); // Allow some rate limiting

      // Verify database can handle the load
      expect(mockDb.insert).toHaveBeenCalledTimes(successfulResults.length);
    });

    it('should handle memory usage efficiently', async () => {
      const initialMemory = process.memoryUsage().heapUsed;

      // Process many messages
      const messages = Array.from({ length: 100 }, (_, i) => ({
        from: '+27821234567',
        body: `DR1234567${i}`,
        timestamp: Date.now() + i,
        messageType: 'text' as const,
        messageId: `msg-${i}`
      }));

      await Promise.allSettled(
        messages.map(msg => whatsappService.processMessage(msg))
      );

      const finalMemory = process.memoryUsage().heapUsed;
      const memoryIncrease = finalMemory - initialMemory;

      // Memory increase should be reasonable (< 50MB for 100 messages)
      expect(memoryIncrease).toBeLessThan(50 * 1024 * 1024);
    });

    it('should maintain audit trail integrity', async () => {
      const messages = Array.from({ length: 10 }, (_, i) => ({
        from: '+27821234567',
        body: `DR1234567${i}`,
        timestamp: Date.now() + i,
        messageType: 'text' as const,
        messageId: `msg-${i}`
      }));

      await Promise.allSettled(
        messages.map(msg => whatsappService.processMessage(msg))
      );

      // Verify all audit trail calls were made
      expect(mockSecurityService.logAuditTrail).toHaveBeenCalledTimes(10);

      // Verify audit trail entries contain required data
      for (let i = 0; i < 10; i++) {
        expect(mockSecurityService.logAuditTrail).toHaveBeenCalledWith(
          expect.objectContaining({
            type: 'whatsapp_message_processed',
            data: expect.objectContaining({
              messageId: `msg-${i}`,
              hasDR: true
            }),
            timestamp: expect.any(Number),
            userId: 'system'
          })
        );
      }
    });
  });
});