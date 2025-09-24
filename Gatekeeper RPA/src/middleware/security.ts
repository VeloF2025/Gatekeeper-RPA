import { Request, Response, NextFunction } from 'express';
import helmet from 'helmet';
import { logger } from '@/utils/logger';

export function setupSecurityMiddleware(app: any): void {
  // Prevent clickjacking
  app.use((req: Request, res: Response, next: NextFunction) => {
    res.setHeader('X-Frame-Options', 'DENY');
    next();
  });

  // Prevent MIME type sniffing
  app.use((req: Request, res: Response, next: NextFunction) => {
    res.setHeader('X-Content-Type-Options', 'nosniff');
    next();
  });

  // XSS Protection
  app.use((req: Request, res: Response, next: NextFunction) => {
    res.setHeader('X-XSS-Protection', '1; mode=block');
    next();
  });

  // HSTS
  app.use((req: Request, res: Response, next: NextFunction) => {
    if (req.secure) {
      res.setHeader('Strict-Transport-Security', 'max-age=31536000; includeSubDomains');
    }
    next();
  });

  // Remove sensitive headers
  app.use((req: Request, res: Response, next: NextFunction) => {
    res.removeHeader('X-Powered-By');
    next();
  });

  // Request validation middleware
  app.use((req: Request, res: Response, next: NextFunction) => {
    // Validate request size
    const contentLength = req.get('Content-Length');
    if (contentLength && parseInt(contentLength) > 10 * 1024 * 1024) { // 10MB
      logger.warn('Request too large', {
        ip: req.ip,
        contentLength,
        url: req.url,
      });
      return res.status(413).json({
        error: 'Request Entity Too Large',
        message: 'Request size exceeds 10MB limit',
      });
    }

    next();
  });

  // API key validation for internal services
  app.use('/api/internal', (req: Request, res: Response, next: NextFunction) => {
    const apiKey = req.headers['x-api-key'];

    if (!apiKey || apiKey !== process.env.INTERNAL_API_KEY) {
      logger.warn('Invalid or missing API key for internal endpoint', {
        ip: req.ip,
        url: req.url,
      });
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'Valid API key required',
      });
    }

    next();
  });
}

// JWT verification middleware
export function verifyToken(req: Request, res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Bearer token required',
    });
  }

  const token = authHeader.substring(7); // Remove 'Bearer ' prefix

  try {
    // Token validation logic will be implemented with JWT
    // For now, we'll validate basic format
    if (token.length < 10) {
      throw new Error('Invalid token format');
    }

    // Add decoded token to request object
    (req as any).user = { id: 'temp-user-id' }; // Will be replaced with actual JWT decoding
    next();

  } catch (error) {
    logger.warn('Invalid JWT token', {
      error: error instanceof Error ? error.message : error,
      ip: req.ip,
      url: req.url,
    });

    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Invalid or expired token',
    });
  }
}

// Role-based access control middleware
export function authorizeRoles(roles: string[]) {
  return (req: Request, res: Response, next: NextFunction): void => {
    const user = (req as any).user;

    if (!user || !user.role) {
      return res.status(403).json({
        error: 'Forbidden',
        message: 'User role not specified',
      });
    }

    if (!roles.includes(user.role)) {
      logger.warn('Insufficient permissions', {
        userId: user.id,
        userRole: user.role,
        requiredRoles: roles,
        url: req.url,
      });

      return res.status(403).json({
        error: 'Forbidden',
        message: 'Insufficient permissions to access this resource',
      });
    }

    next();
  };
}

// Input validation middleware
export function validateInput(schema: any) {
  return (req: Request, res: Response, next: NextFunction): void => {
    try {
      const { error } = schema.validate(req.body);

      if (error) {
        logger.warn('Input validation failed', {
          error: error.details[0].message,
          body: req.body,
          url: req.url,
        });

        return res.status(400).json({
          error: 'Bad Request',
          message: 'Validation failed',
          details: error.details[0].message,
        });
      }

      next();
    } catch (validationError) {
      logger.error('Input validation error', {
        error: validationError instanceof Error ? validationError.message : validationError,
        body: req.body,
        url: req.url,
      });

      return res.status(500).json({
        error: 'Internal Server Error',
        message: 'Input validation failed',
      });
    }
  };
}

// Rate limiting for specific endpoints
export function createRateLimit(options: {
  windowMs?: number;
  max?: number;
  skipSuccessfulRequests?: boolean;
}) {
  const expressRateLimit = require('express-rate-limit');

  return expressRateLimit({
    windowMs: options.windowMs || 15 * 60 * 1000, // 15 minutes
    max: options.max || 100,
    skipSuccessfulRequests: options.skipSuccessfulRequests || false,
    message: 'Too many requests from this IP, please try again later.',
    standardHeaders: true,
    legacyHeaders: false,
    handler: (req: Request, res: Response) => {
      logger.warn('Rate limit exceeded', {
        ip: req.ip,
        url: req.url,
      });

      res.status(429).json({
        error: 'Too Many Requests',
        message: 'Rate limit exceeded. Please try again later.',
      });
    },
  });
}

// Request ID middleware for tracking
export function addRequestId(req: Request, res: Response, next: NextFunction): void {
  const requestId = req.headers['x-request-id'] || generateRequestId();
  res.setHeader('X-Request-ID', requestId);
  (req as any).requestId = requestId;
  next();
}

function generateRequestId(): string {
  return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
}