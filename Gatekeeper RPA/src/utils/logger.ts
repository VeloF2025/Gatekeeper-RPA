import winston from 'winston';
import config from '@/config';

const { combine, timestamp, errors, json, printf, colorize } = winston.format;

// Custom log format
const logFormat = printf(({ level, message, timestamp, stack, ...meta }) => {
  let log = `${timestamp} [${level}]: ${message}`;

  if (stack) {
    log += `\n${stack}`;
  }

  if (Object.keys(meta).length > 0) {
    log += ` ${JSON.stringify(meta)}`;
  }

  return log;
});

// Create logger instance
export const logger = winston.createLogger({
  level: config.logging?.level || 'info',
  format: combine(
    timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
    errors({ stack: true }),
    json(),
    logFormat
  ),
  defaultMeta: { service: 'gatekeeper-rpa' },
  transports: [
    // Console transport
    new winston.transports.Console({
      format: combine(
        colorize(),
        timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
        errors({ stack: true }),
        logFormat
      )
    }),

    // File transport (only in production)
    ...(config.nodeEnv === 'production' ? [
      new winston.transports.File({
        filename: config.logging?.file || './logs/app.log',
        maxsize: 5242880, // 5MB
        maxFiles: 5,
        tailable: true
      }),
      new winston.transports.File({
        filename: './logs/error.log',
        level: 'error',
        maxsize: 5242880, // 5MB
        maxFiles: 5,
        tailable: true
      })
    ] : [])
  ]
});

// Request logger middleware
export const requestLogger = winston.createLogger({
  level: 'info',
  format: combine(
    timestamp(),
    json()
  ),
  defaultMeta: { service: 'gatekeeper-rpa-requests' },
  transports: [
    new winston.transports.Console({
      format: combine(
        colorize(),
        timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
        printf(({ timestamp, level, message, ...meta }) => {
          return `${timestamp} [${level}]: ${message} ${JSON.stringify(meta)}`;
        })
      )
    })
  ]
});

// Security logger
export const securityLogger = winston.createLogger({
  level: 'info',
  format: combine(
    timestamp(),
    json()
  ),
  defaultMeta: { service: 'gatekeeper-rpa-security' },
  transports: [
    new winston.transports.File({
      filename: './logs/security.log',
      maxsize: 5242880, // 5MB
      maxFiles: 10,
      tailable: true
    })
  ]
});

// Audit logger
export const auditLogger = winston.createLogger({
  level: 'info',
  format: combine(
    timestamp(),
    json()
  ),
  defaultMeta: { service: 'gatekeeper-rpa-audit' },
  transports: [
    new winston.transports.File({
      filename: './logs/audit.log',
      maxsize: 5242880, // 5MB
      maxFiles: 10,
      tailable: true
    })
  ]
});

// Error logger
export const errorLogger = winston.createLogger({
  level: 'error',
  format: combine(
    timestamp(),
    errors({ stack: true }),
    json()
  ),
  defaultMeta: { service: 'gatekeeper-rpa-errors' },
  transports: [
    new winston.transports.File({
      filename: './logs/errors.log',
      maxsize: 5242880, // 5MB
      maxFiles: 10,
      tailable: true
    })
  ]
});

export default logger;