/**
 * Gatekeeper RPA Logging System
 * Structured logging with security and performance monitoring
 */

import winston from 'winston';
import { createHash } from 'crypto';

// Log levels with security considerations
const logLevels = {
  error: 0,
  warn: 1,
  info: 2,
  http: 3,
  debug: 4,
  security: 5,
  audit: 6
};

// Security-sensitive fields to redact
const sensitiveFields = [
  'password', 'token', 'secret', 'key', 'creditCard',
  'ssn', 'socialSecurity', 'bankAccount', 'routingNumber',
  'authorization', 'bearer', 'api_key', 'access_token',
  'refresh_token', 'session_id'
];

// Log formats
const formats = {
  // JSON format for structured logging
  json: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.metadata(),
    winston.format.json()
  ),

  // Console format for development
  console: winston.format.combine(
    winston.format.colorize(),
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
    winston.format.printf(({ timestamp, level, message, metadata, stack }) => {
      let log = `${timestamp} [${level}]: ${message}`;

      if (stack) {
        log += `\n${stack}`;
      }

      if (Object.keys(metadata || {}).length > 0) {
        log += ` ${JSON.stringify(metadata)}`;
      }

      return log;
    })
  ),

  // Security log format
  security: winston.format.combine(
    winston.format.timestamp(),
    winston.format.metadata(),
    winston.format.json()
  )
};

// Create logger instance
export const logger = winston.createLogger({
  levels: logLevels,
  level: process.env.LOG_LEVEL || 'info',
  format: formats.json,
  defaultMeta: {
    service: 'gatekeeper-rpa',
    environment: process.env.NODE_ENV || 'development',
    version: process.env.npm_package_version || '1.0.0'
  },
  transports: [
    // Error logs
    new winston.transports.File({
      filename: 'logs/error.log',
      level: 'error',
      maxsize: 5242880, // 5MB
      maxFiles: 5,
      format: formats.json
    }),

    // Combined logs
    new winston.transports.File({
      filename: 'logs/combined.log',
      maxsize: 5242880, // 5MB
      maxFiles: 5,
      format: formats.json
    }),

    // Security logs
    new winston.transports.File({
      filename: 'logs/security.log',
      level: 'security',
      maxsize: 5242880, // 5MB
      maxFiles: 10,
      format: formats.security,
      tailable: true
    }),

    // Audit logs
    new winston.transports.File({
      filename: 'logs/audit.log',
      level: 'audit',
      maxsize: 5242880, // 5MB
      maxFiles: 20,
      format: formats.security,
      tailable: true
    })
  ]
});

// Add console transport for development
if (process.env.NODE_ENV !== 'production') {
  logger.add(new winston.transports.Console({
    format: formats.console
  }));
}

// Security logging wrapper
export class SecurityLogger {
  private static redactSensitiveData(data: any): any {
    if (typeof data !== 'object' || data === null) return data;

    const sanitized = Array.isArray(data) ? [] : {};

    for (const [key, value] of Object.entries(data)) {
      if (sensitiveFields.some(field => key.toLowerCase().includes(field))) {
        sanitized[key] = '***REDACTED***';
      } else if (typeof value === 'object') {
        sanitized[key] = this.redactSensitiveData(value);
      } else {
        sanitized[key] = value;
      }
    }

    return sanitized;
  }

  static logSecurityEvent(eventType: string, details: any, severity: string = 'medium') {
    const event = {
      eventType,
      severity,
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId(),
      details: this.redactSensitiveData(details),
      environment: process.env.NODE_ENV || 'development'
    };

    logger.security('Security Event', event);
  }

  static logAuthAttempt(username: string, success: boolean, ipAddress: string, userAgent: string, reason?: string) {
    const authEvent = {
      username,
      success,
      ipAddress,
      userAgent,
      reason,
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId()
    };

    logger.audit('Authentication Attempt', authEvent);
  }

  static logApiRequest(req: any, res: any, duration: number) {
    const apiLog = {
      method: req.method,
      url: req.originalUrl,
      statusCode: res.statusCode,
      duration,
      userAgent: req.get('User-Agent'),
      ipAddress: req.ip,
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId()
    };

    logger.http('API Request', apiLog);
  }

  static logDataAccess(userId: string, action: string, resource: string, details: any = {}) {
    const accessLog = {
      userId,
      action,
      resource,
      details: this.redactSensitiveData(details),
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId()
    };

    logger.audit('Data Access', accessLog);
  }

  static logSecurityThreat(threatType: string, details: any, severity: string = 'high') {
    const threat = {
      threatType,
      severity,
      details: this.redactSensitiveData(details),
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId()
    };

    logger.security('Security Threat', threat);
  }
}

// Performance logging wrapper
export class PerformanceLogger {
  static startTimer(label: string) {
    return {
      label,
      startTime: process.hrtime(),
      end: (additionalData: any = {}) => {
        const [seconds, nanoseconds] = process.hrtime(this.startTime);
        const duration = seconds * 1000 + nanoseconds / 1000000;

        logger.info('Performance Metric', {
          metric: label,
          duration,
          unit: 'ms',
          ...additionalData
        });

        return duration;
      }
    };
  }

  static logMemoryUsage(context: string = '') {
    const memoryUsage = process.memoryUsage();

    logger.info('Memory Usage', {
      context,
      rss: `${Math.round(memoryUsage.rss / 1024 / 1024)}MB`,
      heapTotal: `${Math.round(memoryUsage.heapTotal / 1024 / 1024)}MB`,
      heapUsed: `${Math.round(memoryUsage.heapUsed / 1024 / 1024)}MB`,
      external: `${Math.round(memoryUsage.external / 1024 / 1024)}MB`
    });
  }

  static logDatabaseQuery(operation: string, table: string, duration: number, success: boolean) {
    logger.info('Database Query', {
      operation,
      table,
      duration,
      success,
      timestamp: new Date().toISOString()
    });
  }
}

// Audit logging wrapper
export class AuditLogger {
  static logUserAction(userId: string, action: string, resource: string, details: any = {}) {
    const auditEntry = {
      userId,
      action,
      resource,
      details,
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId(),
      environment: process.env.NODE_ENV || 'development'
    };

    logger.audit('User Action', auditEntry);
  }

  static logSystemChange(component: string, change: string, userId: string, details: any = {}) {
    const systemLog = {
      component,
      change,
      userId,
      details,
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId()
    };

    logger.audit('System Change', systemLog);
  }

  static logComplianceEvent(eventType: string, details: any) {
    const complianceEvent = {
      eventType,
      details,
      timestamp: new Date().toISOString(),
      correlationId: generateCorrelationId()
    };

    logger.audit('Compliance Event', complianceEvent);
  }
}

// Client-side logger for React components (maintained for compatibility)
export const log = {
  info: (message: string, data?: any, context?: string) => {
    if (process.env.NODE_ENV === 'development') {
      console.log(`[${context || 'App'}] ${message}`, data || '');
    }
  },
  error: (message: string, error?: any, context?: string) => {
    if (process.env.NODE_ENV === 'development') {
      console.error(`[${context || 'App'}] ${message}`, error || '');
    }
  },
  warn: (message: string, data?: any, context?: string) => {
    if (process.env.NODE_ENV === 'development') {
      console.warn(`[${context || 'App'}] ${message}`, data || '');
    }
  },
  debug: (message: string, data?: any, context?: string) => {
    if (process.env.NODE_ENV === 'development') {
      console.debug(`[${context || 'App'}] ${message}`, data || '');
    }
  }
};

// Utility functions
function generateCorrelationId(): string {
  return createHash('sha256')
    .update(Date.now().toString() + Math.random().toString())
    .digest('hex')
    .substring(0, 16);
}

// Express middleware for request logging
export const requestLogger = (req: any, res: any, next: any) => {
  const startTime = Date.now();
  const correlationId = generateCorrelationId();

  // Add correlation ID to request
  req.correlationId = correlationId;
  res.header('X-Correlation-ID', correlationId);

  // Log request
  logger.info('Request started', {
    method: req.method,
    url: req.originalUrl,
    correlationId,
    userAgent: req.get('User-Agent'),
    ip: req.ip
  });

  // Override end method to log response
  const originalEnd = res.end;
  res.end = function(chunk: any, encoding: any) {
    const duration = Date.now() - startTime;

    logger.info('Request completed', {
      method: req.method,
      url: req.originalUrl,
      statusCode: res.statusCode,
      duration,
      correlationId
    });

    originalEnd.call(this, chunk, encoding);
  };

  next();
};

// Security middleware
export const securityLogger = (req: any, res: any, next: any) => {
  // Log security-related headers
  const securityHeaders = {
    'x-forwarded-for': req.headers['x-forwarded-for'],
    'x-real-ip': req.headers['x-real-ip'],
    'user-agent': req.headers['user-agent'],
    'authorization': req.headers['authorization'] ? 'Bearer ***REDACTED***' : undefined
  };

  logger.debug('Security Headers', {
    correlationId: req.correlationId,
    headers: securityHeaders
  });

  next();
};

// Export default logger
export default logger;
export const errorLogger = logger;