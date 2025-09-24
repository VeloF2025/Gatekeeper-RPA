import { NextRequest, NextResponse } from 'next/server';
import { WhatiTicketService } from '@/services/whatiTicketService';
import logger from '@/lib/logger';

const whatiTicketService = new WhatiTicketService();

export async function GET(_request: NextRequest) {
  try {
    const requestId = generateRequestId();
    logger.info('WhatiTicket health check requested', { requestId });

    // Get comprehensive health status
    const healthStatus = await whatiTicketService.getHealthStatus();

    // Determine HTTP status based on health
    let httpStatus = 200;
    if (healthStatus.status === 'unhealthy') {
      httpStatus = 503;
    } else if (healthStatus.status === 'degraded') {
      httpStatus = 200; // Still serving, but with degraded performance
    }

    logger.info('WhatiTicket health check completed', {
      requestId,
      status: healthStatus.status,
      services: healthStatus.services
    });

    return NextResponse.json({
      status: healthStatus.status,
      timestamp: new Date().toISOString(),
      requestId,
      services: healthStatus.services,
      metrics: healthStatus.metrics,
      version: process.env.npm_package_version || '1.0.0',
      environment: process.env.NODE_ENV || 'development'
    }, { status: httpStatus });
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    logger.error('WhatiTicket health check failed', { error: errorMessage });

    return NextResponse.json({
      status: 'unhealthy',
      timestamp: new Date().toISOString(),
      error: errorMessage,
      services: {
        database: 'unknown',
        redis: 'unknown',
        whatsapp: 'unknown',
        security: 'unknown'
      },
      metrics: {
        uptime: 0,
        memoryUsage: 0,
        activeConnections: 0
      }
    }, { status: 503 });
  }
}

function generateRequestId(): string {
  return `health_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
}