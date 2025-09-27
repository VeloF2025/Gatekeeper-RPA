/**
 * WebSocket Route for Real-time Performance Metrics
 *
 * Provides real-time performance metrics streaming to connected clients
 * using Server-Sent Events (SSE) for better compatibility.
 */

import { NextRequest } from 'next/server';
import { performanceCollector } from '@/lib/performance/performance-collector';
import { logger } from '@/utils/logger';

/**
 * GET /api/performance/ws - Real-time performance metrics stream
 */
export async function GET(request: NextRequest) {
  const startTime = Date.now();

  try {
    const { searchParams } = new URL(request.url);
    const clientId = searchParams.get('clientId') || 'unknown';
    const interval = Number(searchParams.get('interval')) || 5000; // Default 5 seconds

    logger.info('Performance metrics stream requested', {
      clientId,
      interval,
      userAgent: request.headers.get('user-agent')
    });

    // Create a readable stream for SSE
    const stream = new ReadableStream({
      start(controller) {
        let isActive = true;

        // Send initial metrics
        const initialMetrics = performanceCollector.getCurrentMetrics();
        controller.enqueue(`data: ${JSON.stringify({
          type: 'initial',
          metrics: initialMetrics,
          timestamp: Date.now()
        })}\n\n`);

        // Subscribe to real-time updates
        const unsubscribe = performanceCollector.subscribe((metrics) => {
          if (!isActive) return;

          try {
            const message = {
              type: 'update',
              metrics,
              timestamp: Date.now()
            };

            controller.enqueue(`data: ${JSON.stringify(message)}\n\n`);
          } catch (error) {
            logger.error('Error sending metrics to client', {
              clientId,
              error: error instanceof Error ? error.message : 'Unknown error'
            });
            isActive = false;
            controller.close();
          }
        });

        // Subscribe to alerts
        const alertHandler = (alert: any) => {
          if (!isActive) return;

          try {
            const message = {
              type: 'alert',
              alert,
              timestamp: Date.now()
            };

            controller.enqueue(`data: ${JSON.stringify(message)}\n\n`);
          } catch (error) {
            logger.error('Error sending alert to client', {
              clientId,
              error: error instanceof Error ? error.message : 'Unknown error'
            });
          }
        };

        performanceCollector.on('alert', alertHandler);

        // Send periodic heartbeat
        const heartbeatInterval = setInterval(() => {
          if (!isActive) {
            clearInterval(heartbeatInterval);
            return;
          }

          try {
            controller.enqueue(`data: ${JSON.stringify({
              type: 'heartbeat',
              timestamp: Date.now()
            })}\n\n`);
          } catch (error) {
            logger.error('Error sending heartbeat to client', { clientId });
            isActive = false;
            clearInterval(heartbeatInterval);
          }
        }, 30000); // Every 30 seconds

        // Handle client disconnect
        request.signal.addEventListener('abort', () => {
          isActive = false;
          clearInterval(heartbeatInterval);
          unsubscribe();
          performanceCollector.off('alert', alertHandler);
          controller.close();

          logger.info('Performance metrics stream disconnected', { clientId });
        });
      }
    });

    const responseTime = Date.now() - startTime;
    logger.info('Performance metrics stream established', {
      clientId,
      responseTime
    });

    return new Response(stream, {
      headers: {
        'Content-Type': 'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Cache-Control'
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error establishing performance metrics stream', {
      error: errorMessage,
      responseTime
    });

    return new Response(
      JSON.stringify({
        error: 'Failed to establish performance metrics stream',
        details: errorMessage
      }),
      {
        status: 500,
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
  }
}