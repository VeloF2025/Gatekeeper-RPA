import { NextRequest, NextResponse } from 'next/server';
import { authMiddleware } from '@/middleware/auth';
import { logger } from '@/utils/logger';
import { database } from '@/database/database';
import { eq, and, gte, desc, sql } from 'drizzle-orm';
import { rpaJobs } from '@/database/schemas';

/**
 * GET /api/rpa/stats - Get RPA system statistics and performance metrics
 */
export async function GET(request: NextRequest) {
  const startTime = Date.now();

  try {
    // Check authentication
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    const { searchParams } = new URL(request.url);
    const timeRange = searchParams.get('timeRange') || '24h'; // 1h, 24h, 7d, 30d
    const detailed = searchParams.get('detailed') === 'true';

    logger.info('RPA statistics requested', {
      timeRange,
      detailed,
      userId: authResult.userId
    });

    // Calculate time range
    const now = new Date();
    let timeThreshold: Date;

    switch (timeRange) {
      case '1h':
        timeThreshold = new Date(now.getTime() - 60 * 60 * 1000);
        break;
      case '7d':
        timeThreshold = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case '30d':
        timeThreshold = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
        break;
      case '24h':
      default:
        timeThreshold = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        break;
    }

    // Basic statistics
    const [totalJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs);

    const [completedJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(eq(rpaJobs.status, 'completed'));

    const [failedJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(eq(rpaJobs.status, 'failed'));

    const [inProgressJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(eq(rpaJobs.status, 'in_progress'));

    // Time-range based statistics
    const [recentJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(gte(rpaJobs.createdAt, timeThreshold));

    const [recentCompletedJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(
        and(
          eq(rpaJobs.status, 'completed'),
          gte(rpaJobs.createdAt, timeThreshold)
        )
      );

    const [recentFailedJobs] = await database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(
        and(
          eq(rpaJobs.status, 'failed'),
          gte(rpaJobs.createdAt, timeThreshold)
        )
      );

    // Performance metrics
    const performanceMetrics = await database.drizzle
      .select({
        avgExecutionTime: sql`AVG(EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)`,
        minExecutionTime: sql`MIN(EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)`,
        maxExecutionTime: sql`MAX(EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)`,
        avgProcessingTime: sql`AVG(EXTRACT(EPOCH FROM (completed_at - created_at)) * 1000)`
      })
      .from(rpaJobs)
      .where(
        and(
          eq(rpaJobs.status, 'completed'),
          sql`started_at IS NOT NULL`,
          sql`completed_at IS NOT NULL`
        )
      );

    // Recent performance metrics (time range)
    const recentPerformanceMetrics = await database.drizzle
      .select({
        avgExecutionTime: sql`AVG(EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)`,
        minExecutionTime: sql`MIN(EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)`,
        maxExecutionTime: sql`MAX(EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)`,
        avgProcessingTime: sql`AVG(EXTRACT(EPOCH FROM (completed_at - created_at)) * 1000)`
      })
      .from(rpaJobs)
      .where(
        and(
          eq(rpaJobs.status, 'completed'),
          gte(rpaJobs.createdAt, timeThreshold),
          sql`started_at IS NOT NULL`,
          sql`completed_at IS NOT NULL`
        )
      );

    // Success rates
    const successRate = Number(totalJobs.count) > 0 ? (Number(completedJobs.count) / Number(totalJobs.count)) * 100 : 0;
    const recentSuccessRate = Number(recentJobs.count) > 0 ? (Number(recentCompletedJobs.count) / Number(recentJobs.count)) * 100 : 0;

    // Detailed statistics
    let detailedStats = null;
    if (detailed) {
      // Get jobs by hour for the time range
      const jobsByHour = await database.drizzle
        .select({
          hour: sql`DATE_TRUNC('hour', created_at)`,
          total: sql`count(*)`,
          completed: sql`COUNT(CASE WHEN status = 'completed' THEN 1 END)`,
          failed: sql`COUNT(CASE WHEN status = 'failed' THEN 1 END)`
        })
        .from(rpaJobs)
        .where(gte(rpaJobs.createdAt, timeThreshold))
        .groupBy(sql`DATE_TRUNC('hour', created_at)`)
        .orderBy(sql`DATE_TRUNC('hour', created_at)`);

      // Get top errors
      const topErrors = await database.drizzle
        .select({
          error: rpaJobs.error,
          count: sql`count(*)`
        })
        .from(rpaJobs)
        .where(
          and(
            eq(rpaJobs.status, 'failed'),
            sql`error IS NOT NULL`,
            gte(rpaJobs.createdAt, timeThreshold)
          )
        )
        .groupBy(rpaJobs.error)
        .orderBy(desc(sql`count(*)`))
        .limit(5);

      // Get recent jobs
      const recentJobsList = await database.drizzle
        .select({
          id: rpaJobs.id,
          ticketId: rpaJobs.ticketId,
          drNumber: rpaJobs.drNumber,
          status: rpaJobs.status,
          createdAt: rpaJobs.createdAt,
          completedAt: rpaJobs.completedAt,
          error: rpaJobs.error
        })
        .from(rpaJobs)
        .where(gte(rpaJobs.createdAt, timeThreshold))
        .orderBy(desc(rpaJobs.createdAt))
        .limit(10);

      detailedStats = {
        jobsByHour,
        topErrors,
        recentJobs: recentJobsList
      };
    }

    // Calculate system health
    const systemHealth = {
      status: 'healthy',
      score: 100,
      issues: [] as string[]
    };

    // Check for potential issues
    if (Number(recentFailedJobs.count) > Number(recentJobs.count) * 0.2) {
      systemHealth.status = 'degraded';
      systemHealth.score -= 30;
      systemHealth.issues.push('High failure rate detected');
    }

    if (Number(inProgressJobs.count) > 5) {
      systemHealth.status = 'busy';
      systemHealth.score -= 10;
      systemHealth.issues.push('High number of concurrent jobs');
    }

    if (recentSuccessRate < 80) {
      systemHealth.status = 'degraded';
      systemHealth.score -= 20;
      systemHealth.issues.push('Success rate below 80%');
    }

    // Check performance metrics
    const avgExecutionTime = Number(performanceMetrics[0]?.avgExecutionTime || 0);
    if (avgExecutionTime > 30000) { // 30 seconds
      systemHealth.status = 'degraded';
      systemHealth.score -= 15;
      systemHealth.issues.push('Average execution time exceeds 30 seconds');
    }

    const responseTime = Date.now() - startTime;

    logger.info('RPA statistics retrieved successfully', {
      totalJobs: totalJobs.count,
      successRate,
      systemHealth: systemHealth.status,
      responseTime
    });

    return NextResponse.json({
      success: true,
      data: {
        overview: {
          totalJobs: Number(totalJobs.count),
          completedJobs: Number(completedJobs.count),
          failedJobs: Number(failedJobs.count),
          inProgressJobs: Number(inProgressJobs.count),
          successRate: Math.round(successRate * 100) / 100,
          failureRate: Math.round(((Number(failedJobs.count) / Number(totalJobs.count)) || 0) * 100) / 100
        },
        recent: {
          timeRange,
          totalJobs: Number(recentJobs.count),
          completedJobs: Number(recentCompletedJobs.count),
          failedJobs: Number(recentFailedJobs.count),
          successRate: Math.round(recentSuccessRate * 100) / 100,
          failureRate: Math.round(((Number(recentFailedJobs.count) / Number(recentJobs.count)) || 0) * 100) / 100
        },
        performance: {
          overall: {
            avgExecutionTime: Math.round(Number(performanceMetrics[0]?.avgExecutionTime) || 0),
            minExecutionTime: Math.round(Number(performanceMetrics[0]?.minExecutionTime) || 0),
            maxExecutionTime: Math.round(Number(performanceMetrics[0]?.maxExecutionTime) || 0),
            avgProcessingTime: Math.round(Number(performanceMetrics[0]?.avgProcessingTime) || 0)
          },
          recent: {
            avgExecutionTime: Math.round(Number(recentPerformanceMetrics[0]?.avgExecutionTime) || 0),
            minExecutionTime: Math.round(Number(recentPerformanceMetrics[0]?.minExecutionTime) || 0),
            maxExecutionTime: Math.round(Number(recentPerformanceMetrics[0]?.maxExecutionTime) || 0),
            avgProcessingTime: Math.round(Number(recentPerformanceMetrics[0]?.avgProcessingTime) || 0)
          }
        },
        systemHealth,
        detailed: detailedStats
      },
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      },
      metadata: {
        generatedAt: new Date().toISOString(),
        timeRange,
        dataFreshness: 'real-time'
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error getting RPA statistics', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to get RPA statistics',
        details: errorMessage,
        performance: {
          responseTime,
          timestamp: new Date().toISOString()
        }
      },
      { status: 500 }
    );
  }
}