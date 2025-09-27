import { NextRequest, NextResponse } from 'next/server';
import { authMiddleware } from '@/middleware/auth';
import { logger } from '@/utils/logger';
import { database } from '@/database/database';
import { eq, and, gte, sql } from 'drizzle-orm';
import { rpaJobs } from '@/database/schemas';
import { RPAService } from '@/services/rpa/rpa.service';

/**
 * GET /api/rpa/health - Comprehensive RPA system health check
 */
export async function GET(request: NextRequest) {
  const startTime = Date.now();

  try {
    // Check authentication (optional for health checks)
    const isHealthCheckFromSystem = request.headers.get('user-agent')?.includes('health-check');
    let authResult = null;

    if (!isHealthCheckFromSystem) {
      authResult = await authMiddleware(request);
      if (authResult instanceof NextResponse) {
        return authResult; // Return error response
      }
    }

    logger.info('RPA health check initiated', {
      userId: authResult?.userId,
      userAgent: request.headers.get('user-agent')
    });

    const healthStatus = {
      status: 'healthy' as 'healthy' | 'degraded' | 'unhealthy',
      score: 100,
      checks: [] as Array<{
        name: string;
        status: 'pass' | 'fail' | 'warn';
        message: string;
        details?: any;
        duration: number;
      }>,
      metrics: {
        database: {},
        rpaService: {},
        configuration: {},
        performance: {},
        load: {}
      },
      timestamp: new Date().toISOString()
    };

    // 1. Database Connection Check
    const dbCheckStart = Date.now();
    try {
      const [dbResult] = await database.drizzle
        .select({ count: sql`count(*)` })
        .from(rpaJobs)
        .limit(1);

      healthStatus.checks.push({
        name: 'database_connection',
        status: 'pass',
        message: 'Database connection successful',
        details: { queryTime: Date.now() - dbCheckStart },
        duration: Date.now() - dbCheckStart
      });

      healthStatus.metrics.database = {
        connected: true,
        responseTime: Date.now() - dbCheckStart,
        totalJobs: Number(dbResult.count)
      };

    } catch (error) {
      const dbErrorTime = Date.now() - dbCheckStart;
      healthStatus.checks.push({
        name: 'database_connection',
        status: 'fail',
        message: `Database connection failed: ${error instanceof Error ? error.message : 'Unknown error'}`,
        duration: dbErrorTime
      });

      healthStatus.metrics.database = {
        connected: false,
        responseTime: dbErrorTime,
        error: error instanceof Error ? error.message : 'Unknown error'
      };

      healthStatus.status = 'unhealthy';
      healthStatus.score -= 40;
    }

    // 2. RPA Service Initialization Check
    const rpaCheckStart = Date.now();
    try {
      // Test RPA service availability (without actually launching browser in production)
      const testOptions = {
        headless: process.env.NODE_ENV === 'production',
        timeout: 5000, // Short timeout for health check
        screenshots: false
      };

      // Note: In production, we might not want to actually launch browsers for health checks
      // For now, we'll just verify the service can be instantiated
      new RPAService();
      healthStatus.checks.push({
        name: 'rpa_service_available',
        status: 'pass',
        message: 'RPA service is available',
        details: { serviceType: 'RPAService', options: testOptions },
        duration: Date.now() - rpaCheckStart
      });

      healthStatus.metrics.rpaService = {
        available: true,
        initializationTime: Date.now() - rpaCheckStart
      };

    } catch (error) {
      const rpaErrorTime = Date.now() - rpaCheckStart;
      healthStatus.checks.push({
        name: 'rpa_service_available',
        status: 'fail',
        message: `RPA service unavailable: ${error instanceof Error ? error.message : 'Unknown error'}`,
        duration: rpaErrorTime
      });

      healthStatus.metrics.rpaService = {
        available: false,
        error: error instanceof Error ? error.message : 'Unknown error'
      };

      healthStatus.status = 'unhealthy';
      healthStatus.score -= 30;
    }

    // 3. Configuration Check
    const configCheckStart = Date.now();
    const requiredConfig = [
      'ONEMAP_BASE_URL',
      'ONEMAP_USERNAME',
      'ONEMAP_PASSWORD',
      'DATABASE_URL'
    ];

    const missingConfig = requiredConfig.filter(key => !process.env[key]);
    const configStatus = missingConfig.length === 0 ? 'pass' : 'fail';

    healthStatus.checks.push({
      name: 'configuration',
      status: configStatus,
      message: configStatus === 'pass'
        ? 'All required configuration variables present'
        : `Missing configuration: ${missingConfig.join(', ')}`,
      details: { missingConfig, requiredConfig },
      duration: Date.now() - configCheckStart
    });

    healthStatus.metrics.configuration = {
      valid: missingConfig.length === 0,
      missingVariables: missingConfig,
      totalVariables: requiredConfig.length
    };

    if (configStatus === 'fail') {
      healthStatus.status = 'unhealthy';
      healthStatus.score -= 20;
    }

    // 4. Recent Performance Check
    const perfCheckStart = Date.now();
    try {
      const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

      const [recentJobs] = await database.drizzle
        .select({ count: sql`count(*)` })
        .from(rpaJobs)
        .where(gte(rpaJobs.createdAt, oneHourAgo));

      const [recentFailedJobs] = await database.drizzle
        .select({ count: sql`count(*)` })
        .from(rpaJobs)
        .where(
          and(
            eq(rpaJobs.status, 'failed'),
            gte(rpaJobs.createdAt, oneHourAgo)
          )
        );

      const recentJobsCount = Number(recentJobs.count);
      const recentFailedJobsCount = Number(recentFailedJobs.count);
      const recentSuccessRate = recentJobsCount > 0
        ? ((recentJobsCount - recentFailedJobsCount) / recentJobsCount) * 100
        : 100;

      const perfStatus = recentSuccessRate >= 80 ? 'pass' : recentSuccessRate >= 50 ? 'warn' : 'fail';

      healthStatus.checks.push({
        name: 'recent_performance',
        status: perfStatus,
        message: `Recent success rate: ${recentSuccessRate.toFixed(1)}%`,
        details: {
          timeRange: '1h',
          totalJobs: recentJobs.count,
          failedJobs: recentFailedJobs.count,
          successRate: recentSuccessRate
        },
        duration: Date.now() - perfCheckStart
      });

      healthStatus.metrics.performance = {
        recentJobs: Number(recentJobs.count),
        recentFailedJobs: Number(recentFailedJobs.count),
        successRate: recentSuccessRate,
        timeRange: '1h'
      };

      if (perfStatus === 'fail') {
        healthStatus.status = 'unhealthy';
        healthStatus.score -= 25;
      } else if (perfStatus === 'warn') {
        healthStatus.status = 'degraded';
        healthStatus.score -= 10;
      }

    } catch (error) {
      const perfErrorTime = Date.now() - perfCheckStart;
      healthStatus.checks.push({
        name: 'recent_performance',
        status: 'warn',
        message: `Could not retrieve performance data: ${error instanceof Error ? error.message : 'Unknown error'}`,
        duration: perfErrorTime
      });

      healthStatus.metrics.performance = {
        error: error instanceof Error ? error.message : 'Unknown error'
      };

      healthStatus.status = 'degraded';
      healthStatus.score -= 15;
    }

    // 5. Current Load Check
    const loadCheckStart = Date.now();
    try {
      const [inProgressJobs] = await database.drizzle
        .select({ count: sql`count(*)` })
        .from(rpaJobs)
        .where(eq(rpaJobs.status, 'in_progress'));

      const inProgressJobsCount = Number(inProgressJobs.count);
      const loadStatus = inProgressJobsCount <= 3 ? 'pass' : inProgressJobsCount <= 5 ? 'warn' : 'fail';

      healthStatus.checks.push({
        name: 'current_load',
        status: loadStatus,
        message: `${inProgressJobs.count} jobs currently in progress`,
        details: { inProgressJobs: inProgressJobs.count, threshold: 3 },
        duration: Date.now() - loadCheckStart
      });

      healthStatus.metrics.load = {
        inProgressJobs: Number(inProgressJobs.count),
        status: loadStatus,
        threshold: 3
      };

      if (loadStatus === 'fail') {
        healthStatus.status = 'degraded';
        healthStatus.score -= 15;
      } else if (loadStatus === 'warn') {
        healthStatus.status = 'degraded';
        healthStatus.score -= 5;
      }

    } catch (error) {
      const loadErrorTime = Date.now() - loadCheckStart;
      healthStatus.checks.push({
        name: 'current_load',
        status: 'warn',
        message: `Could not retrieve load data: ${error instanceof Error ? error.message : 'Unknown error'}`,
        duration: loadErrorTime
      });

      healthStatus.metrics.load = {
        error: error instanceof Error ? error.message : 'Unknown error'
      };

      healthStatus.status = 'degraded';
      healthStatus.score -= 10;
    }

    // Calculate final health score
    healthStatus.score = Math.max(0, Math.min(100, healthStatus.score));

    // Determine overall status based on score
    if (healthStatus.score >= 90) {
      healthStatus.status = 'healthy';
    } else if (healthStatus.score >= 70) {
      healthStatus.status = 'degraded';
    } else {
      healthStatus.status = 'unhealthy';
    }

    // Add recommendations based on issues found
    const recommendations: string[] = [];

    if (healthStatus.score < 100) {
      const failedChecks = healthStatus.checks.filter(check => check.status === 'fail');
      const warnChecks = healthStatus.checks.filter(check => check.status === 'warn');

      failedChecks.forEach(check => {
        switch (check.name) {
          case 'database_connection':
            recommendations.push('Check database connectivity and credentials');
            break;
          case 'rpa_service_available':
            recommendations.push('Verify Playwright installation and browser dependencies');
            break;
          case 'configuration':
            recommendations.push('Review environment variables and configuration');
            break;
          case 'recent_performance':
            recommendations.push('Investigate recent job failures and optimize performance');
            break;
          case 'current_load':
            recommendations.push('Consider scaling or load balancing for high job volume');
            break;
        }
      });

      if (recommendations.length === 0 && warnChecks.length > 0) {
        recommendations.push('Monitor warning conditions and optimize system performance');
      }
    }

    const responseTime = Date.now() - startTime;

    logger.info('RPA health check completed', {
      overallStatus: healthStatus.status,
      score: healthStatus.score,
      checksCount: healthStatus.checks.length,
      failedChecks: healthStatus.checks.filter(c => c.status === 'fail').length,
      responseTime
    });

    return NextResponse.json({
      success: true,
      data: {
        health: healthStatus,
        recommendations,
        uptime: process.uptime ? Math.floor(process.uptime()) : null,
        version: process.env.npm_package_version || '1.0.0',
        environment: process.env.NODE_ENV || 'development'
      },
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error during RPA health check', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        success: false,
        error: 'Health check failed',
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