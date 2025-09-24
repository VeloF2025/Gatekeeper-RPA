import { Request, Response, NextFunction } from 'express';
import { logger } from '@/utils/logger';

export interface AppError extends Error {
  statusCode?: number;
  isOperational?: boolean;
  code?: string;
  details?: any;
}

export class ValidationError extends Error implements AppError {
  statusCode = 400;
  isOperational = true;
  code = 'VALIDATION_ERROR';

  constructor(message: string, public details?: any) {
    super(message);
    this.name = 'ValidationError';
  }
}

export class AuthenticationError extends Error implements AppError {
  statusCode = 401;
  isOperational = true;
  code = 'AUTHENTICATION_ERROR';

  constructor(message: string = 'Authentication failed') {
    super(message);
    this.name = 'AuthenticationError';
  }
}

export class AuthorizationError extends Error implements AppError {
  statusCode = 403;
  isOperational = true;
  code = 'AUTHORIZATION_ERROR';

  constructor(message: string = 'Authorization failed') {
    super(message);
    this.name = 'AuthorizationError';
  }
}

export class NotFoundError extends Error implements AppError {
  statusCode = 404;
  isOperational = true;
  code = 'NOT_FOUND_ERROR';

  constructor(message: string = 'Resource not found') {
    super(message);
    this.name = 'NotFoundError';
  }
}

export class RateLimitError extends Error implements AppError {
  statusCode = 429;
  isOperational = true;
  code = 'RATE_LIMIT_ERROR';

  constructor(message: string = 'Rate limit exceeded') {
    super(message);
    this.name = 'RateLimitError';
  }
}

export class DatabaseError extends Error implements AppError {
  statusCode = 500;
  isOperational = true;
  code = 'DATABASE_ERROR';

  constructor(message: string, public details?: any) {
    super(message);
    this.name = 'DatabaseError';
  }
}

export function setupErrorHandling(app: any): void {
  // Handle 404 - Not Found
  app.use('*', (req: Request, res: Response, next: NextFunction) => {
    const error = new NotFoundError(`Route ${req.originalUrl} not found`);
    next(error);
  });

  // Global error handler
  app.use((err: AppError, req: Request, res: Response, next: NextFunction) => {
    const requestId = (req as any).requestId;
    const timestamp = new Date().toISOString();

    // Log error details
    const errorDetails = {
      requestId,
      timestamp,
      method: req.method,
      url: req.url,
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      statusCode: err.statusCode || 500,
      errorCode: err.code,
      message: err.message,
      stack: err.stack,
      details: err.details,
    };

    // Log based on error type
    if (err.statusCode && err.statusCode >= 400 && err.statusCode < 500) {
      logger.warn('Client error', errorDetails);
    } else {
      logger.error('Server error', errorDetails);
    }

    // Send error response
    const response = {
      error: {
        code: err.code || 'INTERNAL_SERVER_ERROR',
        message: getErrorMessage(err),
        timestamp,
        requestId,
      },
    };

    // Add details for development environment
    if (process.env.NODE_ENV === 'development' && err.details) {
      (response.error as any).details = err.details;
    }

    // Add stack trace for development
    if (process.env.NODE_ENV === 'development' && err.stack) {
      (response.error as any).stack = err.stack;
    }

    res.status(err.statusCode || 500).json(response);
  });

  // Handle unhandled promise rejections
  process.on('unhandledRejection', (reason: any, promise: Promise<any>) => {
    logger.error('Unhandled Rejection', {
      reason: reason instanceof Error ? reason.message : reason,
      stack: reason instanceof Error ? reason.stack : undefined,
    });

    // In production, we might want to gracefully shutdown
    if (process.env.NODE_ENV === 'production') {
      logger.warn('Shutting down due to unhandled promise rejection');
      process.exit(1);
    }
  });

  // Handle uncaught exceptions
  process.on('uncaughtException', (error: Error) => {
    logger.error('Uncaught Exception', {
      message: error.message,
      stack: error.stack,
    });

    // In production, we might want to gracefully shutdown
    if (process.env.NODE_ENV === 'production') {
      logger.warn('Shutting down due to uncaught exception');
      process.exit(1);
    }
  });
}

function getErrorMessage(err: AppError): string {
  // Don't expose internal error details to clients
  if (err.isOperational) {
    return err.message;
  }

  if (process.env.NODE_ENV === 'production') {
    return 'An internal server error occurred';
  }

  return err.message || 'Internal Server Error';
}

// Async error handler wrapper
export function asyncHandler(
  fn: (req: Request, res: Response, next: NextFunction) => Promise<any>
) {
  return (req: Request, res: Response, next: NextFunction): void => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
}

// Route-specific error handler
export function handleRouteErrors(
  handler: (req: Request, res: Response, next: NextFunction) => Promise<any>
) {
  return async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      await handler(req, res, next);
    } catch (error) {
      next(error);
    }
  };
}