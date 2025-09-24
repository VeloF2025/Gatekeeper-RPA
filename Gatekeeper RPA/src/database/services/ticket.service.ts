import { eq, and, or, ilike, desc, asc, gte, lte, count } from 'drizzle-orm';
import { database } from '../database';
import { tickets } from '../schemas';
import {
  CreateTicketRequest,
  UpdateTicketRequest,
  TicketFilters,
  PaginatedResponse,
  Ticket,
  NewTicket
} from '../models';
import { logger, errorLogger } from '@/utils/logger';
import { v4 as uuidv4 } from 'uuid';

export class TicketService {
  /**
   * Create a new ticket
   */
  async createTicket(data: CreateTicketRequest): Promise<Ticket> {
    try {
      // Generate unique ticket number if not provided
      const ticketNumber = data.ticketNumber || this.generateTicketNumber();

      const newTicket: NewTicket = {
        ticketNumber,
        drNumber: data.drNumber,
        technicianNumber: data.technicianNumber,
        technicianName: data.technicianName,
        messageContent: data.messageContent,
        messageTimestamp: data.messageTimestamp || new Date(),
        status: 'pending',
        priority: data.priority || 'normal',
        assignedTo: data.assignedTo,
        whatsappMessageId: data.whatsappMessageId,
        externalTicketId: data.externalTicketId,
        externalSystem: data.externalSystem,
        syncStatus: 'pending',
        metadata: {},
      };

      const [createdTicket] = await database.drizzle
        .insert(tickets)
        .values(newTicket)
        .returning();

      logger.info('Ticket created successfully', {
        ticketId: createdTicket.id,
        ticketNumber: createdTicket.ticketNumber
      });

      return createdTicket;
    } catch (error) {
      errorLogger.error('Failed to create ticket', {
        error: error instanceof Error ? error.message : error,
        data
      });
      throw new Error('Failed to create ticket');
    }
  }

  /**
   * Get ticket by ID
   */
  async getTicketById(id: string): Promise<Ticket | null> {
    try {
      const [ticket] = await database.drizzle
        .select()
        .from(tickets)
        .where(eq(tickets.id, id));

      return ticket || null;
    } catch (error) {
      errorLogger.error('Failed to get ticket by ID', {
        error: error instanceof Error ? error.message : error,
        id
      });
      throw new Error('Failed to get ticket');
    }
  }

  /**
   * Get ticket by ticket number
   */
  async getTicketByNumber(ticketNumber: string): Promise<Ticket | null> {
    try {
      const [ticket] = await database.drizzle
        .select()
        .from(tickets)
        .where(eq(tickets.ticketNumber, ticketNumber));

      return ticket || null;
    } catch (error) {
      errorLogger.error('Failed to get ticket by number', {
        error: error instanceof Error ? error.message : error,
        ticketNumber
      });
      throw new Error('Failed to get ticket');
    }
  }

  /**
   * Get tickets with filtering and pagination
   */
  async getTickets(filters: TicketFilters = {}): Promise<PaginatedResponse<Ticket>> {
    try {
      const {
        status,
        priority,
        assignedTo,
        technicianNumber,
        drNumber,
        dateFrom,
        dateTo,
        page = 1,
        limit = 50,
        sortBy = 'createdAt',
        sortOrder = 'desc'
      } = filters;

      // Build where conditions
      const whereConditions = [];

      if (status) {
        if (Array.isArray(status)) {
          whereConditions.push(or(...status.map(s => eq(tickets.status, s))));
        } else {
          whereConditions.push(eq(tickets.status, status));
        }
      }

      if (priority) {
        if (Array.isArray(priority)) {
          whereConditions.push(or(...priority.map(p => eq(tickets.priority, p))));
        } else {
          whereConditions.push(eq(tickets.priority, priority));
        }
      }

      if (assignedTo) {
        whereConditions.push(eq(tickets.assignedTo, assignedTo));
      }

      if (technicianNumber) {
        whereConditions.push(eq(tickets.technicianNumber, technicianNumber));
      }

      if (drNumber) {
        whereConditions.push(eq(tickets.drNumber, drNumber));
      }

      if (dateFrom) {
        whereConditions.push(gte(tickets.createdAt, dateFrom));
      }

      if (dateTo) {
        whereConditions.push(lte(tickets.createdAt, dateTo));
      }

      const whereClause = whereConditions.length > 0 ? and(...whereConditions) : undefined;

      // Get total count
      const [totalCount] = await database.drizzle
        .select({ count: count() })
        .from(tickets)
        .where(whereClause);

      // Get paginated results
      const offset = (page - 1) * limit;
      const orderBy = sortBy === 'createdAt' ? tickets.createdAt :
                     sortBy === 'updatedAt' ? tickets.updatedAt :
                     sortBy === 'priority' ? tickets.priority :
                     tickets.status;

      const orderDirection = sortOrder === 'asc' ? asc : desc;

      const results = await database.drizzle
        .select()
        .from(tickets)
        .where(whereClause)
        .orderBy(orderDirection(orderBy))
        .limit(limit)
        .offset(offset);

      const totalPages = Math.ceil(totalCount.count / limit);

      return {
        data: results,
        pagination: {
          page,
          limit,
          total: totalCount.count,
          pages: totalPages,
          hasNext: page < totalPages,
          hasPrev: page > 1
        }
      };
    } catch (error) {
      errorLogger.error('Failed to get tickets', {
        error: error instanceof Error ? error.message : error,
        filters
      });
      throw new Error('Failed to get tickets');
    }
  }

  /**
   * Update ticket
   */
  async updateTicket(id: string, data: UpdateTicketRequest): Promise<Ticket> {
    try {
      const updateData: Partial<NewTicket> = {
        ...data,
        updatedAt: new Date(),
      };

      if (data.status === 'completed' && !data.completedAt) {
        updateData.completedAt = new Date();
      }

      const [updatedTicket] = await database.drizzle
        .update(tickets)
        .set(updateData)
        .where(eq(tickets.id, id))
        .returning();

      if (!updatedTicket) {
        throw new Error('Ticket not found');
      }

      logger.info('Ticket updated successfully', {
        ticketId: id,
        updates: data
      });

      return updatedTicket;
    } catch (error) {
      errorLogger.error('Failed to update ticket', {
        error: error instanceof Error ? error.message : error,
        id,
        data
      });
      throw new Error('Failed to update ticket');
    }
  }

  /**
   * Delete ticket (soft delete by setting status to cancelled)
   */
  async deleteTicket(id: string): Promise<void> {
    try {
      await database.drizzle
        .update(tickets)
        .set({
          status: 'cancelled',
          updatedAt: new Date()
        })
        .where(eq(tickets.id, id));

      logger.info('Ticket deleted successfully', { ticketId: id });
    } catch (error) {
      errorLogger.error('Failed to delete ticket', {
        error: error instanceof Error ? error.message : error,
        id
      });
      throw new Error('Failed to delete ticket');
    }
  }

  /**
   * Get ticket statistics
   */
  async getTicketStatistics(): Promise<{
    total: number;
    byStatus: Record<string, number>;
    byPriority: Record<string, number>;
    completedToday: number;
    avgProcessingTime: number;
  }> {
    try {
      const allTickets = await database.drizzle
        .select({
          status: tickets.status,
          priority: tickets.priority,
          createdAt: tickets.createdAt,
          completedAt: tickets.completedAt,
        })
        .from(tickets);

      const total = allTickets.length;
      const byStatus = allTickets.reduce((acc, ticket) => {
        acc[ticket.status] = (acc[ticket.status] || 0) + 1;
        return acc;
      }, {} as Record<string, number>);

      const byPriority = allTickets.reduce((acc, ticket) => {
        acc[ticket.priority] = (acc[ticket.priority] || 0) + 1;
        return acc;
      }, {} as Record<string, number>);

      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);

      const completedToday = allTickets.filter(ticket =>
        ticket.completedAt &&
        ticket.completedAt >= today &&
        ticket.completedAt < tomorrow
      ).length;

      // Calculate average processing time for completed tickets
      const completedTickets = allTickets.filter(ticket =>
        ticket.completedAt && ticket.createdAt
      );

      const avgProcessingTime = completedTickets.length > 0
        ? completedTickets.reduce((sum, ticket) => {
            const processingTime = ticket.completedAt!.getTime() - ticket.createdAt.getTime();
            return sum + processingTime;
          }, 0) / completedTickets.length / (1000 * 60) // Convert to minutes
        : 0;

      return {
        total,
        byStatus,
        byPriority,
        completedToday,
        avgProcessingTime: Math.round(avgProcessingTime * 100) / 100
      };
    } catch (error) {
      errorLogger.error('Failed to get ticket statistics', {
        error: error instanceof Error ? error.message : error
      });
      throw new Error('Failed to get ticket statistics');
    }
  }

  /**
   * Generate unique ticket number
   */
  private generateTicketNumber(): string {
    const timestamp = Date.now().toString(36);
    const random = Math.random().toString(36).substr(2, 5);
    return `TCK-${timestamp}-${random}`.toUpperCase();
  }
}

// Export singleton instance
export const ticketService = new TicketService();