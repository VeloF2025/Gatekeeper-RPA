/**
 * Performance Monitoring Middleware
 *
 * Automatically monitors API response times and performance metrics.
 * Integrates with the PerformanceMonitor service for comprehensive tracking.
 *
 * Created following Zero Trust security principles with comprehensive metrics collection.
 */

import { NextRequest, NextResponse } from 'next/server';
import { performance } from 'perf_hooks';
import { Database } from '@/database';
import { performanceMonitor } from '@/lib/performance-monitor';
import { logger } from '@/lib/logger';

export interface PerformanceMiddlewareOptions {
  excludePaths?: string[];
  includePaths?: string[];
  sampleRate?: number; // 0-1, fraction of requests to sample
  slowRequestThreshold?: number; // ms
}

export function createPerformanceMiddleware(database: Database, options: PerformanceMiddlewareOptions = {}) {
  const {
    excludePaths = ['/health', '/metrics', '/favicon.ico'],
    includePaths,
    sampleRate = 1,
    slowRequestThreshold = 200
  } = options;

  return function withPerformanceMonitoring(handler: (req: NextRequest) => Promise<NextResponse>) {
    return async (req: NextRequest): Promise<NextResponse> => {
      const startTime = performance.now();
      const method = req.method;
      const route = new URL(req.url).pathname;

      // Skip monitoring for excluded paths
      if (excludePaths.some(path => route.startsWith(path))) {
        return handler(req);
      }

      // Apply sampling if configured
      if (sampleRate < 1 && Math.random() > sampleRate) {
        return handler(req);
      }

      // Apply path filtering if includePaths is specified
      if (includePaths && !includePaths.some(path => route.startsWith(path))) {
        return handler(req);
      }

      let response: NextResponse;
      let success = true;
      let responseTime = 0;
      let error: string | undefined;

      try {
        response = await handler(req);
        responseTime = performance.now() - startTime;

        // Determine success based on HTTP status
        success = response.status >= 200 && response.status < 400;

        // Add performance headers
        response.headers.set('X-Response-Time', responseTime.toFixed(2));
        response.headers.set('X-Server-Timing', `total;dur=${responseTime.toFixed(2)}`);
        response.headers.set('X-Metrics-Collected', 'true');

      } catch (err) {
        success = false;
        error = err instanceof Error ? err.message : 'Unknown error';
        responseTime = performance.now() - startTime;

        logger.error('Request failed', {
          method,
          route,
          responseTime,
          error
        });

        throw err;
      } finally {
        try {
          // Record metrics
          const monitor = performanceMonitor(database);
          monitor.recordRequest(`${method} ${route}`, responseTime, success);

          // Log slow requests
          if (responseTime > slowRequestThreshold) {
            logger.warn(`Slow request: ${method} ${route} took ${responseTime.toFixed(2)}ms`);
          }

        } catch (monitorError) {
          // Don't let monitoring errors affect the request
          logger.error('Performance monitoring error:', monitorError);
        }
      }

      return response!;
    };
  };
}

// Database query monitoring wrapper
export function monitorDatabaseQuery<T>(
  database: Database,
  query: string,
  executeFn: () => Promise<T>,
  parameters?: any[]
): Promise<T> {
  const startTime = performance.now();

  return executeFn()
    .then(result => {
      const executionTime = performance.now() - startTime;
      const monitor = performanceMonitor(database);
      monitor.recordQuery(query, executionTime, parameters);
      return result;
    })
    .catch(error => {
      const executionTime = performance.now() - startTime;
      const monitor = performanceMonitor(database);
      monitor.recordQuery(query, executionTime, parameters);
      throw error;
    });
}

// Performance monitoring utilities
export class PerformanceUtils {
  static async measureExecutionTime<T>(
    name: string,
    database: Database,
    fn: () => Promise<T>
  ): Promise<{ result: T; executionTime: number }> {
    const startTime = performance.now();
    const result = await fn();
    const executionTime = performance.now() - startTime;

    // Record as a custom metric
    const monitor = performanceMonitor(database);
    monitor.recordQuery(`custom_metric:${name}`, executionTime);

    return { result, executionTime };
  }

  static createPerformanceTracker(database: Database) {
    const trackers = new Map<string, number>();

    return {
      start: (name: string) => {
        trackers.set(name, performance.now());
      },

      end: (name: string): number | undefined => {
        const startTime = trackers.get(name);
        if (startTime) {
          const executionTime = performance.now() - startTime;
          trackers.delete(name);

          const monitor = performanceMonitor(database);
          monitor.recordQuery(`tracker:${name}`, executionTime);

          return executionTime;
        }
        return undefined;
      },

      time: async <T>(name: string, fn: () => Promise<T>): Promise<T> => {
        this.start(name);
        const result = await fn();
        this.end(name);
        return result;
      }
    };
  }
}

// Legacy compatibility - export default function
export function withPerformanceMonitoring(handler: (req: NextRequest) => Promise<NextResponse>) {
  return async (req: NextRequest): Promise<NextResponse> => {
    const startTime = performance.now();
    const method = req.method;
    const route = new URL(req.url).pathname;

    try {
      const response = await handler(req);

      // Calculate request duration
      const duration = performance.now() - startTime;
      const statusCode = response.status;

      // Legacy support - try to use performance monitor if available
      try {
        const { Database } = await import('@/database');
        const database = Database.getInstance();
        const monitor = performanceMonitor(database);
        monitor.recordRequest(`${method} ${route}`, duration, statusCode >= 200 && statusCode < 400);
      } catch (e) {
        // Silently fail if performance monitor is not available
      }

      // Add performance headers
      response.headers.set('X-Response-Time', `${duration.toFixed(2)}ms`);
      response.headers.set('X-Metrics-Collected', 'true');

      return response;

    } catch (error) {
      const duration = performance.now() - startTime;
      const statusCode = 500;

      // Legacy support
      try {
        const { Database } = await import('@/database');
        const database = Database.getInstance();
        const monitor = performanceMonitor(database);
        monitor.recordRequest(`${method} ${route}`, duration, false);
      } catch (e) {
        // Silently fail
      }

      logger.error('Request failed', {
        method,
        route,
        duration,
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  };
}