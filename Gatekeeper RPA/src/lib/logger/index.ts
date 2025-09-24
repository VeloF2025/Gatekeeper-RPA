/**
 * Logger Service for Gatekeeper RPA System
 *
 * Secure logging service following Zero Trust principles.
 * Replaces console.log statements with proper logging infrastructure.
 *
 * Created with security validation and proper error handling.
 */

// Log level interface
export type LogLevel = 'error' | 'warn' | 'info' | 'debug';

// Log entry interface
export interface LogEntry {
  timestamp: Date;
  level: LogLevel;
  message: string;
  data?: any;
  component?: string;
  userId?: string;
  requestId?: string;
}

// Logger configuration interface
interface LoggerConfig {
  level: LogLevel;
  enableConsole: boolean;
  enableRemote: boolean;
  remoteEndpoint?: string;
  maxEntries: number;
  sanitizeData: boolean;
}

/**
 * Logger class implementing secure logging practices
 */
export class Logger {
  private config: LoggerConfig;
  private entries: LogEntry[] = [];
  private flushInterval: NodeJS.Timeout | null = null;

  constructor(config: Partial<LoggerConfig> = {}) {
    this.config = {
      level: 'info',
      enableConsole: process.env.NODE_ENV === 'development',
      enableRemote: process.env.NODE_ENV === 'production',
      maxEntries: 1000,
      sanitizeData: true,
      ...config,
    };

    this.startFlushInterval();
  }

  /**
   * Log an error message
   */
  error(message: string, error?: any, component?: string): void {
    this.log('error', message, error, component);
  }

  /**
   * Log a warning message
   */
  warn(message: string, data?: any, component?: string): void {
    this.log('warn', message, data, component);
  }

  /**
   * Log an info message
   */
  info(message: string, data?: any, component?: string): void {
    this.log('info', message, data, component);
  }

  /**
   * Log a debug message
   */
  debug(message: string, data?: any, component?: string): void {
    this.log('debug', message, data, component);
  }

  /**
   * Internal logging method
   */
  private log(level: LogLevel, message: string, data?: any, component?: string): void {
    // Check if we should log at this level
    if (!this.shouldLog(level)) {
      return;
    }

    const entry: LogEntry = {
      timestamp: new Date(),
      level,
      message,
      data: this.config.sanitizeData ? this.sanitizeData(data) : data,
      component,
      userId: this.getCurrentUserId(),
      requestId: this.getRequestId(),
    };

    // Add to internal buffer
    this.entries.push(entry);

    // Trim entries if we exceed max
    if (this.entries.length > this.config.maxEntries) {
      this.entries = this.entries.slice(-this.config.maxEntries);
    }

    // Log to console if enabled
    if (this.config.enableConsole) {
      this.logToConsole(entry);
    }

    // Send to remote logging service if enabled
    if (this.config.enableRemote) {
      this.sendToRemote(entry).catch(error => {
        // Don't use this.log here to avoid infinite recursion
        console.error('Failed to send log to remote service:', error);
      });
    }
  }

  /**
   * Check if we should log at this level
   */
  private shouldLog(level: LogLevel): boolean {
    const levels: LogLevel[] = ['debug', 'info', 'warn', 'error'];
    const currentLevelIndex = levels.indexOf(this.config.level);
    const messageLevelIndex = levels.indexOf(level);

    return messageLevelIndex >= currentLevelIndex;
  }

  /**
   * Log to console with proper formatting
   */
  private logToConsole(entry: LogEntry): void {
    const timestamp = entry.timestamp.toISOString();
    const prefix = `[${timestamp}] [${entry.level.toUpperCase()}]`;
    const componentSuffix = entry.component ? ` [${entry.component}]` : '';
    const message = `${prefix}${componentSuffix} ${entry.message}`;

    switch (entry.level) {
      case 'error':
        console.error(message, entry.data || '');
        break;
      case 'warn':
        console.warn(message, entry.data || '');
        break;
      case 'info':
        console.info(message, entry.data || '');
        break;
      case 'debug':
        console.debug(message, entry.data || '');
        break;
    }
  }

  /**
   * Send log entry to remote logging service
   */
  private async sendToRemote(entry: LogEntry): Promise<void> {
    if (!this.config.remoteEndpoint) {
      return;
    }

    // In a real implementation, this would send to your logging service
    // For now, we'll simulate it
    await new Promise(resolve => setTimeout(resolve, 10));

    // TODO: Implement actual remote logging
    // await fetch(this.config.remoteEndpoint, {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(entry),
    // });
  }

  /**
   * Sanitize sensitive data from logs
   */
  private sanitizeData(data: any): any {
    if (!data || typeof data !== 'object') {
      return data;
    }

    const sensitiveKeys = [
      'password', 'token', 'secret', 'key', 'auth', 'credential',
      'ssn', 'social_security', 'credit_card', 'card_number'
    ];

    const sanitized = { ...data };

    for (const key in sanitized) {
      if (sensitiveKeys.some(sensitive => key.toLowerCase().includes(sensitive))) {
        sanitized[key] = '[REDACTED]';
      } else if (typeof sanitized[key] === 'object' && sanitized[key] !== null) {
        sanitized[key] = this.sanitizeData(sanitized[key]);
      }
    }

    return sanitized;
  }

  /**
   * Get current user ID from auth context
   */
  private getCurrentUserId(): string | undefined {
    // In a real implementation, this would get the current user ID from auth context
    // For now, we'll return undefined
    return undefined;
  }

  /**
   * Get request ID for tracking
   */
  private getRequestId(): string | undefined {
    // In a real implementation, this would get the request ID from headers or context
    // For now, we'll return undefined
    return undefined;
  }

  /**
   * Start periodic flush interval
   */
  private startFlushInterval(): void {
    this.flushInterval = setInterval(() => {
      this.flush();
    }, 30000); // Flush every 30 seconds
  }

  /**
   * Flush buffered logs
   */
  private async flush(): Promise<void> {
    if (this.entries.length === 0) {
      return;
    }

    const entriesToFlush = [...this.entries];
    this.entries = [];

    if (this.config.enableRemote && this.config.remoteEndpoint) {
      try {
        // In a real implementation, this would send buffered logs
        // await this.sendBatchToRemote(entriesToFlush);
      } catch (error) {
        // Re-add entries to buffer if flush fails
        this.entries.unshift(...entriesToFlush);
        console.error('Failed to flush logs:', error);
      }
    }
  }

  /**
   * Get all log entries
   */
  getEntries(): LogEntry[] {
    return [...this.entries];
  }

  /**
   * Clear all log entries
   */
  clearEntries(): void {
    this.entries = [];
  }

  /**
   * Destroy logger and cleanup
   */
  destroy(): void {
    if (this.flushInterval) {
      clearInterval(this.flushInterval);
      this.flushInterval = null;
    }

    // Flush remaining entries
    this.flush().catch(error => {
      console.error('Failed to flush logs during destroy:', error);
    });
  }
}

// Create default logger instance
const logger = new Logger({
  level: (process.env.LOG_LEVEL as LogLevel) || 'info',
  enableConsole: process.env.NODE_ENV === 'development',
  enableRemote: process.env.NODE_ENV === 'production',
  remoteEndpoint: process.env.LOGGING_ENDPOINT,
});

// Export logger instance as default
export default logger;

// Logger class is already exported above

// Export convenience functions for direct import
export const error = (message: string, error?: any, component?: string) => logger.error(message, error, component);
export const warn = (message: string, data?: any, component?: string) => logger.warn(message, data, component);
export const info = (message: string, data?: any, component?: string) => logger.info(message, data, component);
export const debug = (message: string, data?: any, component?: string) => logger.debug(message, data, component);

// Export logger instance for named import
export const log = logger;