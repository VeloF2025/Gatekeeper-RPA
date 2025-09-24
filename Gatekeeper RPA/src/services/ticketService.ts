import { db } from '@/database/database';
import { tickets, ticketAssignments, ticketMetrics, auditPhotos, workflowEvents } from '@/database/schemas';
import { eq, and, desc, or, ilike, gte, lte } from 'drizzle-orm';
import { logger } from '@/lib/logger';
import { securityService } from './securityService';
import { WhatiTicketMessage } from './whatiTicketService';

export interface TicketData {
  id: string;
  ticketNumber: string;
  drNumber: string;
  technicianNumber: string;
  technicianName?: string;
  messageContent: string;
  messageTimestamp: Date;
  status: 'pending' | 'processing' | 'completed' | 'failed' | 'cancelled';
  priority: 'low' | 'normal' | 'high' | 'urgent';
  assignedTo?: string;
  createdAt: Date;
  updatedAt: Date;
  completedAt?: Date;
  whatsappMessageId: string;
  externalTicketId?: string;
  externalSystem: string;
  syncStatus: 'pending' | 'synced' | 'failed';
}

export interface TicketCreateData {
  from: string;
  content: string;
  drNumber: string;
  timestamp: number;
  messageType: 'text' | 'image' | 'document' | 'audio' | 'video';
  media?: Array<{
    id: string;
    type: string;
    mimeType: string;
    fileSize: number;
    url?: string;
    caption?: string;
  }>;
}

export interface TicketValidation {
  isValid: boolean;
  errors: string[];
  warnings: string[];
  sanitized?: Partial<TicketCreateData>;
}

export interface TicketSearchCriteria {
  status?: TicketData['status'] | TicketData['status'][];
  priority?: TicketData['priority'] | TicketData['priority'][];
  technicianNumber?: string;
  drNumber?: string;
  dateRange?: {
    start: Date;
    end: Date;
  };
  search?: string;
  assignedTo?: string;
  limit?: number;
  offset?: number;
}

export interface TicketMetrics {
  totalTickets: number;
  pendingTickets: number;
  processingTickets: number;
  completedTickets: number;
  failedTickets: number;
  averageProcessingTime: number;
  ticketsByPriority: Record<TicketData['priority'], number>;
  ticketsByStatus: Record<TicketData['status'], number>;
}

export class TicketService {
  private readonly TICKET_NUMBER_PREFIX = 'TICKET';
  private readonly MAX_RETRY_COUNT = 3;

  /**
   * Create ticket from validated WhatsApp message
   * Follows Zero Trust principles - validate all inputs
   */
  async createFromWhatsApp(messageData: TicketCreateData): Promise<TicketData> {
    try {
      logger.info('Creating ticket from WhatsApp message', {
        from: messageData.from,
        drNumber: messageData.drNumber,
        messageType: messageData.messageType
      });

      // Step 1: Validate input data
      const validation = await this.validateTicketData(messageData);
      if (!validation.isValid) {
        throw new Error(`Validation failed: ${validation.errors.join(', ')}`);
      }

      // Step 2: Check for duplicates
      const duplicateCheck = await this.checkForDuplicate(
        messageData.drNumber,
        messageData.from
      );

      if (duplicateCheck.isDuplicate) {
        logger.info('Duplicate ticket detected', {
          drNumber: messageData.drNumber,
          from: messageData.from,
          originalTicketId: duplicateCheck.originalTicketId
        });

        // Return the original ticket as a duplicate
        return duplicateCheck.originalTicket!;
      }

      // Step 3: Generate ticket number
      const ticketNumber = this.generateTicketNumber();

      // Step 4: Create ticket record
      const ticket = await db.insert(tickets).values({
        ticketNumber,
        drNumber: messageData.drNumber,
        technicianNumber: messageData.from,
        messageContent: validation.sanitized?.content || messageData.content,
        messageTimestamp: new Date(messageData.timestamp),
        status: 'pending',
        priority: this.determinePriority(messageData.content),
        whatsappMessageId: this.generateMessageId(),
        externalSystem: 'whatsapp',
        syncStatus: 'pending',
        createdAt: new Date(),
        updatedAt: new Date()
      }).returning().then(rows => rows[0]);

      // Step 5: Process media if present
      if (messageData.media && messageData.media.length > 0) {
        await this.processTicketMedia(ticket.id, messageData.media);
      }

      // Step 6: Log ticket creation event
      await securityService.logAuditTrail({
        type: 'ticket_created_from_whatsapp',
        data: {
          ticketId: ticket.id,
          ticketNumber: ticket.ticketNumber,
          drNumber: ticket.drNumber,
          from: messageData.from,
          messageType: messageData.messageType,
          hasMedia: !!messageData.media?.length
        },
        timestamp: Date.now(),
        userId: 'system'
      });

      // Step 7: Record metrics
      await this.recordTicketMetric(ticket.id, 'created', 1, 'count');

      logger.info('Ticket created successfully', {
        ticketId: ticket.id,
        ticketNumber: ticket.ticketNumber,
        drNumber: ticket.drNumber
      });

      return ticket;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      logger.error('Failed to create ticket from WhatsApp', {
        from: messageData.from,
        drNumber: messageData.drNumber,
        error: errorMessage
      });

      throw error;
    }
  }

  /**
   * Validate ticket data before creation
   */
  async validateTicketData(data: TicketCreateData): Promise<TicketValidation> {
    const errors: string[] = [];
    const warnings: string[] = [];
    const sanitized: Partial<TicketCreateData> = {};

    try {
      // Validate phone number
      if (!data.from || data.from.trim().length === 0) {
        errors.push('Phone number is required');
      } else if (!this.validatePhoneNumber(data.from)) {
        errors.push('Invalid phone number format');
      } else {
        sanitized.from = data.from.trim();
      }

      // Validate DR number
      if (!data.drNumber || data.drNumber.trim().length === 0) {
        errors.push('DR number is required');
      } else if (!this.validateDRNumber(data.drNumber)) {
        errors.push('Invalid DR number format');
      } else {
        sanitized.drNumber = data.drNumber.trim().toUpperCase();
      }

      // Validate content
      if (!data.content || data.content.trim().length === 0) {
        errors.push('Message content is required');
      } else {
        const sanitizedContent = await securityService.sanitizeMessage(data.content);
        if (sanitizedContent.length === 0) {
          errors.push('Message content is empty after sanitization');
        } else if (sanitizedContent.length > 5000) {
          errors.push('Message content too long');
        } else {
          sanitized.content = sanitizedContent;
        }
      }

      // Validate timestamp
      if (!data.timestamp || data.timestamp <= 0) {
        errors.push('Valid timestamp is required');
      } else {
        const messageDate = new Date(data.timestamp);
        const now = new Date();
        const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);

        if (messageDate > now) {
          warnings.push('Message timestamp is in the future');
        } else if (messageDate < oneHourAgo) {
          warnings.push('Message timestamp is more than 1 hour old');
        }
      }

      // Validate message type
      const validTypes = ['text', 'image', 'document', 'audio', 'video'];
      if (!data.messageType || !validTypes.includes(data.messageType)) {
        errors.push('Invalid message type');
      } else {
        sanitized.messageType = data.messageType;
      }

      // Validate media if present
      if (data.media && data.media.length > 0) {
        const mediaValidation = await this.validateMediaData(data.media);
        if (!mediaValidation.valid) {
          errors.push(...mediaValidation.errors);
        }
        warnings.push(...mediaValidation.warnings);
        sanitized.media = mediaValidation.sanitized;
      }

      // Additional business validations
      if (sanitized.content && sanitized.content.toLowerCase().includes('test')) {
        warnings.push('Message appears to be a test message');
      }

      if (sanitized.content && sanitized.content.length < 10) {
        warnings.push('Message content is very short');
      }

      return {
        isValid: errors.length === 0,
        errors,
        warnings: warnings.length > 0 ? warnings : undefined,
        sanitized
      };
    } catch (error) {
      logger.error('Ticket data validation failed', {
        error: error instanceof Error ? error.message : 'Unknown error'
      });

      return {
        isValid: false,
        errors: ['Validation process failed'],
        warnings
      };
    }
  }

  /**
   * Search tickets with various criteria
   */
  async searchTickets(criteria: TicketSearchCriteria): Promise<{
    tickets: TicketData[];
    total: number;
    hasMore: boolean;
  }> {
    try {
      let query = db.select().from(tickets);

      // Build filters
      const filters = [];

      if (criteria.status) {
        if (Array.isArray(criteria.status)) {
          filters.push(or(...criteria.status.map(status => eq(tickets.status, status))));
        } else {
          filters.push(eq(tickets.status, criteria.status));
        }
      }

      if (criteria.priority) {
        if (Array.isArray(criteria.priority)) {
          filters.push(or(...criteria.priority.map(priority => eq(tickets.priority, priority))));
        } else {
          filters.push(eq(tickets.priority, criteria.priority));
        }
      }

      if (criteria.technicianNumber) {
        filters.push(eq(tickets.technicianNumber, criteria.technicianNumber));
      }

      if (criteria.drNumber) {
        filters.push(eq(tickets.drNumber, criteria.drNumber));
      }

      if (criteria.assignedTo) {
        filters.push(eq(tickets.assignedTo, criteria.assignedTo));
      }

      if (criteria.dateRange) {
        filters.push(and(
          gte(tickets.createdAt, criteria.dateRange.start),
          lte(tickets.createdAt, criteria.dateRange.end)
        ));
      }

      if (criteria.search) {
        const searchTerm = `%${criteria.search}%`;
        filters.push(or(
          ilike(tickets.ticketNumber, searchTerm),
          ilike(tickets.drNumber, searchTerm),
          ilike(tickets.messageContent, searchTerm),
          ilike(tickets.technicianNumber, searchTerm)
        ));
      }

      // Apply filters
      if (filters.length > 0) {
        query = query.where(and(...filters));
      }

      // Get total count
      const countQuery = db.select({ count: tickets.id }).from(tickets);
      if (filters.length > 0) {
        countQuery.where(and(...filters));
      }
      const [{ count: total }] = await countQuery;

      // Apply pagination and ordering
      const limit = Math.min(criteria.limit || 50, 100);
      const offset = criteria.offset || 0;

      query = query
        .orderBy(desc(tickets.createdAt))
        .limit(limit)
        .offset(offset);

      const tickets = await query;

      return {
        tickets,
        total,
        hasMore: offset + limit < total
      };
    } catch (error) {
      logger.error('Ticket search failed', {
        criteria,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Update ticket status and metadata
   */
  async updateTicket(ticketId: string, updates: Partial<TicketData>): Promise<TicketData> {
    try {
      logger.info('Updating ticket', { ticketId, updates });

      // Validate updates
      const validUpdates = this.validateTicketUpdates(updates);

      // Update ticket
      const [updatedTicket] = await db
        .update(tickets)
        .set({
          ...validUpdates,
          updatedAt: new Date()
        })
        .where(eq(tickets.id, ticketId))
        .returning();

      if (!updatedTicket) {
        throw new Error('Ticket not found');
      }

      // Log update event
      await securityService.logAuditTrail({
        type: 'ticket_updated',
        data: {
          ticketId,
          updates: validUpdates
        },
        timestamp: Date.now(),
        userId: 'system'
      });

      // Record status change metric
      if (updates.status) {
        await this.recordTicketMetric(ticketId, 'status_change', 1, 'count');
      }

      return updatedTicket;
    } catch (error) {
      logger.error('Failed to update ticket', {
        ticketId,
        updates,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Assign ticket to a user/system
   */
  async assignTicket(ticketId: string, assignedTo: string, assignedBy: string, notes?: string): Promise<TicketData> {
    try {
      logger.info('Assigning ticket', { ticketId, assignedTo, assignedBy });

      // Update ticket assignment
      const [updatedTicket] = await db
        .update(tickets)
        .set({
          assignedTo,
          updatedAt: new Date()
        })
        .where(eq(tickets.id, ticketId))
        .returning();

      if (!updatedTicket) {
        throw new Error('Ticket not found');
      }

      // Record assignment
      await db.insert(ticketAssignments).values({
        ticketId,
        assignedTo,
        assignedBy,
        status: 'assigned',
        notes: notes || null
      });

      // Log assignment event
      await securityService.logAuditTrail({
        type: 'ticket_assigned',
        data: {
          ticketId,
          assignedTo,
          assignedBy,
          notes
        },
        timestamp: Date.now(),
        userId: assignedBy
      });

      // Record assignment metric
      await this.recordTicketMetric(ticketId, 'assigned', 1, 'count');

      return updatedTicket;
    } catch (error) {
      logger.error('Failed to assign ticket', {
        ticketId,
        assignedTo,
        assignedBy,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Get ticket by ID
   */
  async getTicketById(ticketId: string): Promise<TicketData | null> {
    try {
      const ticket = await db
        .select()
        .from(tickets)
        .where(eq(tickets.id, ticketId))
        .limit(1);

      return ticket[0] || null;
    } catch (error) {
      logger.error('Failed to get ticket by ID', {
        ticketId,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Get ticket metrics and statistics
   */
  async getTicketMetrics(dateRange?: { start: Date; end: Date }): Promise<TicketMetrics> {
    try {
      let query = db.select().from(tickets);

      if (dateRange) {
        query = query.where(and(
          gte(tickets.createdAt, dateRange.start),
          lte(tickets.createdAt, dateRange.end)
        ));
      }

      const allTickets = await query;

      const totalTickets = allTickets.length;
      const pendingTickets = allTickets.filter(t => t.status === 'pending').length;
      const processingTickets = allTickets.filter(t => t.status === 'processing').length;
      const completedTickets = allTickets.filter(t => t.status === 'completed').length;
      const failedTickets = allTickets.filter(t => t.status === 'failed').length;

      const ticketsByPriority: Record<TicketData['priority'], number> = {
        low: 0,
        normal: 0,
        high: 0,
        urgent: 0
      };

      const ticketsByStatus: Record<TicketData['status'], number> = {
        pending: 0,
        processing: 0,
        completed: 0,
        failed: 0,
        cancelled: 0
      };

      allTickets.forEach(ticket => {
        ticketsByPriority[ticket.priority]++;
        ticketsByStatus[ticket.status]++;
      });

      // Calculate average processing time
      const completedTicketTimes = allTickets
        .filter(t => t.status === 'completed' && t.completedAt)
        .map(t => t.completedAt!.getTime() - t.createdAt.getTime());

      const averageProcessingTime = completedTicketTimes.length > 0
        ? completedTicketTimes.reduce((a, b) => a + b, 0) / completedTicketTimes.length
        : 0;

      return {
        totalTickets,
        pendingTickets,
        processingTickets,
        completedTickets,
        failedTickets,
        averageProcessingTime,
        ticketsByPriority,
        ticketsByStatus
      };
    } catch (error) {
      logger.error('Failed to get ticket metrics', {
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Process media files associated with a ticket
   */
  private async processTicketMedia(ticketId: string, mediaItems: Array<{
    id: string;
    type: string;
    mimeType: string;
    fileSize: number;
    url?: string;
    caption?: string;
  }>): Promise<void> {
    try {
      for (const media of mediaItems) {
        // Validate media file
        const validation = await securityService.validateMediaFile({
          type: media.mimeType,
          size: media.fileSize
        });

        if (!validation.valid) {
          logger.warn('Media validation failed', {
            ticketId,
            mediaId: media.id,
            error: validation.error
          });
          continue;
        }

        // Scan for security threats
        const scanResult = await securityService.scanMedia({
          id: media.id,
          path: media.url || '',
          size: media.fileSize,
          type: media.mimeType
        });

        if (!scanResult.safeToProcess) {
          logger.warn('Media security scan failed', {
            ticketId,
            mediaId: media.id,
            threats: scanResult.threatsDetected
          });
          continue;
        }

        // Store media metadata
        await db.insert(auditPhotos).values({
          auditResultId: null, // Will be linked when audit is created
          photoUrl: media.url,
          photoType: media.type,
          photoCategory: this.categorizeMedia(media.mimeType),
          fileSize: media.fileSize,
          fileFormat: media.mimeType.split('/')[1],
          uploadTimestamp: new Date(),
          complianceStatus: 'pending',
          complianceNotes: media.caption || null
        });

        logger.info('Media processed for ticket', {
          ticketId,
          mediaId: media.id,
          type: media.type,
          scanTime: scanResult.scanTime
        });
      }
    } catch (error) {
      logger.error('Failed to process ticket media', {
        ticketId,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      throw error;
    }
  }

  /**
   * Record ticket metric
   */
  private async recordTicketMetric(ticketId: string, metricType: string, value: number, unit: string): Promise<void> {
    try {
      await db.insert(ticketMetrics).values({
        ticketId,
        metricType,
        metricValue: value,
        metricUnit: unit
      });
    } catch (error) {
      logger.error('Failed to record ticket metric', {
        ticketId,
        metricType,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
    }
  }

  /**
   * Check for duplicate tickets
   */
  private async checkForDuplicate(drNumber: string, phoneNumber: string): Promise<{
    isDuplicate: boolean;
    originalTicketId?: string;
    originalTicket?: TicketData;
  }> {
    try {
      const recentThreshold = new Date(Date.now() - 24 * 60 * 60 * 1000); // 24 hours ago

      const existingTickets = await db
        .select()
        .from(tickets)
        .where(and(
          eq(tickets.drNumber, drNumber),
          eq(tickets.technicianNumber, phoneNumber),
          // @ts-ignore - Date comparison
          tickets.createdAt > recentThreshold,
          eq(tickets.status, 'pending')
        ))
        .limit(1);

      if (existingTickets[0]) {
        return {
          isDuplicate: true,
          originalTicketId: existingTickets[0].id,
          originalTicket: existingTickets[0]
        };
      }

      return { isDuplicate: false };
    } catch (error) {
      logger.error('Duplicate check failed', {
        drNumber,
        phoneNumber,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      return { isDuplicate: false };
    }
  }

  // Helper methods
  private generateTicketNumber(): string {
    const date = new Date();
    const dateStr = date.toISOString().slice(0, 10).replace(/-/g, '');
    const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    return `${this.TICKET_NUMBER_PREFIX}-${dateStr}-${random}`;
  }

  private generateMessageId(): string {
    return `whatsapp_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  private validatePhoneNumber(phoneNumber: string): boolean {
    // Basic phone number validation for international format
    return /^\+?\d{10,15}$/.test(phoneNumber.replace(/\s+/g, ''));
  }

  private validateDRNumber(drNumber: string): boolean {
    // Validate DR number format
    return /^[A-Za-z]{0,4}\d{5,8}$/.test(drNumber);
  }

  private determinePriority(content: string): 'low' | 'normal' | 'high' | 'urgent' {
    const lowerContent = content.toLowerCase();

    if (lowerContent.includes('urgent') || lowerContent.includes('emergency')) {
      return 'urgent';
    }
    if (lowerContent.includes('high') || lowerContent.includes('critical')) {
      return 'high';
    }
    if (lowerContent.includes('low') || lowerContent.includes('minor')) {
      return 'low';
    }

    return 'normal';
  }

  private categorizeMedia(mimeType: string): string {
    if (mimeType.startsWith('image/')) {
      return 'photo';
    }
    if (mimeType.startsWith('video/')) {
      return 'video';
    }
    if (mimeType.includes('pdf')) {
      return 'document';
    }
    return 'other';
  }

  private validateTicketUpdates(updates: Partial<TicketData>): Partial<TicketData> {
    const validUpdates: Partial<TicketData> = {};

    // Validate status
    if (updates.status) {
      const validStatuses = ['pending', 'processing', 'completed', 'failed', 'cancelled'];
      if (validStatuses.includes(updates.status)) {
        validUpdates.status = updates.status;
      }
    }

    // Validate priority
    if (updates.priority) {
      const validPriorities = ['low', 'normal', 'high', 'urgent'];
      if (validPriorities.includes(updates.priority)) {
        validUpdates.priority = updates.priority;
      }
    }

    // Validate assignedTo
    if (updates.assignedTo) {
      if (updates.assignedTo.trim().length > 0) {
        validUpdates.assignedTo = updates.assignedTo.trim();
      }
    }

    // Validate other fields
    if (updates.technicianName !== undefined) {
      validUpdates.technicianName = updates.technicianName;
    }

    if (updates.completedAt !== undefined) {
      validUpdates.completedAt = updates.completedAt;
    }

    if (updates.externalTicketId !== undefined) {
      validUpdates.externalTicketId = updates.externalTicketId;
    }

    if (updates.syncStatus !== undefined) {
      const validSyncStatuses = ['pending', 'synced', 'failed'];
      if (validSyncStatuses.includes(updates.syncStatus)) {
        validUpdates.syncStatus = updates.syncStatus;
      }
    }

    return validUpdates;
  }

  private async validateMediaData(mediaItems: Array<{
    id: string;
    type: string;
    mimeType: string;
    fileSize: number;
    url?: string;
    caption?: string;
  }>): Promise<{
    valid: boolean;
    errors: string[];
    warnings: string[];
    sanitized?: Array<{
      id: string;
      type: string;
      mimeType: string;
      fileSize: number;
      url?: string;
      caption?: string;
    }>;
  }> {
    const errors: string[] = [];
    const warnings: string[] = [];
    const sanitized: Array<{
      id: string;
      type: string;
      mimeType: string;
      fileSize: number;
      url?: string;
      caption?: string;
    }> = [];

    const MAX_MEDIA_FILES = 10;
    const MAX_TOTAL_SIZE = 50 * 1024 * 1024; // 50MB

    if (mediaItems.length > MAX_MEDIA_FILES) {
      errors.push(`Too many media files. Maximum ${MAX_MEDIA_FILES} allowed`);
    }

    let totalSize = 0;
    const validTypes = ['image', 'document', 'audio', 'video'];

    for (const media of mediaItems) {
      // Validate media type
      if (!validTypes.includes(media.type)) {
        errors.push(`Invalid media type: ${media.type}`);
        continue;
      }

      // Validate file size
      if (media.fileSize <= 0) {
        errors.push(`Invalid file size for media ${media.id}`);
        continue;
      }

      if (media.fileSize > 10 * 1024 * 1024) { // 10MB per file
        warnings.push(`Large file size for media ${media.id}: ${media.fileSize} bytes`);
      }

      totalSize += media.fileSize;

      // Sanitize caption if present
      let sanitizedCaption = undefined;
      if (media.caption) {
        sanitizedCaption = await securityService.sanitizeMessage(media.caption);
        if (sanitizedCaption.length === 0) {
          warnings.push(`Caption sanitized to empty for media ${media.id}`);
        }
      }

      sanitized.push({
        id: media.id,
        type: media.type,
        mimeType: media.mimeType,
        fileSize: media.fileSize,
        url: media.url,
        caption: sanitizedCaption
      });
    }

    if (totalSize > MAX_TOTAL_SIZE) {
      warnings.push(`Total media size ${totalSize} bytes exceeds recommended limit`);
    }

    return {
      valid: errors.length === 0,
      errors,
      warnings: warnings.length > 0 ? warnings : undefined,
      sanitized: sanitized.length > 0 ? sanitized : undefined
    };
  }
}

export const ticketService = new TicketService();