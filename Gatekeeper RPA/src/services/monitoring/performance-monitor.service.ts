/**
 * Performance Monitoring Service
 * Real-time metrics collection and aggregation for the Gatekeeper RPA system
 */

import { performance } from 'perf_hooks';
import { metrics } from '@/lib/monitoring/metrics';
import { logger } from '@/lib/logger';
import { db } from '@/database/database';
import { performance_metrics } from '@/database/schema';

export interface PerformanceMetric {
  name: string;
  value: number;
  type: 'counter' | 'gauge' | 'histogram';
  tags?: Record<string, string>;
  timestamp?: Date;
}

export interface SystemMetrics {
  cpu: {
    usage: number;
    cores: number;
    loadAverage: number[];
  };
  memory: {
    total: number;
    used: number;
    free: number;
    percentage: number;
  };
  disk: {
    total: number;
    used: number;
    free: number;
    percentage: number;
  };
  network: {
    incoming: number;
    outgoing: number;
  };
}

export interface DatabaseMetrics {
  connectionCount: number;
  queryTimes: {
    average: number;
    min: number;
    max: number;
    p95: number;
    p99: number;
  };
  slowQueries: Array<{
    query: string;
    duration: number;
    timestamp: Date;
  }>;
}

export interface RPAMetrics {
  activeJobs: number;
  completedJobs: number;
  failedJobs: number;
  averageExecutionTime: number;
  successRate: number;
  queueLength: number;
}

class PerformanceMonitor {
  private metricsBuffer: PerformanceMetric[] = [];
  private isCollecting = false;
  private collectionInterval?: NodeJS.Timeout;
  private systemMetricsInterval?: NodeJS.Timeout;

  /**
   * Initialize performance monitoring
   */
  async initialize(): Promise<void> {
    if (this.isCollecting) return;

    this.isCollecting = true;

    // Start metrics collection
    this.collectionInterval = setInterval(() => {
      this.flushMetrics();
    }, 10000); // Flush every 10 seconds

    // Start system metrics collection
    this.systemMetricsInterval = setInterval(() => {
      this.collectSystemMetrics();
    }, 5000); // Collect every 5 seconds

    logger.info('Performance monitoring initialized');
  }

  /**
   * Record a performance metric
   */
  recordMetric(metric: PerformanceMetric): void {
    // Add to Prometheus metrics
    switch (metric.type) {
      case 'counter':
        metrics.recordError(metric.name, 'system', metric.tags?.severity || 'info');
        break;
      case 'gauge':
        metrics.setMemoryUsage(metric.name, metric.value);
        break;
      case 'histogram':
        // Record histogram metric
        break;
    }

    // Add to buffer for database storage
    this.metricsBuffer.push({
      ...metric,
      timestamp: metric.timestamp || new Date()
    });

    // Buffer size check
    if (this.metricsBuffer.length > 1000) {
      this.flushMetrics();
    }
  }

  /**
   * Record API request metric
   */
  recordApiRequest(method: string, route: string, statusCode: number, duration: number): void {
    // Record in Prometheus
    metrics.recordHttpRequest(method, route, statusCode.toString(), duration / 1000);

    // Record custom metric
    this.recordMetric({
      name: 'api_request_duration',
      value: duration,
      type: 'histogram',
      tags: {
        method,
        route,
        status_code: statusCode.toString()
      }
    });

    // Alert on slow requests
    if (duration > 200) { // 200ms threshold
      logger.warn('Slow API request detected', {
        method,
        route,
        statusCode,
        duration
      });
    }
  }

  /**
   * Record database query metric
   */
  recordDatabaseQuery(query: string, table: string, operation: string, duration: number): void {
    // Record in Prometheus
    metrics.recordDatabaseQuery('sql', table, operation, duration / 1000);

    // Record custom metric
    this.recordMetric({
      name: 'database_query_duration',
      value: duration,
      type: 'histogram',
      tags: {
        table,
        operation,
        query_type: 'sql'
      }
    });

    // Alert on slow queries
    if (duration > 100) { // 100ms threshold
      logger.warn('Slow database query detected', {
        table,
        operation,
        duration,
        query: query.substring(0, 100) + '...'
      });
    }
  }

  /**
   * Record RPA execution metric
   */
  recordRPAExecution(drNumber: string, status: string, duration: number, success: boolean): void {
    // Record in Prometheus
    if (success) {
      metrics.recordRPATaskCompleted('audit', 'completed', duration / 1000);
    } else {
      metrics.recordRPAError('execution_failed', 'audit', 'system');
    }

    // Record custom metric
    this.recordMetric({
      name: 'rpa_execution_duration',
      value: duration,
      type: 'histogram',
      tags: {
        status,
        success: success.toString(),
        dr_number: drNumber
      }
    });

    // Alert on slow executions
    if (duration > 30000) { // 30 seconds threshold
      logger.warn('Slow RPA execution detected', {
        drNumber,
        status,
        duration
      });
    }
  }

  /**
   * Collect system metrics
   */
  private async collectSystemMetrics(): Promise<void> {
    try {
      const memUsage = process.memoryUsage();
      const cpuUsage = process.cpuUsage();

      // Record memory metrics
      this.recordMetric({
        name: 'system_memory_usage',
        value: memUsage.heapUsed,
        type: 'gauge',
        tags: { type: 'heap_used' }
      });

      this.recordMetric({
        name: 'system_memory_usage',
        value: memUsage.heapTotal,
        type: 'gauge',
        tags: { type: 'heap_total' }
      });

      this.recordMetric({
        name: 'system_memory_usage',
        value: memUsage.rss,
        type: 'gauge',
        tags: { type: 'rss' }
      });

      // Calculate memory percentage
      const memoryPercentage = (memUsage.heapUsed / memUsage.heapTotal) * 100;
      metrics.setMemoryUsage('heap_percentage', memoryPercentage);

      // Alert on high memory usage
      if (memoryPercentage > 85) {
        logger.error('High memory usage detected', {
          heapUsed: memUsage.heapUsed,
          heapTotal: memUsage.heapTotal,
          percentage: memoryPercentage
        });
      }

      // Record CPU metrics
      const totalCpuTime = cpuUsage.user + cpuUsage.system;
      this.recordMetric({
        name: 'system_cpu_usage',
        value: totalCpuTime,
        type: 'gauge',
        tags: { type: 'total' }
      });

    } catch (error) {
      logger.error('Failed to collect system metrics', {
        error: error instanceof Error ? error.message : error
      });
    }
  }

  /**
   * Get current system metrics
   */
  async getSystemMetrics(): Promise<SystemMetrics> {
    const memUsage = process.memoryUsage();
    const cpuUsage = process.cpuUsage();

    // Simulate disk and network metrics (in real implementation, use system libraries)
    return {
      cpu: {
        usage: Math.min(100, (cpuUsage.user + cpuUsage.system) / 10000), // Convert to percentage
        cores: require('os').cpus().length,
        loadAverage: require('os').loadavg()
      },
      memory: {
        total: memUsage.heapTotal,
        used: memUsage.heapUsed,
        free: memUsage.heapTotal - memUsage.heapUsed,
        percentage: (memUsage.heapUsed / memUsage.heapTotal) * 100
      },
      disk: {
        total: 100 * 1024 * 1024 * 1024, // 100GB simulated
        used: 34 * 1024 * 1024 * 1024,  // 34GB simulated
        free: 66 * 1024 * 1024 * 1024,  // 66GB simulated
        percentage: 34
      },
      network: {
        incoming: 0,
        outgoing: 0
      }
    };
  }

  /**
   * Get database performance metrics
   */
  async getDatabaseMetrics(): Promise<DatabaseMetrics> {
    // This would query the database for actual metrics
    // For now, return simulated data
    return {
      connectionCount: 5,
      queryTimes: {
        average: 45,
        min: 5,
        max: 250,
        p95: 120,
        p99: 180
      },
      slowQueries: []
    };
  }

  /**
   * Get RPA performance metrics
   */
  async getRPAMetrics(): Promise<RPAMetrics> {
    // This would query actual RPA metrics
    // For now, return simulated data
    return {
      activeJobs: 3,
      completedJobs: 247,
      failedJobs: 5,
      averageExecutionTime: 25000,
      successRate: 98,
      queueLength: 12
    };
  }

  /**
   * Flush metrics to database
   */
  private async flushMetrics(): Promise<void> {
    if (this.metricsBuffer.length === 0) return;

    try {
      const metricsToInsert = this.metricsBuffer.splice(0);

      // Insert metrics into database
      await db.insert(performance_metrics).values(
        metricsToInsert.map(metric => ({
          metric_name: metric.name,
          metric_value: metric.value,
          metric_type: metric.type,
          tags: metric.tags || {},
          timestamp: metric.timestamp || new Date()
        }))
      );

      logger.debug(`Flushed ${metricsToInsert.length} metrics to database`);

    } catch (error) {
      logger.error('Failed to flush metrics to database', {
        error: error instanceof Error ? error.message : error,
        metricsCount: this.metricsBuffer.length
      });
    }
  }

  /**
   * Get aggregated metrics for dashboard
   */
  async getAggregatedMetrics(timeRange: '1h' | '24h' | '7d' | '30d' = '1h'): Promise<any> {
    const now = new Date();
    let startTime: Date;

    switch (timeRange) {
      case '1h':
        startTime = new Date(now.getTime() - 60 * 60 * 1000);
        break;
      case '24h':
        startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        break;
      case '7d':
        startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case '30d':
        startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
        break;
    }

    // Query aggregated metrics from database
    const results = await db
      .select({
        name: performance_metrics.metric_name,
        avg: db.raw('AVG(metric_value)'),
        min: db.raw('MIN(metric_value)'),
        max: db.raw('MAX(metric_value)'),
        count: db.raw('COUNT(*)')
      })
      .from(performance_metrics)
      .where(
        db.gte(performance_metrics.timestamp, startTime)
      )
      .groupBy(performance_metrics.metric_name);

    return results;
  }

  /**
   * Stop monitoring
   */
  stop(): void {
    if (this.collectionInterval) {
      clearInterval(this.collectionInterval);
    }
    if (this.systemMetricsInterval) {
      clearInterval(this.systemMetricsInterval);
    }

    // Flush remaining metrics
    this.flushMetrics();

    this.isCollecting = false;
    logger.info('Performance monitoring stopped');
  }
}

// Export singleton instance
export const performanceMonitor = new PerformanceMonitor();

// Initialize on module load
if (process.env.NODE_ENV !== 'test') {
  performanceMonitor.initialize().catch(error => {
    logger.error('Failed to initialize performance monitor', { error });
  });
}

export default performanceMonitor;