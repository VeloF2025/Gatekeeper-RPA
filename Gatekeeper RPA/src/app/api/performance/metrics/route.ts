/**
 * Performance Metrics API Endpoint
 * Provides access to collected performance metrics
 */

import { NextRequest, NextResponse } from 'next/server';
import { performanceMonitor } from '@/services/monitoring/performance-monitor.service';
import { alertService } from '@/services/monitoring/alert.service';
import { queryAnalyzer } from '@/services/database/query-analyzer.service';
import { withAuth } from '@/middleware/auth';
import { withRateLimit } from '@/middleware/rate-limiter';
import { log } from '@/lib/logger';

async function handler(req: NextRequest) {
  try {
    const { searchParams } = new URL(req.url);
    const type = searchParams.get('type') || 'system';
    const timeRange = searchParams.get('timeRange') as '1h' | '24h' | '7d' | '30d' || '1h';

    switch (type) {
      case 'system': {
        const systemMetrics = await performanceMonitor.getSystemMetrics();
        return NextResponse.json({
          success: true,
          data: systemMetrics,
          timestamp: new Date().toISOString()
        });
      }

      case 'database': {
        const dbMetrics = await performanceMonitor.getDatabaseMetrics();
        return NextResponse.json({
          success: true,
          data: dbMetrics,
          timestamp: new Date().toISOString()
        });
      }

      case 'rpa': {
        const rpaMetrics = await performanceMonitor.getRPAMetrics();
        return NextResponse.json({
          success: true,
          data: rpaMetrics,
          timestamp: new Date().toISOString()
        });
      }

      case 'aggregated': {
        const aggregatedMetrics = await performanceMonitor.getAggregatedMetrics(timeRange);
        return NextResponse.json({
          success: true,
          data: aggregatedMetrics,
          timeRange,
          timestamp: new Date().toISOString()
        });
      }

      case 'alerts': {
        const alerts = alertService.getActiveAlerts();
        const alertStats = alertService.getAlertStats();
        return NextResponse.json({
          success: true,
          data: {
            alerts,
            stats: alertStats
          },
          timestamp: new Date().toISOString()
        });
      }

      case 'database-stats': {
        const dbStats = await queryAnalyzer.getDatabaseStats();
        return NextResponse.json({
          success: true,
          data: dbStats,
          timestamp: new Date().toISOString()
        });
      }

      case 'slow-queries': {
        const limit = parseInt(searchParams.get('limit') || '20');
        const slowQueries = await queryAnalyzer.getSlowQueries(limit);
        return NextResponse.json({
          success: true,
          data: slowQueries,
          timestamp: new Date().toISOString()
        });
      }

      case 'index-recommendations': {
        const indexRecommendations = await queryAnalyzer.generateIndexRecommendations();
        return NextResponse.json({
          success: true,
          data: indexRecommendations,
          timestamp: new Date().toISOString()
        });
      }

      default:
        return NextResponse.json(
          { success: false, error: 'Invalid metrics type' },
          { status: 400 }
        );
    }
  } catch (error) {
    log.error('Performance metrics API error:', error, 'performance-api');
    return NextResponse.json(
      {
        success: false,
        error: 'Failed to fetch performance metrics',
        details: error instanceof Error ? error.message : 'Unknown error'
      },
      { status: 500 }
    );
  }
}

// Export with middlewares
export const GET = withAuth(withRateLimit(handler, { requests: 100, windowMs: 60000 }));