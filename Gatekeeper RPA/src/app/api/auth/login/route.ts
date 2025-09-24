import { NextRequest, NextResponse } from 'next/server';
import { authService } from '@/services/auth/auth.service';
import { loginSchema } from '@/validation/schemas';
import { validateBody, rateLimit, securityHeaders, handleError } from '@/middleware/auth';
import { logger } from '@/utils/logger';

/**
 * POST /api/auth/login
 * User login endpoint
 */
export async function POST(request: NextRequest) {
  try {
    // Apply rate limiting
    const rateLimitResult = await rateLimit(5, 60 * 1000)(request); // 5 requests per minute
    if (rateLimitResult instanceof NextResponse) {
      return rateLimitResult;
    }

    // Validate request body
    const bodyResult = await validateBody(loginSchema)(request);
    if (bodyResult instanceof NextResponse) {
      return bodyResult;
    }

    const { email, password } = bodyResult;

    // Authenticate user (using email as username for now)
    const loginResponse = await authService.login({ username: email, password });

    // Log successful login
    logger.info('User login successful', {
      userId: loginResponse.user.id,
      email: loginResponse.user.email,
      userAgent: request.headers.get('user-agent'),
      ip: request.headers.get('x-forwarded-for') || 'unknown'
    });

    // Return success response
    const response = NextResponse.json({
      success: true,
      data: loginResponse,
      message: 'Login successful',
      timestamp: new Date()
    });

    // Apply security headers
    return securityHeaders(response);
  } catch (error) {
    // Handle authentication errors specifically
    if (error instanceof Error && error.message.includes('Invalid credentials')) {
      logger.warn('Login failed - invalid credentials', {
        email: 'unknown', // Can't access body after error
        userAgent: request.headers.get('user-agent'),
        ip: request.headers.get('x-forwarded-for') || 'unknown'
      });

      return securityHeaders(
        NextResponse.json(
          {
            success: false,
            error: 'Invalid credentials',
            message: 'The email or password you entered is incorrect',
            timestamp: new Date()
          },
          { status: 401 }
        )
      );
    }

    if (error instanceof Error && error.message.includes('already exists')) {
      logger.warn('Login failed - user already exists', {
        email: 'unknown' // Can't access body after error
      });

      return securityHeaders(
        NextResponse.json(
          {
            success: false,
            error: 'Account conflict',
            message: error.message,
            timestamp: new Date()
          },
          { status: 409 }
        )
      );
    }

    return handleError(error, 'Login');
  }
}

/**
 * GET /api/auth/login - Not allowed
 */
export async function GET() {
  return securityHeaders(
    NextResponse.json(
      {
        success: false,
        error: 'Method not allowed',
        message: 'Please use POST method for login',
        timestamp: new Date()
      },
      { status: 405 }
    )
  );
}