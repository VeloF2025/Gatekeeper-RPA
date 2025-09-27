/**
 * Performance Monitoring API
 *
 * Provides comprehensive performance monitoring, optimization,
 * and management capabilities for the Gatekeeper RPA system.
 */

import { NextRequest, NextResponse } from 'next/server';
import { authMiddleware } from '@/middleware/auth';
import { logger } from '@/utils/logger';
import { performanceCollector } from '@/lib/performance/performance-collector';
import { performanceOptimizer } from '@/lib/performance/performance-optimizer';

/**
 * GET /api/performance - Get comprehensive performance data
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
    const includeMetrics = searchParams.get('metrics') !== 'false';
    const includeAlerts = searchParams.get('alerts') !== 'false';
    const includeOptimization = searchParams.get('optimization') !== 'false';
    const includeHistory = searchParams.get('history') === 'true';
    const includeReport = searchParams.get('report') === 'true';

    logger.info('Performance data requested', {
      includeMetrics,
      includeAlerts,
      includeOptimization,
      includeHistory,
      includeReport,
      userId: authResult.userId
    });

    const response: any = {
      success: true,
      timestamp: new Date().toISOString(),
      performance: {
        responseTime: Date.now() - startTime
      }
    };

    // Include current metrics
    if (includeMetrics) {
      response.metrics = performanceCollector.getCurrentMetrics();
    }

    // Include alerts
    if (includeAlerts) {
      response.alerts = {
        active: performanceCollector.getActiveAlerts(),
        total: performanceCollector.getActiveAlerts().length
      };
    }

    // Include optimization data
    if (includeOptimization) {
      response.optimization = {
        strategies: performanceOptimizer.getStrategies(),
        statistics: performanceOptimizer.getStatistics(),
        recentResults: performanceOptimizer.getResults(10)
      };
    }

    // Include metrics history
    if (includeHistory) {
      response.history = {
        metrics: performanceCollector.getMetricsHistory(24),
        alerts: [] // In a real implementation, this would query a database
      };
    }

    // Include comprehensive report
    if (includeReport) {
      response.report = {
        performance: performanceCollector.generateReport(),
        optimization: performanceOptimizer.generateReport()
      };
    }

    const responseTime = Date.now() - startTime;

    logger.info('Performance data retrieved successfully', {
      responseTime,
      dataPoints: Object.keys(response).length
    });

    return NextResponse.json(response);

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error getting performance data', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to get performance data',
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

/**
 * POST /api/performance/optimize - Run performance optimization
 */
export async function POST(request: NextRequest) {
  const startTime = Date.now();

  try {
    // Check authentication
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    const body = await request.json();
    const { strategy, trigger = 'manual' } = body;

    logger.info('Performance optimization requested', {
      strategy,
      trigger,
      userId: authResult.userId
    });

    let results;

    if (strategy) {
      // Run specific strategy
      const result = await performanceOptimizer.runStrategy(strategy);
      results = result ? [result] : [];
    } else {
      // Run all applicable strategies
      results = await performanceOptimizer.runOptimization(trigger);
    }

    const responseTime = Date.now() - startTime;

    logger.info('Performance optimization completed', {
      strategy,
      trigger,
      resultsCount: results.length,
      responseTime
    });

    return NextResponse.json({
      success: true,
      data: {
        results,
        summary: {
          total: results.length,
          successful: results.filter(r => r.success).length,
          failed: results.filter(r => !r.success).length,
          averageExecutionTime: results.length > 0
            ? Math.round(results.reduce((sum, r) => sum + r.executionTime, 0) / results.length)
            : 0
        }
      },
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error running performance optimization', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to run performance optimization',
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