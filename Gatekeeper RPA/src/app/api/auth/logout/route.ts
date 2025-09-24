import { NextRequest, NextResponse } from 'next/server';
import { authService } from '@/database/services/auth.service';
import { authMiddleware, securityHeaders, handleError } from '@/middleware/auth';
import { logger } from '@/utils/logger';

/**
 * POST /api/auth/logout
 * User logout endpoint
 */
export async function POST(request: NextRequest) {
  try {
    // Authenticate user
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult;
    }

    const { userId } = authResult;

    // Get authorization header for token
    const authHeader = request.headers.get('authorization');
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return securityHeaders(
        NextResponse.json(
          {
            success: false,
            error: 'Missing authorization header',
            message: 'Authorization header is required',
            timestamp: new Date()
          },
          { status: 400 }
        )
      );
    }

    const accessToken = authHeader.substring(7); // Remove 'Bearer ' prefix

    // Logout user (invalidate session)
    await authService.logout(accessToken);

    logger.info('User logout successful', {
      userId,
      userAgent: request.headers.get('user-agent'),
      ip: request.headers.get('x-forwarded-for') || 'unknown'
    });

    // Return success response
    const response = NextResponse.json({
      success: true,
      message: 'Logout successful',
      timestamp: new Date()
    });

    // Apply security headers
    return securityHeaders(response);
  } catch (error) {
    return handleError(error, 'Logout');
  }
}

/**
 * GET /api/auth/logout - Not allowed
 */
export async function GET() {
  return securityHeaders(
    NextResponse.json(
      {
        success: false,
        error: 'Method not allowed',
        message: 'Please use POST method for logout',
        timestamp: new Date()
      },
      { status: 405 }
    )
  );
}