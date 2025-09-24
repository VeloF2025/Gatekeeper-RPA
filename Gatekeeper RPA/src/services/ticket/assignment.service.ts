import { v4 as uuidv4 } from 'uuid';
import { Database } from '@/database/database';
import { Ticket, TicketStatus, UserRole, Assignment } from '@/types';
import { logger, auditLogger } from '@/utils/logger';

export interface AssignmentHistory {
  id: string;
  ticketId: string;
  assignedTo: string;
  assignedBy: string;
  assignedAt: Date;
  status: 'assigned' | 'reassigned' | 'unassigned';
  notes?: string;
}

export class TicketAssignmentService {
  private db: Database;

  constructor(db: Database) {
    this.db = db;
  }

  /**
   * Assign ticket to a user with validation and audit trail
   */
  async assignTicket(
    ticketId: string,
    assignedTo: string,
    assignedBy: string,
    notes?: string
  ): Promise<Ticket> {
    try {
      // Validate ticket ID format
      if (!uuidv4.validate(ticketId)) {
        throw new Error('Invalid ticket ID format');
      }

      // Validate user IDs
      if (!uuidv4.validate(assignedTo) || !uuidv4.validate(assignedBy)) {
        throw new Error('Invalid user ID format');
      }

      // Check if ticket exists and can be assigned
      const ticketQuery = `
        SELECT t.*, u_assigned.role as assigned_role, u_assigner.role as assigner_role
        FROM tickets t
        LEFT JOIN users u_assigned ON u_assigned.id = $2
        LEFT JOIN users u_assigner ON u_assigner.id = $3
        WHERE t.id = $1
      `;

      const ticketResult = await this.db.query(ticketQuery, [ticketId, assignedTo, assignedBy]);

      if (ticketResult.length === 0) {
        throw new Error('Ticket not found');
      }

      const ticket = ticketResult[0];

      // Validate ticket status for assignment
      if (ticket.status === TicketStatus.COMPLETED || ticket.status === TicketStatus.CANCELLED) {
        throw new Error('Cannot assign completed or cancelled tickets');
      }

      // Validate assignee role
      if (ticket.assigned_role !== UserRole.AUDITOR && ticket.assigned_role !== UserRole.ADMIN) {
        throw new Error('Only auditors and admins can be assigned tickets');
      }

      // Validate assigner role
      if (ticket.assigner_role !== UserRole.ADMIN && ticket.assigner_role !== UserRole.AUDITOR) {
        throw new Error('Only admins and auditors can assign tickets');
      }

      // Check if assignee exists and has required role
      const assigneeQuery = `
        SELECT id, role, is_active FROM users WHERE id = $1
      `;
      const assigneeResult = await this.db.query(assigneeQuery, [assignedTo]);

      if (assigneeResult.length === 0) {
        throw new Error('Assignee not found');
      }

      const assignee = assigneeResult[0];
      if (!assignee.is_active) {
        throw new Error('Assignee is not active');
      }

      if (assignee.role !== UserRole.AUDITOR && assignee.role !== UserRole.ADMIN) {
        throw new Error('Assignee must have auditor or admin role');
      }

      // Start transaction
      const client = await this.db.getClient();
      try {
        await client.query('BEGIN');

        // Update ticket assignment
        const updateQuery = `
          UPDATE tickets
          SET
            assigned_to = $1,
            status = $2,
            updated_at = $3
          WHERE id = $4
          RETURNING *
        `;

        const updateResult = await client.query(updateQuery, [
          assignedTo,
          TicketStatus.ASSIGNED,
          new Date(),
          ticketId
        ]);

        const updatedTicket = updateResult[0];

        // Record assignment history
        const historyQuery = `
          INSERT INTO ticket_assignments (
            id, ticket_id, assigned_to, assigned_by, assigned_at, status, notes
          ) VALUES ($1, $2, $3, $4, $5, $6, $7)
        `;

        await client.query(historyQuery, [
          uuidv4(),
          ticketId,
          assignedTo,
          assignedBy,
          new Date(),
          'assigned',
          notes
        ]);

        // Check if this is a reassignment
        if (ticket.assigned_to && ticket.assigned_to !== assignedTo) {
          const reassignHistoryQuery = `
            INSERT INTO ticket_assignments (
              id, ticket_id, assigned_to, assigned_by, assigned_at, status, notes
            ) VALUES ($1, $2, $3, $4, $5, $6, $7)
          `;

          await client.query(reassignHistoryQuery, [
            uuidv4(),
            ticketId,
            ticket.assigned_to,
            assignedBy,
            new Date(),
            'unassigned',
            `Reassigned to ${assignedTo}`
          ]);
        }

        await client.query('COMMIT');

        // Map updated ticket
        const mappedTicket = this.mapDbTicketToTicket(updatedTicket);

        // Log successful assignment
        logger.info('Ticket assigned successfully', {
          ticketId,
          assignedTo,
          assignedBy,
          previousAssignee: ticket.assigned_to
        });

        // Audit log
        auditLogger.info('Ticket assigned', {
          ticketId,
          action: 'assign',
          assignedTo,
          assignedBy,
          previousAssignee: ticket.assigned_to,
          notes
        });

        return mappedTicket;

      } catch (error) {
        await client.query('ROLLBACK');
        throw error;
      } finally {
        client.release();
      }

    } catch (error) {
      logger.error('Failed to assign ticket', {
        error: error instanceof Error ? error.message : error,
        ticketId,
        assignedTo,
        assignedBy
      });

      throw error;
    }
  }

  /**
   * Unassign ticket from user
   */
  async unassignTicket(
    ticketId: string,
    unassignedBy: string,
    notes?: string
  ): Promise<Ticket> {
    try {
      // Validate ticket ID format
      if (!uuidv4.validate(ticketId)) {
        throw new Error('Invalid ticket ID format');
      }

      // Validate user ID
      if (!uuidv4.validate(unassignedBy)) {
        throw new Error('Invalid user ID format');
      }

      // Check if ticket exists and is assigned
      const ticketQuery = `
        SELECT t.*, u.role as unassigner_role
        FROM tickets t
        LEFT JOIN users u ON u.id = $2
        WHERE t.id = $1
      `;

      const ticketResult = await this.db.query(ticketQuery, [ticketId, unassignedBy]);

      if (ticketResult.length === 0) {
        throw new Error('Ticket not found');
      }

      const ticket = ticketResult[0];

      if (!ticket.assigned_to) {
        throw new Error('Ticket is not assigned to anyone');
      }

      // Validate unassigner role
      if (ticket.unassigner_role !== UserRole.ADMIN && ticket.unassigner_role !== UserRole.AUDITOR) {
        throw new Error('Only admins and auditors can unassign tickets');
      }

      // Start transaction
      const client = await this.db.getClient();
      try {
        await client.query('BEGIN');

        // Update ticket to remove assignment
        const updateQuery = `
          UPDATE tickets
          SET
            assigned_to = NULL,
            status = $1,
            updated_at = $2
          WHERE id = $3
          RETURNING *
        `;

        const updateResult = await client.query(updateQuery, [
          TicketStatus.PENDING,
          new Date(),
          ticketId
        ]);

        const updatedTicket = updateResult[0];

        // Record unassignment in history
        const historyQuery = `
          INSERT INTO ticket_assignments (
            id, ticket_id, assigned_to, assigned_by, assigned_at, status, notes
          ) VALUES ($1, $2, $3, $4, $5, $6, $7)
        `;

        await client.query(historyQuery, [
          uuidv4(),
          ticketId,
          ticket.assigned_to,
          unassignedBy,
          new Date(),
          'unassigned',
          notes
        ]);

        await client.query('COMMIT');

        // Map updated ticket
        const mappedTicket = this.mapDbTicketToTicket(updatedTicket);

        // Log successful unassignment
        logger.info('Ticket unassigned successfully', {
          ticketId,
          unassignedBy,
          previousAssignee: ticket.assigned_to
        });

        // Audit log
        auditLogger.info('Ticket unassigned', {
          ticketId,
          action: 'unassign',
          unassignedBy,
          previousAssignee: ticket.assigned_to,
          notes
        });

        return mappedTicket;

      } catch (error) {
        await client.query('ROLLBACK');
        throw error;
      } finally {
        client.release();
      }

    } catch (error) {
      logger.error('Failed to unassign ticket', {
        error: error instanceof Error ? error.message : error,
        ticketId,
        unassignedBy
      });

      throw error;
    }
  }

  /**
   * Get assignment history for a ticket
   */
  async getAssignmentHistory(ticketId: string): Promise<AssignmentHistory[]> {
    try {
      // Validate ticket ID format
      if (!uuidv4.validate(ticketId)) {
        throw new Error('Invalid ticket ID format');
      }

      const query = `
        SELECT
          id,
          ticket_id,
          assigned_to,
          assigned_by,
          assigned_at,
          status,
          notes
        FROM ticket_assignments
        WHERE ticket_id = $1
        ORDER BY assigned_at DESC
      `;

      const result = await this.db.query(query, [ticketId]);

      return result.map(row => ({
        id: row.id,
        ticketId: row.ticket_id,
        assignedTo: row.assigned_to,
        assignedBy: row.assigned_by,
        assignedAt: new Date(row.assigned_at),
        status: row.status,
        notes: row.notes
      }));

    } catch (error) {
      logger.error('Failed to get assignment history', {
        error: error instanceof Error ? error.message : error,
        ticketId
      });

      throw error;
    }
  }

  /**
   * Get tickets assigned to a user
   */
  async getAssignedTickets(
    userId: string,
    status?: TicketStatus,
    page: number = 1,
    limit: number = 50
  ): Promise<{ tickets: Ticket[]; total: number; page: number; totalPages: number }> {
    try {
      // Validate user ID
      if (!uuidv4.validate(userId)) {
        throw new Error('Invalid user ID format');
      }

      // Validate pagination
      if (page < 1) throw new Error('Page must be greater than 0');
      if (limit < 1 || limit > 100) throw new Error('Limit must be between 1 and 100');

      const offset = (page - 1) * limit;

      // Build query
      let whereClause = 'WHERE assigned_to = $1';
      const countParams: unknown[] = [userId];
      const dataParams: unknown[] = [userId, limit, offset];

      if (status) {
        whereClause += ' AND status = $2';
        countParams.push(status);
        dataParams.splice(1, 0, status);
      }

      // Get total count
      const countQuery = `SELECT COUNT(*) as total FROM tickets ${whereClause}`;
      const countResult = await this.db.query(countQuery, countParams);
      const total = parseInt(countResult[0].total, 10);

      // Get paginated results
      const dataQuery = `
        SELECT * FROM tickets
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT $${dataParams.length - 1} OFFSET $${dataParams.length}
      `;

      const ticketsResult = await this.db.query(dataQuery, dataParams);
      const tickets = ticketsResult.map(ticket => this.mapDbTicketToTicket(ticket));

      const totalPages = Math.ceil(total / limit);

      return {
        tickets,
        total,
        page,
        totalPages
      };

    } catch (error) {
      logger.error('Failed to get assigned tickets', {
        error: error instanceof Error ? error.message : error,
        userId,
        status,
        page,
        limit
      });

      throw error;
    }
  }

  /**
   * Get assignment statistics
   */
  async getAssignmentStatistics(
    startDate?: Date,
    endDate?: Date
  ): Promise<{
    totalAssignments: number;
    activeAssignments: number;
    completedAssignments: number;
    averageAssignmentTime: number;
    topPerformers: Array<{
      userId: string;
      userName: string;
      completedCount: number;
      averageTime: number;
    }>;
  }> {
    try {
      let dateFilter = '';
      const params: unknown[] = [];

      if (startDate || endDate) {
        const conditions: string[] = [];

        if (startDate) {
          conditions.push('ta.assigned_at >= $' + (params.length + 1));
          params.push(startDate);
        }

        if (endDate) {
          conditions.push('ta.assigned_at <= $' + (params.length + 1));
          params.push(endDate);
        }

        dateFilter = 'WHERE ' + conditions.join(' AND ');
      }

      // Get total assignments
      const totalQuery = `
        SELECT COUNT(*) as total
        FROM ticket_assignments ta
        ${dateFilter}
      `;

      const totalResult = await this.db.query(totalQuery, params);
      const totalAssignments = parseInt(totalResult[0].total, 10);

      // Get active assignments
      const activeQuery = `
        SELECT COUNT(*) as active
        FROM tickets t
        JOIN ticket_assignments ta ON t.id = ta.ticket_id
        WHERE t.assigned_to IS NOT NULL
        AND t.status NOT IN ('completed', 'cancelled')
        ${dateFilter.replace('ta.assigned_at', 'ta.assigned_at')}
      `;

      const activeResult = await this.db.query(activeQuery, params);
      const activeAssignments = parseInt(activeResult[0].active, 10);

      // Get completed assignments
      const completedQuery = `
        SELECT COUNT(*) as completed
        FROM tickets t
        JOIN ticket_assignments ta ON t.id = ta.ticket_id
        WHERE t.status = 'completed'
        AND t.completed_at IS NOT NULL
        ${dateFilter.replace('ta.assigned_at', 'ta.assigned_at')}
      `;

      const completedResult = await this.db.query(completedQuery, params);
      const completedAssignments = parseInt(completedResult[0].completed, 10);

      // Get average assignment time
      const timeQuery = `
        SELECT AVG(
          EXTRACT(EPOCH FROM (t.completed_at - ta.assigned_at))
        ) as avg_time
        FROM tickets t
        JOIN ticket_assignments ta ON t.id = ta.ticket_id
        WHERE t.status = 'completed'
        AND t.completed_at IS NOT NULL
        ${dateFilter.replace('ta.assigned_at', 'ta.assigned_at')}
      `;

      const timeResult = await this.db.query(timeQuery, params);
      const averageAssignmentTime = timeResult[0].avg_time
        ? parseFloat(timeResult[0].avg_time)
        : 0;

      // Get top performers
      const performersQuery = `
        SELECT
          ta.assigned_to as user_id,
          u.username as user_name,
          COUNT(t.id) as completed_count,
          AVG(EXTRACT(EPOCH FROM (t.completed_at - ta.assigned_at))) as avg_time
        FROM tickets t
        JOIN ticket_assignments ta ON t.id = ta.ticket_id
        JOIN users u ON u.id = ta.assigned_to
        WHERE t.status = 'completed'
        AND t.completed_at IS NOT NULL
        ${dateFilter.replace('ta.assigned_at', 'ta.assigned_at')}
        GROUP BY ta.assigned_to, u.username
        ORDER BY completed_count DESC
        LIMIT 10
      `;

      const performersResult = await this.db.query(performersQuery, params);
      const topPerformers = performersResult.map(row => ({
        userId: row.user_id,
        userName: row.user_name,
        completedCount: parseInt(row.completed_count, 10),
        averageTime: parseFloat(row.avg_time) || 0
      }));

      return {
        totalAssignments,
        activeAssignments,
        completedAssignments,
        averageAssignmentTime: Math.round(averageAssignmentTime),
        topPerformers
      };

    } catch (error) {
      logger.error('Failed to get assignment statistics', {
        error: error instanceof Error ? error.message : error,
        startDate,
        endDate
      });

      throw error;
    }
  }

  /**
   * Map database ticket to Ticket type
   */
  private mapDbTicketToTicket(dbTicket: any): Ticket {
    return {
      id: dbTicket.id,
      ticketNumber: dbTicket.ticket_number,
      drNumber: dbTicket.dr_number,
      technicianNumber: dbTicket.technician_number,
      technicianName: dbTicket.technician_name,
      messageContent: dbTicket.message_content,
      messageTimestamp: new Date(dbTicket.message_timestamp),
      status: dbTicket.status,
      priority: dbTicket.priority,
      assignedTo: dbTicket.assigned_to,
      propertyId: dbTicket.property_id,
      jobId: dbTicket.job_id,
      address: dbTicket.address,
      installationStatus: dbTicket.installation_status,
      complianceScore: dbTicket.compliance_score,
      completedAt: dbTicket.completed_at ? new Date(dbTicket.completed_at) : undefined,
      createdAt: new Date(dbTicket.created_at),
      updatedAt: new Date(dbTicket.updated_at)
    };
  }
}