import { NextRequest, NextResponse } from 'next/server';
import { database } from '@/database/database';
import { logger } from '@/utils/logger';

/**
 * GET /api/health
 * Health check endpoint
 */
export async function GET(request: NextRequest) {
  try {
    const startTime = Date.now();

    // Check database connectivity
    const dbHealth = await database.healthCheck();

    // Get memory usage
    const memoryUsage = process.memoryUsage();

    // Get uptime
    const uptime = process.uptime();

    // Calculate response time
    const responseTime = Date.now() - startTime;

    // Prepare health status
    const healthStatus = {
      status: dbHealth ? 'healthy' : 'unhealthy',
      timestamp: new Date().toISOString(),
      uptime: Math.floor(uptime),
      responseTime,
      checks: {
        database: {
          status: dbHealth ? 'healthy' : 'unhealthy',
          responseTime: responseTime
        },
        memory: {
          rss: Math.round(memoryUsage.rss / 1024 / 1024), // MB
          heapTotal: Math.round(memoryUsage.heapTotal / 1024 / 1024), // MB
          heapUsed: Math.round(memoryUsage.heapUsed / 1024 / 1024), // MB
          external: Math.round(memoryUsage.external / 1024 / 1024), // MB
        },
        system: {
          nodeVersion: process.version,
          platform: process.platform,
          arch: process.arch,
        }
      },
      version: {
        api: '1.0.0',
        node: process.version
      }
    };

    // Set appropriate HTTP status based on health
    const httpStatus = healthStatus.status === 'healthy' ? 200 : 503;

    logger.info('Health check completed', {
      status: healthStatus.status,
      responseTime,
      dbHealth,
      userAgent: request.headers.get('user-agent'),
      ip: request.headers.get('x-forwarded-for') || 'unknown'
    });

    return NextResponse.json(healthStatus, {
      status: httpStatus,
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      }
    });
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Health check failed', {
      error: errorMessage,
      userAgent: request.headers.get('user-agent'),
      ip: request.headers.get('x-forwarded-for') || 'unknown'
    });

    return NextResponse.json({
      status: 'unhealthy',
      timestamp: new Date().toISOString(),
      error: errorMessage,
      checks: {
        database: {
          status: 'unhealthy',
          error: errorMessage
        }
      }
    }, {
      status: 503,
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      }
    });
  }
}

/**
 * HEAD /api/health
 * Health check HEAD endpoint (for monitoring)
 */
export async function HEAD() {
  try {
    const dbHealth = await database.healthCheck();
    const status = dbHealth ? 200 : 503;

    return new NextResponse(null, {
      status,
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      }
    });
  } catch (_error) {
    return new NextResponse(null, {
      status: 503,
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      }
    });
  }
}