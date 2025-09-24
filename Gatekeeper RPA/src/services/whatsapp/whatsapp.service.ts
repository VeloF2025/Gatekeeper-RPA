import axios, { AxiosInstance } from 'axios';
import { Queue } from 'bull';
import { v4 as uuidv4 } from 'uuid';
import { WhatsAppMessage, MessageStatus, TicketStatus } from '@/types';
import { InputSanitizer } from '@/utils/validation';
import { logger, securityLogger } from '@/utils/logger';
import config from '@/config';

export class WhatsAppService {
  private api: AxiosInstance;
  private queue: Queue;
  private rateLimiter: Map<string, { count: number; resetTime: number }> = new Map();

  constructor(queue: Queue) {
    this.queue = queue;

    // Configure API client with Zero Trust principles
    this.api = axios.create({
      baseURL: config.whatsapp.baseUrl,
      headers: {
        'Authorization': `Bearer ${config.whatsapp.apiKey}`,
        'Content-Type': 'application/json',
        'User-Agent': 'Gatekeeper-RPA/1.0'
      },
      timeout: 10000,
      validateStatus: (status) => status < 500
    });

    // Setup request interceptor for security
    this.api.interceptors.request.use((config) => {
      // Validate all outbound requests
      if (!config.headers?.Authorization) {
        throw new Error('Missing authorization header');
      }

      // Log request for audit trail
      securityLogger.info('WhatsApp API Request', {
        url: config.url,
        method: config.method?.toUpperCase(),
        timestamp: new Date().toISOString()
      });

      return config;
    });

    // Setup response interceptor for security
    this.api.interceptors.response.use(
      (response) => {
        // Validate response structure
        if (!response.data || typeof response.data !== 'object') {
          throw new Error('Invalid response format');
        }

        // Log successful response
        securityLogger.info('WhatsApp API Response', {
          url: response.config.url,
          status: response.status,
          timestamp: new Date().toISOString()
        });

        return response;
      },
      (error) => {
        // Log security-relevant errors
        securityLogger.error('WhatsApp API Error', {
          url: error.config?.url,
          status: error.response?.status,
          message: error.message,
          timestamp: new Date().toISOString()
        });

        throw error;
      }
    );
  }

  /**
   * Extract DR number from message using strict validation
   */
  extractDRNumber(message: string): string | null {
    if (!message || typeof message !== 'string') {
      return null;
    }

    // Sanitize input first
    const sanitized = InputSanitizer.sanitizeString(message);
    return InputSanitizer.extractDRNumber(sanitized);
  }

  /**
   * Validate phone number with Zero Trust principles
   */
  private validatePhoneNumber(phoneNumber: string): boolean {
    if (!phoneNumber || typeof phoneNumber !== 'string') {
      return false;
    }

    const sanitized = InputSanitizer.validatePhoneNumber(phoneNumber);
    if (!sanitized) {
      securityLogger.warn('Invalid phone number format', { phoneNumber });
      return false;
    }

    return true;
  }

  /**
   * Apply rate limiting with Zero Trust principles
   */
  private async applyRateLimit(phoneNumber: string): Promise<boolean> {
    const now = Date.now();
    const key = `whatsapp:${phoneNumber}`;

    // Get current rate limit data
    let limitData = this.rateLimiter.get(key);

    if (!limitData || now > limitData.resetTime) {
      // Reset rate limit
      limitData = {
        count: 1,
        resetTime: now + config.rateLimit.duration
      };
      this.rateLimiter.set(key, limitData);
      return true;
    }

    if (limitData.count >= config.rateLimit.points) {
      securityLogger.warn('Rate limit exceeded', { phoneNumber, count: limitData.count });
      return false;
    }

    limitData.count++;
    return true;
  }

  /**
   * Create audit ticket with comprehensive validation
   */
  async createAuditTicket(data: {
    drNumber: string;
    technicianNumber: string;
    messageContent: string;
    messageTimestamp: Date;
  }): Promise<{ ticketId: string; ticketNumber: string }> {
    // Validate all required fields
    if (!data.drNumber || !data.technicianNumber || !data.messageContent) {
      throw new Error('Missing required fields for ticket creation');
    }

    // Validate phone number
    if (!this.validatePhoneNumber(data.technicianNumber)) {
      throw new Error('Invalid phone number format');
    }

    // Validate DR number
    if (!this.extractDRNumber(data.drNumber)) {
      throw new Error('Invalid DR number format');
    }

    // Apply rate limiting
    if (!(await this.applyRateLimit(data.technicianNumber))) {
      throw new Error('Rate limit exceeded. Please try again later.');
    }

    // Generate unique identifiers
    const ticketId = uuidv4();
    const ticketNumber = this.generateTicketNumber();

    // Create ticket data with sanitized inputs
    const ticketData = {
      id: ticketId,
      ticketNumber,
      drNumber: data.drNumber.toUpperCase(),
      technicianNumber: InputSanitizer.validatePhoneNumber(data.technicianNumber)!,
      technicianName: '', // Will be populated from user data
      messageContent: InputSanitizer.sanitizeString(data.messageContent),
      messageTimestamp: data.messageTimestamp,
      status: TicketStatus.PENDING,
      priority: 'normal',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    // Queue audit task
    await this.queue.add('process-audit', {
      ticketId,
      priority: 'normal',
      data: ticketData
    }, {
      attempts: 3,
      backoff: {
        type: 'exponential',
        delay: 2000
      },
      removeOnComplete: 10,
      removeOnFail: 5
    });

    logger.info('Audit ticket created', { ticketId, ticketNumber, drNumber: data.drNumber });

    return { ticketId, ticketNumber };
  }

  /**
   * Handle incoming WhatsApp message with Zero Trust validation
   */
  async handleMessage(message: {
    from: string;
    body: string;
    timestamp: Date | string;
    id?: string;
  }): Promise<{ success: boolean; ticketId?: string; error?: string }> {
    try {
      // Validate message structure
      if (!message.from || !message.body || !message.timestamp) {
        throw new Error('Invalid message structure');
      }

      // Sanitize and validate inputs
      const sanitizedFrom = InputSanitizer.validatePhoneNumber(message.from);
      const sanitizedBody = InputSanitizer.sanitizeString(message.body);

      if (!sanitizedFrom || !sanitizedBody) {
        throw new Error('Invalid message content');
      }

      // Extract DR number
      const drNumber = this.extractDRNumber(sanitizedBody);

      if (!drNumber) {
        // Send help message for invalid DR format
        await this.sendHelpMessage(sanitizedFrom);
        return {
          success: false,
          error: 'Invalid DR number format. Please use format: DR1234567'
        };
      }

      // Create audit ticket
      const ticket = await this.createAuditTicket({
        drNumber,
        technicianNumber: sanitizedFrom,
        messageContent: sanitizedBody,
        messageTimestamp: new Date(message.timestamp)
      });

      // Send confirmation message
      await this.sendConfirmationMessage(sanitizedFrom, drNumber, ticket.ticketNumber);

      logger.info('WhatsApp message processed successfully', {
        messageId: message.id,
        from: sanitizedFrom,
        drNumber,
        ticketId: ticket.ticketId
      });

      return {
        success: true,
        ticketId: ticket.ticketId
      };

    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';

      securityLogger.error('WhatsApp message processing failed', {
        messageId: message.id,
        from: message.from,
        error: errorMessage,
        timestamp: new Date().toISOString()
      });

      return {
        success: false,
        error: errorMessage
      };
    }
  }

  /**
   * Send confirmation message with Zero Trust validation
   */
  private async sendConfirmationMessage(
    to: string,
    drNumber: string,
    ticketNumber: string
  ): Promise<void> {
    const message = `✅ Audit request received!\n\nDR Number: ${drNumber}\nTicket: ${ticketNumber}\n\nWe'll process your audit and send results shortly.`;

    await this.sendMessage({
      to,
      body: message,
      ticketId: ticketNumber
    });
  }

  /**
   * Send help message
   */
  private async sendHelpMessage(to: string): Promise<void> {
    const message = `🤖 *Gatekeeper RPA Bot*\n\nTo request an audit, send a message with the DR number in this format:\n\nDR1234567\n\nExample: DR1854443\n\nThe bot will automatically process your audit request.`;

    await this.sendMessage({
      to,
      body: message
    });
  }

  /**
   * Send audit completion message
   */
  async sendAuditCompletionMessage(
    to: string,
    drNumber: string,
    ticketNumber: string,
    result: {
      complianceScore: number;
      status: string;
      missingPhotos?: string[];
    }
  ): Promise<void> {
    let message = `✅ **Audit Complete!**\n\nDR Number: ${drNumber}\nTicket: ${ticketNumber}\n\n`;

    if (result.complianceScore >= 90) {
      message += `🎉 **Excellent!** Score: ${result.complianceScore}%\n\n`;
    } else if (result.complianceScore >= 70) {
      message += `✅ **Good!** Score: ${result.complianceScore}%\n\n`;
    } else {
      message += `⚠️ **Needs Attention** Score: ${result.complianceScore}%\n\n`;
    }

    message += `Status: ${result.status}\n`;

    if (result.missingPhotos && result.missingPhotos.length > 0) {
      message += `\nMissing Photos: ${result.missingPhotos.join(', ')}\n`;
    }

    message += `\nThank you for using Gatekeeper RPA! 🚀`;

    await this.sendMessage({
      to,
      body: message,
      ticketId: ticketNumber
    });
  }

  /**
   * Send message via WhatsApp API with Zero Trust principles
   */
  async sendMessage(data: {
    to: string;
    body: string;
    ticketId?: string;
    type?: 'text' | 'image' | 'document';
  }): Promise<{ success: boolean; messageId?: string; error?: string }> {
    try {
      // Validate recipient
      if (!this.validatePhoneNumber(data.to)) {
        throw new Error('Invalid recipient phone number');
      }

      // Validate message content
      if (!data.body || data.body.trim().length === 0) {
        throw new Error('Message content cannot be empty');
      }

      // Sanitize message content
      const sanitizedBody = InputSanitizer.sanitizeString(data.body);

      // Prepare API request
      const requestData = {
        to: data.to,
        body: sanitizedBody,
        type: data.type || 'text',
        timestamp: new Date().toISOString()
      };

      // Add metadata if available
      if (data.ticketId) {
        Object.assign(requestData, { ticketId: data.ticketId });
      }

      // Send message via WhatsApp API
      const response = await this.api.post('/messages', requestData);

      if (!response.data || !response.data.success) {
        throw new Error('Failed to send message');
      }

      logger.info('WhatsApp message sent successfully', {
        to: data.to,
        messageId: response.data.messageId,
        ticketId: data.ticketId,
        timestamp: new Date().toISOString()
      });

      return {
        success: true,
        messageId: response.data.messageId
      };

    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';

      securityLogger.error('WhatsApp message send failed', {
        to: data.to,
        ticketId: data.ticketId,
        error: errorMessage,
        timestamp: new Date().toISOString()
      });

      return {
        success: false,
        error: errorMessage
      };
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
   * Verify webhook signature for security
   */
  verifyWebhookSignature(payload: string, signature: string): boolean {
    if (!config.whatsapp.webhookSecret) {
      return false;
    }

    // Implement HMAC verification
    const crypto = require('crypto');
    const hmac = crypto.createHmac('sha256', config.whatsapp.webhookSecret);
    const calculatedSignature = hmac.update(payload).digest('hex');

    return crypto.timingSafeEqual(
      Buffer.from(signature, 'hex'),
      Buffer.from(calculatedSignature, 'hex')
    );
  }

  /**
   * Get rate limit status for a phone number
   */
  getRateLimitStatus(phoneNumber: string): {
    remaining: number;
    resetTime: number;
    isLimited: boolean;
  } {
    const key = `whatsapp:${phoneNumber}`;
    const limitData = this.rateLimiter.get(key);

    if (!limitData) {
      return {
        remaining: config.rateLimit.points,
        resetTime: 0,
        isLimited: false
      };
    }

    const now = Date.now();
    const remaining = Math.max(0, config.rateLimit.points - limitData.count);
    const isLimited = now < limitData.resetTime && remaining === 0;

    return {
      remaining,
      resetTime: limitData.resetTime,
      isLimited
    };
  }
}