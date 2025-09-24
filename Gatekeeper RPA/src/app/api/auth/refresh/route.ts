import { NextRequest, NextResponse } from 'next/server';
import { authService } from '@/database/services/auth.service';
import { refreshTokenSchema } from '@/validation/schemas';
import { validateBody, securityHeaders, handleError } from '@/middleware/auth';
import { logger } from '@/utils/logger';

/**
 * POST /api/auth/refresh
 * Refresh access token endpoint
 */
export async function POST(request: NextRequest) {
  try {
    // Validate request body
    const bodyResult = await validateBody(refreshTokenSchema)(request);
    if (bodyResult instanceof NextResponse) {
      return bodyResult;
    }

    const { refreshToken } = bodyResult;

    // Refresh the access token
    const refreshResult = await authService.refreshToken(refreshToken);

    logger.info('Access token refreshed successfully', {
      userAgent: request.headers.get('user-agent'),
      ip: request.headers.get('x-forwarded-for') || 'unknown'
    });

    // Return success response
    const response = NextResponse.json({
      success: true,
      data: refreshResult,
      message: 'Access token refreshed successfully',
      timestamp: new Date()
    });

    // Apply security headers
    return securityHeaders(response);
  } catch (error) {
    // Handle specific refresh token errors
    if (error instanceof Error && error.message.includes('Invalid refresh token')) {
      logger.warn('Token refresh failed - invalid refresh token', {
        userAgent: request.headers.get('user-agent'),
        ip: request.headers.get('x-forwarded-for') || 'unknown'
      });

      return securityHeaders(
        NextResponse.json(
          {
            success: false,
            error: 'Invalid refresh token',
            message: 'Please login again to continue',
            timestamp: new Date()
          },
          { status: 401 }
        )
      );
    }

    if (error instanceof Error && error.message.includes('Session expired')) {
      logger.warn('Token refresh failed - session expired', {
        userAgent: request.headers.get('user-agent'),
        ip: request.headers.get('x-forwarded-for') || 'unknown'
      });

      return securityHeaders(
        NextResponse.json(
          {
            success: false,
            error: 'Session expired',
            message: 'Your session has expired. Please login again',
            timestamp: new Date()
          },
          { status: 401 }
        )
      );
    }

    return handleError(error, 'Token refresh');
  }
}

/**
 * GET /api/auth/refresh - Not allowed
 */
export async function GET() {
  return securityHeaders(
    NextResponse.json(
      {
        success: false,
        error: 'Method not allowed',
        message: 'Please use POST method for token refresh',
        timestamp: new Date()
      },
      { status: 405 }
    )
  );
}