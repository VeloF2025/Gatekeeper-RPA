import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { ticketService } from '@/database/services/ticket.service';
import { database } from '@/database/database';
import * as schema from '@/database/schemas';
import { logger, errorLogger } from '@/utils/logger';

// Mock the database and logger
vi.mock('@/database/database');
vi.mock('@/utils/logger');

const mockDatabase = vi.mocked(database);
const mockLogger = vi.mocked(logger);
const mockErrorLogger = vi.mocked(errorLogger);

describe('TicketService', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    // Setup default database mock responses
    mockDatabase.drizzle.insert.returnValues = [{
      id: 'test-ticket-id',
      ticketNumber: 'TCK-TEST-001',
      drNumber: 'DR123',
      technicianNumber: '+27820001111',
      technicianName: 'John Doe',
      messageContent: 'Test message',
      messageTimestamp: new Date(),
      status: 'pending',
      priority: 'normal',
      assignedTo: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      completedAt: null,
      whatsappMessageId: null,
      externalTicketId: null,
      externalSystem: null,
      syncStatus: 'pending',
      metadata: {}
    }];

    mockDatabase.drizzle.select.mockReturnValue({
      where: vi.fn().mockReturnThis(),
      and: vi.fn().mockReturnThis(),
      or: vi.fn().mockReturnThis(),
      limit: vi.fn().mockReturnThis(),
      offset: vi.fn().mockReturnThis(),
      orderBy: vi.fn().mockResolvedValue([
        {
          id: 'test-ticket-id',
          ticketNumber: 'TCK-TEST-001',
          drNumber: 'DR123',
          technicianNumber: '+27820001111',
          status: 'pending',
          priority: 'normal',
          createdAt: new Date(),
          updatedAt: new Date()
        }
      ])
    } as any);

    mockDatabase.drizzle.update.returnValues = [{
      id: 'test-ticket-id',
      ticketNumber: 'TCK-TEST-001',
      drNumber: 'DR123',
      technicianNumber: '+27820001111',
      technicianName: 'John Doe',
      messageContent: 'Test message',
      messageTimestamp: new Date(),
      status: 'completed',
      priority: 'high',
      assignedTo: 'user1',
      createdAt: new Date(),
      updatedAt: new Date(),
      completedAt: new Date(),
      whatsappMessageId: null,
      externalTicketId: null,
      externalSystem: null,
      syncStatus: 'synced',
      metadata: {}
    }];

    // Mock count query
    mockDatabase.drizzle.select.mockReturnValueOnce({
      where: vi.fn().mockReturnThis(),
      and: vi.fn().mockReturnThis()
    } as any);
  });

  describe('createTicket', () => {
    it('should create a new ticket with valid data', async () => {
      const ticketData = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        technicianName: 'John Doe',
        messageContent: 'Installation complete',
        priority: 'normal' as const
      };

      const result = await ticketService.createTicket(ticketData);

      expect(result).toBeDefined();
      expect(result.ticketNumber).toBeDefined();
      expect(result.drNumber).toBe(ticketData.drNumber);
      expect(result.technicianNumber).toBe(ticketData.technicianNumber);
      expect(result.status).toBe('pending');
      expect(result.priority).toBe(ticketData.priority);

      expect(mockDatabase.drizzle.insert).toHaveBeenCalledWith(
        expect.objectContaining({
          drNumber: ticketData.drNumber,
          technicianNumber: ticketData.technicianNumber,
          technicianName: ticketData.technicianName,
          messageContent: ticketData.messageContent,
          priority: ticketData.priority
        })
      );

      expect(mockLogger.info).toHaveBeenCalledWith('Ticket created successfully', {
        ticketId: result.id,
        ticketNumber: result.ticketNumber
      });
    });

    it('should generate unique ticket number if not provided', async () => {
      const ticketData = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        priority: 'normal' as const
      };

      const result = await ticketService.createTicket(ticketData);

      expect(result.ticketNumber).toMatch(/^TCK-[a-z0-9]+-[a-z0-9]+$/i);
    });

    it('should handle database errors gracefully', async () => {
      mockDatabase.drizzle.insert.mockImplementationOnce(() => {
        throw new Error('Database connection failed');
      });

      const ticketData = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        priority: 'normal' as const
      };

      await expect(ticketService.createTicket(ticketData)).rejects.toThrow('Failed to create ticket');

      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Failed to create ticket',
        expect.objectContaining({
          error: 'Database connection failed',
          data: ticketData
        })
      );
    });
  });

  describe('getTicketById', () => {
    it('should return ticket when found', async () => {
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([{
          id: 'test-ticket-id',
          ticketNumber: 'TCK-TEST-001',
          drNumber: 'DR123',
          technicianNumber: '+27820001111',
          status: 'pending'
        }])
      } as any);

      const result = await ticketService.getTicketById('test-ticket-id');

      expect(result).toEqual({
        id: 'test-ticket-id',
        ticketNumber: 'TCK-TEST-001',
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        status: 'pending'
      });
    });

    it('should return null when ticket not found', async () => {
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([])
      } as any);

      const result = await ticketService.getTicketById('non-existent-id');

      expect(result).toBeNull();
    });

    it('should handle database errors', async () => {
      mockDatabase.drizzle.select.mockImplementationOnce(() => {
        throw new Error('Database query failed');
      });

      await expect(ticketService.getTicketById('test-id')).rejects.toThrow('Failed to get ticket');
    });
  });

  describe('getTickets', () => {
    it('should return paginated tickets with filters', async () => {
      const filters = {
        status: ['pending', 'in_progress'],
        priority: 'high',
        page: 1,
        limit: 10,
        sortBy: 'createdAt' as const,
        sortOrder: 'desc' as const
      };

      // Mock the count query response
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([{ count: 50 }])
      } as any);

      const result = await ticketService.getTickets(filters);

      expect(result.data).toHaveLength(1);
      expect(result.pagination).toEqual({
        page: 1,
        limit: 10,
        total: 50,
        pages: 5,
        hasNext: true,
        hasPrev: false
      });

      expect(mockDatabase.drizzle.select).toHaveBeenCalledTimes(2);
    });

    it('should handle empty filters', async () => {
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([{ count: 25 }])
      } as any);

      const result = await ticketService.getTickets();

      expect(result.pagination.total).toBe(25);
      expect(result.pagination.page).toBe(1);
      expect(result.pagination.limit).toBe(50);
    });

    it('should handle database errors', async () => {
      mockDatabase.drizzle.select.mockImplementationOnce(() => {
        throw new Error('Query failed');
      });

      await expect(ticketService.getTickets()).rejects.toThrow('Failed to get tickets');
    });
  });

  describe('updateTicket', () => {
    it('should update ticket with valid data', async () => {
      mockDatabase.drizzle.update.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        set: vi.fn().mockReturnThis(),
        returning: vi.fn().mockResolvedValue([{
          id: 'test-ticket-id',
          ticketNumber: 'TCK-TEST-001',
          status: 'completed',
          priority: 'high',
          assignedTo: 'user1',
          completedAt: new Date(),
          updatedAt: new Date()
        }])
      } as any);

      const updateData = {
        status: 'completed' as const,
        priority: 'high' as const,
        assignedTo: 'user1'
      };

      const result = await ticketService.updateTicket('test-ticket-id', updateData);

      expect(result.status).toBe('completed');
      expect(result.priority).toBe('high');
      expect(result.assignedTo).toBe('user1');
      expect(result.completedAt).toBeDefined();

      expect(mockLogger.info).toHaveBeenCalledWith('Ticket updated successfully', {
        ticketId: 'test-ticket-id',
        updates: expect.objectContaining({
          status: 'completed',
          priority: 'high',
          assignedTo: 'user1'
        })
      });
    });

    it('should set completedAt when status is completed', async () => {
      const beforeUpdate = new Date('2024-01-01');
      vi.setSystemTime(beforeUpdate);

      mockDatabase.drizzle.update.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        set: vi.fn().mockReturnThis(),
        returning: vi.fn().mockResolvedValue([{
          id: 'test-ticket-id',
          status: 'completed',
          completedAt: new Date()
        }])
      } as any);

      const result = await ticketService.updateTicket('test-ticket-id', {
        status: 'completed'
      });

      expect(result.completedAt).toBeDefined();
      expect(result.completedAt?.getTime()).toBeGreaterThan(beforeUpdate.getTime());

      vi.useRealTimers();
    });

    it('should throw error when ticket not found', async () => {
      mockDatabase.drizzle.update.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        set: vi.fn().mockReturnThis(),
        returning: vi.fn().mockResolvedValue([])
      } as any);

      await expect(ticketService.updateTicket('non-existent-id', { status: 'completed' as const }))
        .rejects.toThrow('Ticket not found');
    });
  });

  describe('deleteTicket', () => {
    it('should soft delete ticket by setting status to cancelled', async () => {
      await ticketService.deleteTicket('test-ticket-id');

      expect(mockDatabase.drizzle.update).toHaveBeenCalledWith(
        expect.objectContaining({
          status: 'cancelled',
          updatedAt: expect.any(Date)
        })
      );

      expect(mockLogger.info).toHaveBeenCalledWith('Ticket deleted successfully', {
        ticketId: 'test-ticket-id'
      });
    });
  });

  describe('getTicketStatistics', () => {
    it('should return correct statistics', async () => {
      const mockTickets = [
        {
          status: 'pending',
          priority: 'normal',
          createdAt: new Date('2024-01-01'),
          completedAt: new Date('2024-01-01')
        },
        {
          status: 'completed',
          priority: 'high',
          createdAt: new Date('2024-01-01'),
          completedAt: new Date('2024-01-01')
        },
        {
          status: 'pending',
          priority: 'low',
          createdAt: new Date('2024-01-01'),
          completedAt: null
        }
      ];

      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue(mockTickets)
      } as any);

      const stats = await ticketService.getTicketStatistics();

      expect(stats).toEqual({
        total: 3,
        byStatus: {
          pending: 2,
          completed: 1
        },
        byPriority: {
          normal: 1,
          high: 1,
          low: 1
        },
        completedToday: 0,
        avgProcessingTime: 0
      });
    });

    it('should calculate completed tickets for today', async () => {
      const today = new Date();
      const yesterday = new Date(today);
      yesterday.setDate(yesterday.getDate() - 1);

      const mockTickets = [
        {
          status: 'completed',
          priority: 'normal',
          createdAt: yesterday,
          completedAt: today
        }
      ];

      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue(mockTickets)
      } as any);

      const stats = await ticketService.getTicketStatistics();

      expect(stats.completedToday).toBe(1);
    });

    it('should handle empty ticket list', async () => {
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([])
      } as any);

      const stats = await ticketService.getTicketStatistics();

      expect(stats).toEqual({
        total: 0,
        byStatus: {},
        byPriority: {},
        completedToday: 0,
        avgProcessingTime: 0
      });
    });
  });
});