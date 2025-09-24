import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { TicketService } from '@/services/ticket/ticket.service';
import { Database } from '@/database/database';
import { TicketStatus, Priority, UserRole } from '@/types';

// Mock Database
const createMockDatabase = () => ({
  query: vi.fn(),
  getClient: vi.fn(),
  transaction: vi.fn()
});

// Mock logger
vi.mock('@/utils/logger', () => ({
  logger: {
    info: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    debug: vi.fn()
  },
  auditLogger: {
    info: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    debug: vi.fn()
  }
}));

describe('TicketService', () => {
  let service: TicketService;
  let mockDb: Database;

  beforeEach(() => {
    vi.clearAllMocks();
    mockDb = createMockDatabase() as any;
    service = new TicketService(mockDb);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('createTicket', () => {
    const validTicketData = {
      drNumber: 'DR1854443',
      technicianNumber: '+27821234567',
      technicianName: 'John Doe',
      messageContent: 'DR1854443',
      priority: Priority.NORMAL
    };

    it('should create ticket with valid data', async () => {
      const mockResult = [{
        id: 'ticket-uuid',
        ticket_number: 'TICKET-20250923-1234',
        dr_number: 'DR1854443',
        technician_number: '+27821234567',
        technician_name: 'John Doe',
        message_content: 'DR1854443',
        status: 'pending',
        priority: 'normal',
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString()
      }];

      mockDb.query
        .mockResolvedValueOnce([]) // No existing ticket
        .mockResolvedValueOnce(mockResult); // Insert result

      const ticket = await service.createTicket(validTicketData);

      expect(ticket).toMatchObject({
        id: 'ticket-uuid',
        ticketNumber: 'TICKET-20250923-1234',
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443',
        status: TicketStatus.PENDING,
        priority: Priority.NORMAL
      });

      expect(mockDb.query).toHaveBeenCalledTimes(2);
      expect(mockDb.query).toHaveBeenCalledWith(
        expect.stringContaining('INSERT INTO tickets'),
        expect.arrayContaining([
          expect.any(String),
          expect.stringMatching(/^TICKET-\d{8}-\d{4}$/),
          'DR1854443',
          '+27821234567',
          'John Doe',
          'DR1854443',
          'pending',
          'normal',
          expect.any(Date),
          expect.any(Date)
        ])
      );
    });

    it('should validate required fields', async () => {
      const invalidData = {
        drNumber: '',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'test'
      };

      await expect(service.createTicket(invalidData as any))
        .rejects.toThrow('Missing required fields for ticket creation');
    });

    it('should validate phone number format', async () => {
      const invalidData = {
        ...validTicketData,
        technicianNumber: 'invalid-phone'
      };

      await expect(service.createTicket(invalidData))
        .rejects.toThrow('Invalid phone number format');
    });

    it('should validate DR number format', async () => {
      const invalidData = {
        ...validTicketData,
        drNumber: 'INVALID123'
      };

      await expect(service.createTicket(invalidData))
        .rejects.toThrow('Invalid DR number format');
    });

    it('should detect duplicate DR numbers', async () => {
      mockDb.query.mockResolvedValueOnce([{ id: 'existing-ticket' }]); // Existing ticket found

      await expect(service.createTicket(validTicketData))
        .rejects.toThrow('A ticket for DR1854443 already exists');
    });

    it('should sanitize message content', async () => {
      const maliciousData = {
        ...validTicketData,
        messageContent: '<script>alert("xss")</script>DR1854443'
      };

      mockDb.query
        .mockResolvedValueOnce([]) // No existing ticket
        .mockResolvedValueOnce([{
          id: 'ticket-uuid',
          ticket_number: 'TICKET-20250923-1234',
          message_content: '&lt;script&gt;alert(&quot;xss&quot;)&lt;/script&gt;DR1854443',
          status: 'pending',
          priority: 'normal'
        }]);

      await service.createTicket(maliciousData);

      expect(mockDb.query).toHaveBeenCalledWith(
        expect.stringContaining('INSERT INTO tickets'),
        expect.arrayContaining([
          expect.any(String),
          expect.any(String),
          'DR1854443',
          '+27821234567',
          'John Doe',
          '&lt;script&gt;alert(&quot;xss&quot;)&lt;/script&gt;DR1854443'
        ])
      );
    });

    it('should handle database errors gracefully', async () => {
      mockDb.query.mockRejectedValue(new Error('Database connection failed'));

      await expect(service.createTicket(validTicketData))
        .rejects.toThrow('Database connection failed');
    });
  });

  describe('getTicket', () => {
    it('should return ticket with valid ID', async () => {
      const mockTicket = {
        id: 'ticket-uuid',
        ticket_number: 'TICKET-20250923-1234',
        dr_number: 'DR1854443',
        technician_number: '+27821234567',
        technician_name: 'John Doe',
        message_content: 'DR1854443',
        status: 'pending',
        priority: 'normal',
        message_timestamp: new Date().toISOString(),
        assigned_to: null,
        property_id: null,
        job_id: null,
        address: null,
        installation_status: null,
        compliance_score: null,
        completed_at: null,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString()
      };

      mockDb.query.mockResolvedValueOnce([mockTicket]);

      const ticket = await service.getTicket('ticket-uuid');

      expect(ticket).toMatchObject({
        id: 'ticket-uuid',
        ticketNumber: 'TICKET-20250923-1234',
        drNumber: 'DR1854443',
        status: TicketStatus.PENDING
      });
    });

    it('should return null for non-existent ticket', async () => {
      mockDb.query.mockResolvedValueOnce([]);

      const ticket = await service.getTicket('non-existent-uuid');

      expect(ticket).toBeNull();
    });

    it('should validate ticket ID format', async () => {
      await expect(service.getTicket('invalid-uuid'))
        .rejects.toThrow('Invalid ticket ID format');
    });

    it('should apply role-based access control for technicians', async () => {
      const mockUser = { id: 'user-uuid', phone_number: '+27821234567' };

      mockDb.query
        .mockResolvedValueOnce([{ phone_number: '+27821234567' }])
        .mockResolvedValueOnce([{
          id: 'ticket-uuid',
          technician_number: '+27821234567'
        }]);

      await service.getTicket('ticket-uuid', 'user-uuid', UserRole.TECHNICIAN);

      expect(mockDb.query).toHaveBeenCalledWith(
        expect.stringContaining('technician_number = (SELECT phone_number FROM users WHERE id = $2)'),
        ['ticket-uuid', 'user-uuid']
      );
    });
  });

  describe('updateTicket', () => {
    const validUpdate = {
      status: TicketStatus.IN_PROGRESS,
      priority: Priority.HIGH
    };

    it('should update ticket with valid data', async () => {
      const existingTicket = {
        id: 'ticket-uuid',
        status: 'pending',
        priority: 'normal',
        updated_at: new Date().toISOString()
      };

      const updatedTicket = {
        ...existingTicket,
        status: 'in_progress',
        priority: 'high',
        updated_at: new Date().toISOString()
      };

      mockDb.query
        .mockResolvedValueOnce([existingTicket])
        .mockResolvedValueOnce([updatedTicket]);

      const result = await service.updateTicket('ticket-uuid', validUpdate);

      expect(result.status).toBe(TicketStatus.IN_PROGRESS);
      expect(result.priority).toBe(Priority.HIGH);
      expect(result.updatedAt.getTime()).not.toBe(existingTicket.updated_at);
    });

    it('should validate ticket ID format', async () => {
      await expect(service.updateTicket('invalid-uuid', validUpdate))
        .rejects.toThrow('Invalid ticket ID format');
    });

    it('should validate status enum values', async () => {
      const invalidUpdate = { status: 'invalid_status' as any };

      await expect(service.updateTicket('ticket-uuid', invalidUpdate))
        .rejects.toThrow('Invalid enum value');
    });

    it('should validate assignedTo user ID format', async () => {
      const invalidUpdate = { assignedTo: 'invalid-uuid' };

      await expect(service.updateTicket('ticket-uuid', invalidUpdate))
        .rejects.toThrow('Invalid assignedTo user ID format');
    });

    it('should validate compliance score range', async () => {
      const invalidUpdate = { complianceScore: 150 };

      await expect(service.updateTicket('ticket-uuid', invalidUpdate))
        .rejects.toThrow('Compliance score must be between 0 and 100');
    });

    it('should set completion timestamp when status is completed', async () => {
      const existingTicket = {
        id: 'ticket-uuid',
        status: 'in_progress',
        completed_at: null
      };

      const updatedTicket = {
        ...existingTicket,
        status: 'completed',
        completed_at: new Date().toISOString()
      };

      mockDb.query
        .mockResolvedValueOnce([existingTicket])
        .mockResolvedValueOnce([updatedTicket]);

      const result = await service.updateTicket('ticket-uuid', { status: TicketStatus.COMPLETED });

      expect(result.status).toBe(TicketStatus.COMPLETED);
      expect(result.completedAt).toBeInstanceOf(Date);
    });

    it('should handle empty updates', async () => {
      const existingTicket = {
        id: 'ticket-uuid',
        status: 'pending',
        updated_at: new Date().toISOString()
      };

      mockDb.query.mockResolvedValueOnce([existingTicket]);

      const result = await service.updateTicket('ticket-uuid', {});

      expect(result).toEqual(existingTicket);
    });
  });

  describe('getTicketsByStatus', () => {
    it('should return tickets with specified status', async () => {
      const mockTickets = [
        { id: 'ticket-1', status: 'pending', created_at: new Date().toISOString() },
        { id: 'ticket-2', status: 'pending', created_at: new Date().toISOString() }
      ];

      const mockCount = [{ total: 2 }];

      mockDb.query
        .mockResolvedValueOnce(mockCount)
        .mockResolvedValueOnce(mockTickets);

      const result = await service.getTicketsByStatus(TicketStatus.PENDING);

      expect(result.tickets).toHaveLength(2);
      expect(result.total).toBe(2);
      expect(result.page).toBe(1);
      expect(result.totalPages).toBe(1);

      result.tickets.forEach(ticket => {
        expect(ticket.status).toBe(TicketStatus.PENDING);
      });
    });

    it('should validate pagination parameters', async () => {
      await expect(service.getTicketsByStatus(TicketStatus.PENDING, 0))
        .rejects.toThrow('Page must be greater than 0');

      await expect(service.getTicketsByStatus(TicketStatus.PENDING, 1, 0))
        .rejects.toThrow('Limit must be between 1 and 100');

      await expect(service.getTicketsByStatus(TicketStatus.PENDING, 1, 101))
        .rejects.toThrow('Limit must be between 1 and 100');
    });

    it('should apply role-based access control', async () => {
      const mockCount = [{ total: 1 }];
      const mockTickets = [{ id: 'ticket-1', status: 'pending' }];

      mockDb.query
        .mockResolvedValueOnce(mockCount)
        .mockResolvedValueOnce(mockTickets);

      await service.getTicketsByStatus(TicketStatus.PENDING, 1, 50, 'user-uuid', UserRole.TECHNICIAN);

      expect(mockDb.query).toHaveBeenCalledWith(
        expect.stringContaining('technician_number = (SELECT phone_number FROM users WHERE id = $2)'),
        expect.arrayContaining(['pending', 'user-uuid'])
      );
    });

    it('should return empty array for no matching tickets', async () => {
      mockDb.query
        .mockResolvedValueOnce([{ total: 0 }])
        .mockResolvedValueOnce([]);

      const result = await service.getTicketsByStatus(TicketStatus.COMPLETED);

      expect(result.tickets).toHaveLength(0);
      expect(result.total).toBe(0);
    });
  });

  describe('getTicketMetrics', () => {
    it('should calculate correct metrics', async () => {
      const mockStatusCounts = [
        { status: 'completed', count: 2 },
        { status: 'pending', count: 1 }
      ];

      const mockProcessingTime = [{ avg_processing_time: 3600 }]; // 1 hour

      mockDb.query
        .mockResolvedValueOnce(mockStatusCounts)
        .mockResolvedValueOnce(mockProcessingTime);

      const metrics = await service.getTicketMetrics();

      expect(metrics.total).toBe(3);
      expect(metrics.completed).toBe(2);
      expect(metrics.pending).toBe(1);
      expect(metrics.completionRate).toBeCloseTo(66.67, 2);
      expect(metrics.averageProcessingTime).toBe(3600);
    });

    it('should handle empty data', async () => {
      mockDb.query
        .mockResolvedValueOnce([])
        .mockResolvedValueOnce([{ avg_processing_time: null }]);

      const metrics = await service.getTicketMetrics();

      expect(metrics.total).toBe(0);
      expect(metrics.completed).toBe(0);
      expect(metrics.completionRate).toBe(0);
      expect(metrics.averageProcessingTime).toBe(0);
    });

    it('should apply date filters', async () => {
      const startDate = new Date('2025-01-01');
      const endDate = new Date('2025-01-31');

      mockDb.query
        .mockResolvedValueOnce([])
        .mockResolvedValueOnce([{ avg_processing_time: null }]);

      await service.getTicketMetrics(startDate, endDate);

      expect(mockDb.query).toHaveBeenCalledWith(
        expect.stringContaining('created_at >= $1'),
        expect.arrayContaining([startDate])
      );
    });

    it('should apply role-based access control', async () => {
      mockDb.query
        .mockResolvedValueOnce([])
        .mockResolvedValueOnce([{ avg_processing_time: null }]);

      await service.getTicketMetrics(undefined, undefined, 'user-uuid', UserRole.TECHNICIAN);

      expect(mockDb.query).toHaveBeenCalledWith(
        expect.stringContaining('technician_number = (SELECT phone_number FROM users WHERE id = $1)'),
        expect.arrayContaining(['user-uuid'])
      );
    });
  });
});