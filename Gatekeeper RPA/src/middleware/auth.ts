import { NextRequest, NextResponse } from 'next/server';
import { authService } from '@/services/auth/auth.service';
import { logger, errorLogger } from '@/utils/logger';
import { jwtSecurity } from '@/lib/security/jwt';
import { rbac } from '@/lib/security/rbac';
import { inputValidator } from '@/lib/security/input-validation';

/**
 * Enhanced JWT Authentication Middleware
 */
export async function authMiddleware(request: NextRequest): Promise<NextResponse | { userId: string; email: string; role: string; permissions: string[] }> {
  try {
    // Get authorization header
    const authHeader = request.headers.get('authorization');

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      logger.warn('Missing or invalid authorization header', {
        ip: request.headers.get('x-forwarded-for'),
        userAgent: request.headers.get('user-agent'),
        url: request.url,
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Missing or invalid authorization header',
          message: 'Authorization header with Bearer token is required',
          code: 'AUTH_001'
        },
        { status: 401 }
      );
    }

    const token = authHeader.substring(7); // Remove 'Bearer ' prefix

    // Security check for suspicious tokens
    const securityCheck = inputValidator.securityCheck(token);
    if (securityCheck.isSuspicious) {
      logger.warn('Suspicious JWT token detected', {
        threats: securityCheck.threats,
        ip: request.headers.get('x-forwarded-for'),
        userAgent: request.headers.get('user-agent'),
        url: request.url,
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Invalid token format',
          message: 'Token contains suspicious content',
          code: 'AUTH_014'
        },
        { status: 401 }
      );
    }

    // Verify token using enhanced JWT security
    const payload = jwtSecurity.verifyAccessToken(token);

    if (!payload) {
      logger.warn('Invalid JWT token', {
        ip: request.headers.get('x-forwarded-for'),
        userAgent: request.headers.get('user-agent'),
        url: request.url,
        tokenPreview: token.substring(0, 20) + '...',
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Invalid or expired token',
          message: 'Please login again to continue',
          code: 'AUTH_002'
        },
        { status: 401 }
      );
    }

    // Get user details from RBAC system
    const user = rbac.getUsers().find(u => u.id === payload.userId);

    if (!user || !user.active) {
      logger.warn('Inactive or non-existent user attempted access', {
        userId: payload.userId,
        ip: request.headers.get('x-forwarded-for'),
        url: request.url,
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Account inactive',
          message: 'Your account has been disabled or does not exist',
          code: 'AUTH_015'
        },
        { status: 401 }
      );
    }

    // Get user permissions
    const permissions = rbac.getUserPermissions(payload.userId);

    logger.debug('User authenticated successfully', {
      userId: payload.userId,
      email: payload.email,
      role: payload.role,
      permissions: permissions.length,
      sessionId: payload.sessionId,
      url: request.url,
    });

    // Return complete user information for use in route handlers
    return {
      userId: payload.userId,
      email: payload.email,
      role: payload.role,
      permissions
    };
  } catch (error) {
    errorLogger.error('Authentication middleware error', {
      error: error instanceof Error ? error.message : error,
      url: request.url,
      userAgent: request.headers.get('user-agent'),
      ip: request.headers.get('x-forwarded-for') || 'unknown'
    });

    return NextResponse.json(
      {
        success: false,
        error: 'Authentication failed',
        message: 'An error occurred during authentication',
        code: 'AUTH_016'
      },
      { status: 500 }
    );
  }
}

/**
 * Enhanced Role-based Authorization Middleware
 */
export function requireRole(allowedRoles: string[]) {
  return async (request: NextRequest): Promise<NextResponse | { userId: string; userRole: string; permissions: string[] }> => {
    try {
      // First authenticate the user
      const authResult = await authMiddleware(request);

      if (authResult instanceof NextResponse) {
        return authResult; // Return error response
      }

      // Use enhanced RBAC system for role checking
      const hasRequiredRole = allowedRoles.some(role => role === authResult.role);

      if (!hasRequiredRole) {
        logger.warn('Authorization failed - insufficient role', {
          userId: authResult.userId,
          userRole: authResult.role,
          requiredRoles: allowedRoles,
          userPermissions: authResult.permissions,
          url: request.url,
          method: request.method,
        });

        return NextResponse.json(
          {
            success: false,
            error: 'Insufficient permissions',
            message: `Required role: ${allowedRoles.join(' or ')}, your role: ${authResult.role}`,
            code: 'AUTH_004'
          },
          { status: 403 }
        );
      }

      logger.debug('Role authorization successful', {
        userId: authResult.userId,
        userRole: authResult.role,
        requiredRoles: allowedRoles,
        url: request.url,
      });

      return {
        userId: authResult.userId,
        userRole: authResult.role,
        permissions: authResult.permissions
      };
    } catch (error) {
      errorLogger.error('Role authorization middleware error', {
        error: error instanceof Error ? error.message : error,
        url: request.url,
        allowedRoles
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Authorization failed',
          message: 'An error occurred during authorization',
          code: 'AUTH_017'
        },
        { status: 500 }
      );
    }
  };
}

/**
 * Permission-based Authorization Middleware
 */
export function requirePermission(permission: string) {
  return async (request: NextRequest): Promise<NextResponse | { userId: string; permissions: string[] }> => {
    try {
      // First authenticate the user
      const authResult = await authMiddleware(request);

      if (authResult instanceof NextResponse) {
        return authResult; // Return error response
      }

      // Check if user has the required permission using RBAC
      const hasPermission = rbac.hasPermission(authResult.userId, permission);

      if (!hasPermission) {
        logger.warn('Permission check failed', {
          userId: authResult.userId,
          userRole: authResult.role,
          requiredPermission: permission,
          userPermissions: authResult.permissions,
          url: request.url,
          method: request.method,
        });

        return NextResponse.json(
          {
            success: false,
            error: 'Insufficient permissions',
            message: `Required permission: ${permission}`,
            code: 'AUTH_006'
          },
          { status: 403 }
        );
      }

      logger.debug('Permission authorization successful', {
        userId: authResult.userId,
        permission,
        url: request.url,
      });

      return {
        userId: authResult.userId,
        permissions: authResult.permissions
      };
    } catch (error) {
      errorLogger.error('Permission authorization middleware error', {
        error: error instanceof Error ? error.message : error,
        url: request.url,
        requiredPermission: permission
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Authorization failed',
          message: 'An error occurred during authorization',
          code: 'AUTH_018'
        },
        { status: 500 }
      );
    }
  };
}

/**
 * Rate Limiting Middleware
 */
interface RateLimitData {
  count: number;
  resetTime: number;
}

const rateLimitStore = new Map<string, RateLimitData>();

export function rateLimit(maxRequests: number, windowMs: number) {
  return async (request: NextRequest): Promise<NextResponse | null> => {
    try {
      // Get client identifier (IP address or user ID if authenticated)
      const clientId = request.headers.get('x-forwarded-for') ||
                      request.headers.get('x-real-ip') ||
                      'unknown';

      const now = Date.now();
      const windowStart = now - windowMs;

      // Clean up expired entries
      for (const [key, data] of rateLimitStore.entries()) {
        if (data.resetTime < now) {
          rateLimitStore.delete(key);
        }
      }

      // Get or create rate limit data
      let rateData = rateLimitStore.get(clientId);

      if (!rateData || rateData.resetTime < now) {
        rateData = {
          count: 0,
          resetTime: now + windowMs
        };
        rateLimitStore.set(clientId, rateData);
      }

      // Check if limit exceeded
      if (rateData.count >= maxRequests) {
        logger.warn('Rate limit exceeded', {
          clientId,
          count: rateData.count,
          maxRequests,
          windowMs,
          url: request.url
        });

        return NextResponse.json(
          {
            success: false,
            error: 'Rate limit exceeded',
            message: `Too many requests. Maximum ${maxRequests} requests per ${windowMs / 1000} seconds allowed.`,
            retryAfter: Math.ceil((rateData.resetTime - now) / 1000)
          },
          {
            status: 429,
            headers: {
              'X-RateLimit-Limit': maxRequests.toString(),
              'X-RateLimit-Remaining': '0',
              'X-RateLimit-Reset': rateData.resetTime.toString(),
              'Retry-After': Math.ceil((rateData.resetTime - now) / 1000).toString()
            }
          }
        );
      }

      // Increment count
      rateData.count++;

      // Add rate limit headers to successful responses
      const response = NextResponse.next();
      response.headers.set('X-RateLimit-Limit', maxRequests.toString());
      response.headers.set('X-RateLimit-Remaining', (maxRequests - rateData.count).toString());
      response.headers.set('X-RateLimit-Reset', rateData.resetTime.toString());

      return null; // Allow request to proceed
    } catch (error) {
      errorLogger.error('Rate limiting middleware error', {
        error: error instanceof Error ? error.message : error,
        url: request.url
      });

      // Don't block requests if rate limiting fails
      return null;
    }
  };
}

/**
 * Input Validation Middleware
 */
export function validateBody<T>(schema: {
  parse: (data: unknown) => T;
}) {
  return async (request: NextRequest): Promise<NextResponse | T> => {
    try {
      const body = await request.json();

      const validatedData = schema.parse(body);
      return validatedData;
    } catch (error) {
      if (error && typeof error === 'object' && 'issues' in error) {
        // Zod validation error
        const validationErrors = (error as any).issues.map((issue: any) => ({
          field: issue.path.join('.'),
          message: issue.message
        }));

        return NextResponse.json(
          {
            success: false,
            error: 'Validation failed',
            message: 'Request body contains invalid data',
            validationErrors
          },
          { status: 400 }
        );
      }

      errorLogger.error('Input validation error', {
        error: error instanceof Error ? error.message : error,
        url: request.url
      });

      return NextResponse.json(
        {
          success: false,
          error: 'Invalid request body',
          message: 'The request body could not be processed'
        },
        { status: 400 }
      );
    }
  };
}

/**
 * Security Headers Middleware
 */
export function securityHeaders(response: NextResponse): NextResponse {
  // Add security headers
  response.headers.set('X-Content-Type-Options', 'nosniff');
  response.headers.set('X-Frame-Options', 'DENY');
  response.headers.set('X-XSS-Protection', '1; mode=block');
  response.headers.set('Strict-Transport-Security', 'max-age=31536000; includeSubDomains');
  response.headers.set('Referrer-Policy', 'strict-origin-when-cross-origin');
  response.headers.set('Content-Security-Policy', "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';");

  return response;
}

/**
 * Error Handling Middleware
 */
export function handleError(error: unknown, context: string): NextResponse {
  errorLogger.error(`${context} error`, {
    error: error instanceof Error ? error.message : error,
    stack: error instanceof Error ? error.stack : undefined
  });

  if (error instanceof Error) {
    // Don't expose internal error details in production
    const isDevelopment = process.env.NODE_ENV === 'development';

    return NextResponse.json(
      {
        success: false,
        error: isDevelopment ? error.message : 'Internal server error',
        message: 'An unexpected error occurred',
        timestamp: new Date()
      },
      { status: 500 }
    );
  }

  return NextResponse.json(
    {
      success: false,
      error: 'Unknown error',
      message: 'An unexpected error occurred',
      timestamp: new Date()
    },
    { status: 500 }
  );
}