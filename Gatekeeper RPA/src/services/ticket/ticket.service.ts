import { v4 as uuidv4 } from 'uuid';
import { database } from '@/database/database';
import { Ticket, TicketStatus, Priority, UserRole, Ticket } from '@/types';
import { InputSanitizer, ValidationUtils } from '@/utils/validation';
import { logger, auditLogger } from '@/utils/logger';
import { z } from 'zod';

const ticketUpdateSchema = z.object({
  status: z.enum(['pending', 'assigned', 'in_progress', 'completed', 'failed', 'cancelled']).optional(),
  priority: z.enum(['low', 'normal', 'high', 'urgent']).optional(),
  assignedTo: z.string().uuid().optional(),
  propertyId: z.string().optional(),
  jobId: z.string().optional(),
  address: z.string().optional(),
  installationStatus: z.string().optional(),
  complianceScore: z.number().min(0).max(100).optional()
});

export class TicketService {
  constructor() {}

  /**
   * Create a new ticket with comprehensive validation
   */
  async createTicket(data: {
    drNumber: string;
    technicianNumber: string;
    technicianName: string;
    messageContent: string;
    priority?: Priority;
  }): Promise<Ticket> {
    try {
      // Validate required fields
      const requiredFieldsValidation = ValidationUtils.validateRequired(data, [
        'drNumber', 'technicianNumber', 'technicianName', 'messageContent'
      ]);

      if (!requiredFieldsValidation.isValid) {
        throw new Error(requiredFieldsValidation.errors.join(', '));
      }

      // Sanitize inputs
      const sanitizedData = {
        drNumber: InputSanitizer.validateDRNumber(data.drNumber),
        technicianNumber: InputSanitizer.validatePhoneNumber(data.technicianNumber),
        technicianName: InputSanitizer.sanitizeString(data.technicianName),
        messageContent: InputSanitizer.sanitizeString(data.messageContent),
        priority: data.priority || Priority.NORMAL
      };

      // Validate sanitized data
      if (!sanitizedData.drNumber) {
        throw new Error('Invalid DR number format');
      }

      if (!sanitizedData.technicianNumber) {
        throw new Error('Invalid phone number format');
      }

      // Generate unique identifiers
      const ticketId = uuidv4();
      const ticketNumber = this.generateTicketNumber();

      // Check for duplicate DR number
      const existingTicket = await database.query(
        'SELECT id FROM tickets WHERE dr_number = $1 AND status NOT IN ($2, $3)',
        [sanitizedData.drNumber, TicketStatus.COMPLETED, TicketStatus.CANCELLED]
      );

      if (existingTicket.length > 0) {
        throw new Error(`A ticket for DR ${sanitizedData.drNumber} already exists`);
      }

      // Insert ticket into database
      const result = await database.query(
        `INSERT INTO tickets (
          id, ticket_number, dr_number, technician_number, technician_name,
          message_content, status, priority, created_at, updated_at
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
        RETURNING *`,
        [
          ticketId,
          ticketNumber,
          sanitizedData.drNumber,
          sanitizedData.technicianNumber,
          sanitizedData.technicianName,
          sanitizedData.messageContent,
          TicketStatus.PENDING,
          sanitizedData.priority,
          new Date(),
          new Date()
        ]
      );

      const ticket = result[0];

      // Log ticket creation
      logger.info('Ticket created successfully', {
        ticketId,
        ticketNumber,
        drNumber: sanitizedData.drNumber,
        technicianNumber: sanitizedData.technicianNumber
      });

      // Audit log
      auditLogger.info('Ticket created', {
        ticketId,
        action: 'create',
        data: {
          drNumber: sanitizedData.drNumber,
          technicianNumber: sanitizedData.technicianNumber,
          priority: sanitizedData.priority
        }
      });

      return this.mapDbTicketToTicket(ticket);

    } catch (error) {
      logger.error('Failed to create ticket', {
        error: error instanceof Error ? error.message : error,
        data
      });

      throw error;
    }
  }

  /**
   * Get ticket by ID with access control
   */
  async getTicket(ticketId: string, userId?: string, userRole?: UserRole): Promise<Ticket | null> {
    try {
      // Validate ticket ID format
      if (!uuidv4.validate(ticketId)) {
        throw new Error('Invalid ticket ID format');
      }

      // Build query with access control
      let query = 'SELECT * FROM tickets WHERE id = $1';
      const params: unknown[] = [ticketId];

      // Apply role-based access control
      if (userRole && userRole !== UserRole.ADMIN) {
        if (userRole === UserRole.TECHNICIAN) {
          query += ' AND technician_number = (SELECT phone_number FROM users WHERE id = $2)';
          params.push(userId);
        } else if (userRole === UserRole.AUDITOR) {
          query += ' AND (assigned_to = $2 OR status IN ($3, $4))';
          params.push(userId, TicketStatus.PENDING, TicketStatus.ASSIGNED);
        }
      }

      const result = await database.query(query, params);

      if (result.length === 0) {
        return null;
      }

      return this.mapDbTicketToTicket(result[0]);

    } catch (error) {
      logger.error('Failed to get ticket', {
        error: error instanceof Error ? error.message : error,
        ticketId,
        userId,
        userRole
      });

      throw error;
    }
  }

  /**
   * Update ticket with comprehensive validation
   */
  async updateTicket(
    ticketId: string,
    updates: Partial<{
      status: TicketStatus;
      priority: Priority;
      assignedTo: string;
      propertyId: string;
      jobId: string;
      address: string;
      installationStatus: string;
      complianceScore: number;
    }>,
    userId?: string,
    userRole?: UserRole
  ): Promise<Ticket> {
    try {
      // Validate ticket ID format
      if (!uuidv4.validate(ticketId)) {
        throw new Error('Invalid ticket ID format');
      }

      // Validate update data
      const validation = ValidationUtils.validate(updates, ticketUpdateSchema);
      if (!validation.isValid) {
        throw new Error(validation.errors.join(', '));
      }

      // Check if ticket exists and user has access
      const existingTicket = await this.getTicket(ticketId, userId, userRole);
      if (!existingTicket) {
        throw new Error('Ticket not found or access denied');
      }

      // Prepare update fields
      const updateFields: string[] = [];
      const updateValues: unknown[] = [];
      let paramIndex = 1;

      // Sanitize and validate each field
      if (updates.status !== undefined) {
        updateFields.push(`status = $${paramIndex++}`);
        updateValues.push(updates.status);
      }

      if (updates.priority !== undefined) {
        updateFields.push(`priority = $${paramIndex++}`);
        updateValues.push(updates.priority);
      }

      if (updates.assignedTo !== undefined) {
        if (updates.assignedTo && !uuidv4.validate(updates.assignedTo)) {
          throw new Error('Invalid assignedTo user ID format');
        }
        updateFields.push(`assigned_to = $${paramIndex++}`);
        updateValues.push(updates.assignedTo);
      }

      if (updates.propertyId !== undefined) {
        updateFields.push(`property_id = $${paramIndex++}`);
        updateValues.push(InputSanitizer.sanitizeString(updates.propertyId));
      }

      if (updates.jobId !== undefined) {
        updateFields.push(`job_id = $${paramIndex++}`);
        updateValues.push(InputSanitizer.sanitizeString(updates.jobId));
      }

      if (updates.address !== undefined) {
        updateFields.push(`address = $${paramIndex++}`);
        updateValues.push(InputSanitizer.sanitizeString(updates.address));
      }

      if (updates.installationStatus !== undefined) {
        updateFields.push(`installation_status = $${paramIndex++}`);
        updateValues.push(InputSanitizer.sanitizeString(updates.installationStatus));
      }

      if (updates.complianceScore !== undefined) {
        if (updates.complianceScore < 0 || updates.complianceScore > 100) {
          throw new Error('Compliance score must be between 0 and 100');
        }
        updateFields.push(`compliance_score = $${paramIndex++}`);
        updateValues.push(updates.complianceScore);
      }

      // Add updated_at timestamp
      updateFields.push(`updated_at = $${paramIndex++}`);
      updateValues.push(new Date());

      // Set completion timestamp if status is completed
      if (updates.status === TicketStatus.COMPLETED) {
        updateFields.push(`completed_at = $${paramIndex++}`);
        updateValues.push(new Date());
      }

      // Build update query
      if (updateFields.length === 0) {
        return existingTicket;
      }

      const query = `
        UPDATE tickets
        SET ${updateFields.join(', ')}
        WHERE id = $${paramIndex++}
        RETURNING *
      `;

      updateValues.push(ticketId);

      // Execute update
      const result = await database.query(query, updateValues);

      if (result.length === 0) {
        throw new Error('Failed to update ticket');
      }

      const updatedTicket = this.mapDbTicketToTicket(result[0]);

      // Log ticket update
      logger.info('Ticket updated successfully', {
        ticketId,
        updates,
        userId
      });

      // Audit log
      auditLogger.info('Ticket updated', {
        ticketId,
        action: 'update',
        userId,
        updates
      });

      return updatedTicket;

    } catch (error) {
      logger.error('Failed to update ticket', {
        error: error instanceof Error ? error.message : error,
        ticketId,
        updates,
        userId
      });

      throw error;
    }
  }

  /**
   * Get tickets by status with pagination
   */
  async getTicketsByStatus(
    status: TicketStatus,
    page: number = 1,
    limit: number = 50,
    userId?: string,
    userRole?: UserRole
  ): Promise<{ tickets: Ticket[]; total: number; page: number; totalPages: number }> {
    try {
      // Validate pagination parameters
      if (page < 1) throw new Error('Page must be greater than 0');
      if (limit < 1 || limit > 100) throw new Error('Limit must be between 1 and 100');

      const offset = (page - 1) * limit;

      // Build query with access control
      let whereClause = 'WHERE status = $1';
      const countParams: unknown[] = [status];
      const dataParams: unknown[] = [status, limit, offset];

      // Apply role-based access control
      if (userRole && userRole !== UserRole.ADMIN) {
        if (userRole === UserRole.TECHNICIAN) {
          whereClause += ' AND technician_number = (SELECT phone_number FROM users WHERE id = $2)';
          countParams.push(userId);
          dataParams.splice(1, 0, userId);
        } else if (userRole === UserRole.AUDITOR) {
          whereClause += ' AND (assigned_to = $2 OR status IN ($3, $4))';
          countParams.push(userId, TicketStatus.PENDING, TicketStatus.ASSIGNED);
          dataParams.splice(1, 0, userId, TicketStatus.PENDING, TicketStatus.ASSIGNED);
        }
      }

      // Get total count
      const countQuery = `SELECT COUNT(*) as total FROM tickets ${whereClause}`;
      const countResult = await database.query(countQuery, countParams);
      const total = parseInt(countResult[0].total, 10);

      // Get paginated results
      const dataQuery = `
        SELECT * FROM tickets
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT $${dataParams.length - 1} OFFSET $${dataParams.length}
      `;

      const ticketsResult = await database.query(dataQuery, dataParams);
      const tickets = ticketsResult.map(ticket => this.mapDbTicketToTicket(ticket));

      const totalPages = Math.ceil(total / limit);

      return {
        tickets,
        total,
        page,
        totalPages
      };

    } catch (error) {
      logger.error('Failed to get tickets by status', {
        error: error instanceof Error ? error.message : error,
        status,
        page,
        limit
      });

      throw error;
    }
  }

  /**
   * Get tickets by technician with pagination
   */
  async getTicketsByTechnician(
    technicianNumber: string,
    page: number = 1,
    limit: number = 50,
    userId?: string,
    userRole?: UserRole
  ): Promise<{ tickets: Ticket[]; total: number; page: number; totalPages: number }> {
    try {
      // Validate technician number
      const sanitizedNumber = InputSanitizer.validatePhoneNumber(technicianNumber);
      if (!sanitizedNumber) {
        throw new Error('Invalid phone number format');
      }

      // Apply access control
      if (userRole === UserRole.TECHNICIAN) {
        const userPhone = await database.query(
          'SELECT phone_number FROM users WHERE id = $1',
          [userId]
        );

        if (userPhone.length === 0 || userPhone[0].phone_number !== sanitizedNumber) {
          throw new Error('Access denied: can only view your own tickets');
        }
      }

      // Validate pagination
      if (page < 1) throw new Error('Page must be greater than 0');
      if (limit < 1 || limit > 100) throw new Error('Limit must be between 1 and 100');

      const offset = (page - 1) * limit;

      // Get total count
      const countQuery = 'SELECT COUNT(*) as total FROM tickets WHERE technician_number = $1';
      const countResult = await database.query(countQuery, [sanitizedNumber]);
      const total = parseInt(countResult[0].total, 10);

      // Get paginated results
      const dataQuery = `
        SELECT * FROM tickets
        WHERE technician_number = $1
        ORDER BY created_at DESC
        LIMIT $2 OFFSET $3
      `;

      const ticketsResult = await database.query(dataQuery, [sanitizedNumber, limit, offset]);
      const tickets = ticketsResult.map(ticket => this.mapDbTicketToTicket(ticket));

      const totalPages = Math.ceil(total / limit);

      return {
        tickets,
        total,
        page,
        totalPages
      };

    } catch (error) {
      logger.error('Failed to get tickets by technician', {
        error: error instanceof Error ? error.message : error,
        technicianNumber,
        page,
        limit
      });

      throw error;
    }
  }

  /**
   * Get ticket metrics
   */
  async getTicketMetrics(
    startDate?: Date,
    endDate?: Date,
    userId?: string,
    userRole?: UserRole
  ): Promise<{
    total: number;
    completed: number;
    pending: number;
    inProgress: number;
    failed: number;
    cancelled: number;
    completionRate: number;
    averageProcessingTime: number;
  }> {
    try {
      let whereClause = '';
      const params: unknown[] = [];

      // Apply date filters
      if (startDate || endDate) {
        const conditions: string[] = [];

        if (startDate) {
          conditions.push('created_at >= $' + (params.length + 1));
          params.push(startDate);
        }

        if (endDate) {
          conditions.push('created_at <= $' + (params.length + 1));
          params.push(endDate);
        }

        whereClause = 'WHERE ' + conditions.join(' AND ');
      }

      // Apply access control
      if (userRole && userRole !== UserRole.ADMIN) {
        const accessCondition = userRole === UserRole.TECHNICIAN
          ? 'technician_number = (SELECT phone_number FROM users WHERE id = $' + (params.length + 1) + ')'
          : 'assigned_to = $' + (params.length + 1) + ' OR status IN ($' + (params.length + 2) + ', $' + (params.length + 3) + ')';

        params.push(userId);
        if (userRole === UserRole.AUDITOR) {
          params.push(TicketStatus.PENDING, TicketStatus.ASSIGNED);
        }

        whereClause += (whereClause ? ' AND ' : 'WHERE ') + accessCondition;
      }

      // Get status counts
      const statusQuery = `
        SELECT
          status,
          COUNT(*) as count
        FROM tickets
        ${whereClause}
        GROUP BY status
      `;

      const statusResult = await database.query(statusQuery, params);
      const statusCounts = statusResult.reduce((acc, row) => {
        acc[row.status] = parseInt(row.count, 10);
        return acc;
      }, {} as Record<string, number>);

      const total = Object.values(statusCounts).reduce((sum, count) => sum + count, 0);
      const completed = statusCounts[TicketStatus.COMPLETED] || 0;
      const pending = statusCounts[TicketStatus.PENDING] || 0;
      const inProgress = statusCounts[TicketStatus.IN_PROGRESS] || 0;
      const failed = statusCounts[TicketStatus.FAILED] || 0;
      const cancelled = statusCounts[TicketStatus.CANCELLED] || 0;

      const completionRate = total > 0 ? (completed / total) * 100 : 0;

      // Get average processing time
      const processingTimeQuery = `
        SELECT AVG(
          EXTRACT(EPOCH FROM (completed_at - created_at))
        ) as avg_processing_time
        FROM tickets
        ${whereClause ? whereClause + ' AND ' : 'WHERE '}
          completed_at IS NOT NULL
      `;

      const processingTimeResult = await database.query(processingTimeQuery, params);
      const averageProcessingTime = processingTimeResult[0].avg_processing_time
        ? parseFloat(processingTimeResult[0].avg_processing_time)
        : 0;

      return {
        total,
        completed,
        pending,
        inProgress,
        failed,
        cancelled,
        completionRate: Math.round(completionRate * 100) / 100,
        averageProcessingTime: Math.round(averageProcessingTime)
      };

    } catch (error) {
      logger.error('Failed to get ticket metrics', {
        error: error instanceof Error ? error.message : error,
        startDate,
        endDate,
        userId,
        userRole
      });

      throw error;
    }
  }

  /**
   * Search tickets with filtering and pagination
   */
  async searchTickets(
    filters: {
      search?: string;
      status?: TicketStatus;
      priority?: Priority;
      technicianNumber?: string;
      assignedTo?: string;
      startDate?: Date;
      endDate?: Date;
    },
    page: number = 1,
    limit: number = 50,
    userId?: string,
    userRole?: UserRole
  ): Promise<{ tickets: Ticket[]; total: number; page: number; totalPages: number }> {
    try {
      // Validate pagination
      if (page < 1) throw new Error('Page must be greater than 0');
      if (limit < 1 || limit > 100) throw new Error('Limit must be between 1 and 100');

      const offset = (page - 1) * limit;
      const whereConditions: string[] = [];
      const countParams: unknown[] = [];
      const dataParams: unknown[] = [];

      // Build filter conditions
      if (filters.search) {
        const searchTerm = `%${InputSanitizer.sanitizeString(filters.search)}%`;
        whereConditions.push('(dr_number ILIKE $1 OR technician_name ILIKE $1 OR message_content ILIKE $1)');
        countParams.push(searchTerm);
        dataParams.push(searchTerm);
      }

      if (filters.status) {
        whereConditions.push(`status = $${countParams.length + 1}`);
        countParams.push(filters.status);
        dataParams.push(filters.status);
      }

      if (filters.priority) {
        whereConditions.push(`priority = $${countParams.length + 1}`);
        countParams.push(filters.priority);
        dataParams.push(filters.priority);
      }

      if (filters.technicianNumber) {
        const sanitizedNumber = InputSanitizer.validatePhoneNumber(filters.technicianNumber);
        if (sanitizedNumber) {
          whereConditions.push(`technician_number = $${countParams.length + 1}`);
          countParams.push(sanitizedNumber);
          dataParams.push(sanitizedNumber);
        }
      }

      if (filters.assignedTo) {
        if (!uuidv4.validate(filters.assignedTo)) {
          throw new Error('Invalid assignedTo user ID format');
        }
        whereConditions.push(`assigned_to = $${countParams.length + 1}`);
        countParams.push(filters.assignedTo);
        dataParams.push(filters.assignedTo);
      }

      if (filters.startDate) {
        whereConditions.push(`created_at >= $${countParams.length + 1}`);
        countParams.push(filters.startDate);
        dataParams.push(filters.startDate);
      }

      if (filters.endDate) {
        whereConditions.push(`created_at <= $${countParams.length + 1}`);
        countParams.push(filters.endDate);
        dataParams.push(filters.endDate);
      }

      // Apply access control
      if (userRole && userRole !== UserRole.ADMIN) {
        if (userRole === UserRole.TECHNICIAN) {
          whereConditions.push(`technician_number = (SELECT phone_number FROM users WHERE id = $${countParams.length + 1})`);
          countParams.push(userId);
          dataParams.push(userId);
        } else if (userRole === UserRole.AUDITOR) {
          whereConditions.push(`(assigned_to = $${countParams.length + 1} OR status IN ($${countParams.length + 2}, $${countParams.length + 3}))`);
          countParams.push(userId, TicketStatus.PENDING, TicketStatus.ASSIGNED);
          dataParams.push(userId, TicketStatus.PENDING, TicketStatus.ASSIGNED);
        }
      }

      // Build where clause
      const whereClause = whereConditions.length > 0
        ? 'WHERE ' + whereConditions.join(' AND ')
        : '';

      // Get total count
      const countQuery = `SELECT COUNT(*) as total FROM tickets ${whereClause}`;
      const countResult = await database.query(countQuery, countParams);
      const total = parseInt(countResult[0].total, 10);

      // Get paginated results
      dataParams.push(limit, offset);
      const dataQuery = `
        SELECT * FROM tickets
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT $${dataParams.length - 1} OFFSET $${dataParams.length}
      `;

      const ticketsResult = await database.query(dataQuery, dataParams);
      const tickets = ticketsResult.map(ticket => this.mapDbTicketToTicket(ticket));

      const totalPages = Math.ceil(total / limit);

      return {
        tickets,
        total,
        page,
        totalPages
      };

    } catch (error) {
      logger.error('Failed to search tickets', {
        error: error instanceof Error ? error.message : error,
        filters,
        page,
        limit
      });

      throw error;
    }
  }

  /**
   * Generate unique ticket number
   */
  private generateTicketNumber(): string {
    const date = new Date();
    const dateStr = date.toISOString().slice(0, 10).replace(/-/g, '');
    const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    return `TICKET-${dateStr}-${random}`;
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

  /**
   * Get tickets with filtering and pagination
   */
  async getTickets(filters: {
    status?: string[];
    priority?: string[];
    assignedTo?: string;
    technicianNumber?: string;
    drNumber?: string;
    dateFrom?: Date;
    dateTo?: Date;
    page: number;
    limit: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
  }) {
    const {
      status,
      priority,
      assignedTo,
      technicianNumber,
      drNumber,
      dateFrom,
      dateTo,
      page,
      limit,
      sortBy = 'created_at',
      sortOrder = 'desc'
    } = filters;

    const offset = (page - 1) * limit;

    // Build WHERE clause
    const whereConditions: string[] = [];
    const params: unknown[] = [];
    let paramIndex = 1;

    if (status && status.length > 0) {
      whereConditions.push(`status = ANY($${paramIndex++})`);
      params.push(status);
    }

    if (priority && priority.length > 0) {
      whereConditions.push(`priority = ANY($${paramIndex++})`);
      params.push(priority);
    }

    if (assignedTo) {
      whereConditions.push(`assigned_to = $${paramIndex++}`);
      params.push(assignedTo);
    }

    if (technicianNumber) {
      whereConditions.push(`technician_number = $${paramIndex++}`);
      params.push(technicianNumber);
    }

    if (drNumber) {
      whereConditions.push(`dr_number = $${paramIndex++}`);
      params.push(drNumber);
    }

    if (dateFrom) {
      whereConditions.push(`created_at >= $${paramIndex++}`);
      params.push(dateFrom);
    }

    if (dateTo) {
      whereConditions.push(`created_at <= $${paramIndex++}`);
      params.push(dateTo);
    }

    const whereClause = whereConditions.length > 0 ? `WHERE ${whereConditions.join(' AND ')}` : '';

    // Get total count
    const countQuery = `SELECT COUNT(*) as total FROM tickets ${whereClause}`;
    const countResult = await database.query(countQuery, params);
    const total = parseInt(countResult[0].total, 10);

    // Get paginated results
    const dataQuery = `
      SELECT * FROM tickets
      ${whereClause}
      ORDER BY ${sortBy} ${sortOrder.toUpperCase()}
      LIMIT $${paramIndex++} OFFSET $${paramIndex++}
    `;

    params.push(limit, offset);
    const ticketsResult = await database.query(dataQuery, params);
    const tickets = ticketsResult.map(ticket => this.mapDbTicketToTicket(ticket));

    return {
      data: tickets,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    };
  }
}

// Export singleton instance
export const ticketService = new TicketService();