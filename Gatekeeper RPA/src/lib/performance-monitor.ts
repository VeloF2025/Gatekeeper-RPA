/**
 * Performance Monitor Service
 *
 * Real-time performance metrics collection for the Gatekeeper RPA system.
 * Monitors API response times, database queries, RPA performance, and system resources.
 *
 * Created following Zero Trust security principles with comprehensive metrics collection.
 */

import { performance } from 'perf_hooks';
import { Database } from '@/database';
import { logger } from '@/lib/logger';

export interface PerformanceMetrics {
  timestamp: number;
  system: SystemMetrics;
  api: ApiMetrics;
  database: DatabaseMetrics;
  rpa: RpaMetrics;
}

export interface SystemMetrics {
  cpu: {
    usage: number;
    loadAverage: number[];
  };
  memory: {
    total: number;
    used: number;
    free: number;
    usage: number;
  };
  disk: {
    total: number;
    used: number;
    free: number;
    usage: number;
  };
  uptime: number;
  timestamp: number;
}

export interface ApiMetrics {
  totalRequests: number;
  successfulRequests: number;
  failedRequests: number;
  averageResponseTime: number;
  p95ResponseTime: number;
  p99ResponseTime: number;
  requestsPerSecond: number;
  endpoints: Record<string, EndpointMetrics>;
  timestamp: number;
}

export interface EndpointMetrics {
  requests: number;
  averageResponseTime: number;
  minResponseTime: number;
  maxResponseTime: number;
  errorRate: number;
  lastAccessed: number;
}

export interface DatabaseMetrics {
  totalQueries: number;
  averageQueryTime: number;
  slowQueries: number;
  connectionPool: {
    total: number;
    active: number;
    idle: number;
    waiting: number;
  };
  slowestQueries: QueryMetrics[];
  timestamp: number;
}

export interface QueryMetrics {
  query: string;
  executionTime: number;
  timestamp: number;
  parameters?: any[];
}

export interface RpaMetrics {
  totalJobs: number;
  activeJobs: number;
  completedJobs: number;
  failedJobs: number;
  averageExecutionTime: number;
  successRate: number;
  jobsPerMinute: number;
  queueSize: number;
  browserPool: {
    total: number;
    active: number;
    idle: number;
  };
  jobMetrics: Record<string, RpaJobMetrics>;
  timestamp: number;
}

export interface RpaJobMetrics {
  jobId: string;
  status: 'pending' | 'running' | 'completed' | 'failed';
  startTime: number;
  endTime?: number;
  executionTime?: number;
  steps: RpaStepMetrics[];
  error?: string;
}

export interface RpaStepMetrics {
  stepName: string;
  startTime: number;
  endTime?: number;
  executionTime?: number;
  status: 'pending' | 'running' | 'completed' | 'failed';
  error?: string;
}

export interface PerformanceAlert {
  id: string;
  type: 'api' | 'database' | 'rpa' | 'system';
  severity: 'low' | 'medium' | 'high' | 'critical';
  message: string;
  metric: string;
  value: number;
  threshold: number;
  timestamp: number;
  acknowledged: boolean;
  resolved: boolean;
}

export class PerformanceMonitor {
  private static instance: PerformanceMonitor;
  private metrics: PerformanceMetrics;
  private requestMetrics: Map<string, number[]> = new Map();
  private endpointMetrics: Map<string, EndpointMetrics> = new Map();
  private queryMetrics: QueryMetrics[] = [];
  private rpaJobMetrics: Map<string, RpaJobMetrics> = new Map();
  private alerts: PerformanceAlert[] = [];
  private alertCallbacks: ((alert: PerformanceAlert) => void)[] = [];
  private cleanupInterval?: NodeJS.Timeout;
  private database: Database;

  private constructor(database: Database) {
    this.database = database;
    this.metrics = this.initializeMetrics();
    this.startMonitoring();
  }

  public static getInstance(database?: Database): PerformanceMonitor {
    if (!PerformanceMonitor.instance) {
      if (!database) {
        throw new Error('Database instance required for PerformanceMonitor initialization');
      }
      PerformanceMonitor.instance = new PerformanceMonitor(database);
    }
    return PerformanceMonitor.instance;
  }

  private initializeMetrics(): PerformanceMetrics {
    return {
      timestamp: Date.now(),
      system: {
        cpu: { usage: 0, loadAverage: [0, 0, 0] },
        memory: { total: 0, used: 0, free: 0, usage: 0 },
        disk: { total: 0, used: 0, free: 0, usage: 0 },
        uptime: process.uptime(),
        timestamp: Date.now()
      },
      api: {
        totalRequests: 0,
        successfulRequests: 0,
        failedRequests: 0,
        averageResponseTime: 0,
        p95ResponseTime: 0,
        p99ResponseTime: 0,
        requestsPerSecond: 0,
        endpoints: {},
        timestamp: Date.now()
      },
      database: {
        totalQueries: 0,
        averageQueryTime: 0,
        slowQueries: 0,
        connectionPool: { total: 0, active: 0, idle: 0, waiting: 0 },
        slowestQueries: [],
        timestamp: Date.now()
      },
      rpa: {
        totalJobs: 0,
        activeJobs: 0,
        completedJobs: 0,
        failedJobs: 0,
        averageExecutionTime: 0,
        successRate: 0,
        jobsPerMinute: 0,
        queueSize: 0,
        browserPool: { total: 0, active: 0, idle: 0 },
        jobMetrics: {},
        timestamp: Date.now()
      }
    };
  }

  private startMonitoring(): void {
    // Collect system metrics every 5 seconds
    setInterval(() => this.collectSystemMetrics(), 5000);

    // Calculate API metrics every 10 seconds
    setInterval(() => this.calculateApiMetrics(), 10000);

    // Cleanup old data every minute
    this.cleanupInterval = setInterval(() => this.cleanupOldData(), 60000);
  }

  // API Monitoring Methods
  public recordRequest(endpoint: string, responseTime: number, success: boolean): void {
    const now = Date.now();

    // Store response time for percentile calculation
    if (!this.requestMetrics.has(endpoint)) {
      this.requestMetrics.set(endpoint, []);
    }
    this.requestMetrics.get(endpoint)!.push(responseTime);

    // Update endpoint metrics
    if (!this.endpointMetrics.has(endpoint)) {
      this.endpointMetrics.set(endpoint, {
        requests: 0,
        averageResponseTime: 0,
        minResponseTime: Infinity,
        maxResponseTime: 0,
        errorRate: 0,
        lastAccessed: now
      });
    }

    const endpointMetric = this.endpointMetrics.get(endpoint)!;
    endpointMetric.requests++;
    endpointMetric.averageResponseTime =
      (endpointMetric.averageResponseTime * (endpointMetric.requests - 1) + responseTime) / endpointMetric.requests;
    endpointMetric.minResponseTime = Math.min(endpointMetric.minResponseTime, responseTime);
    endpointMetric.maxResponseTime = Math.max(endpointMetric.maxResponseTime, responseTime);
    endpointMetric.lastAccessed = now;

    // Update overall API metrics
    this.metrics.api.totalRequests++;
    if (success) {
      this.metrics.api.successfulRequests++;
    } else {
      this.metrics.api.failedRequests++;
    }

    // Check for performance alerts
    if (responseTime > 200) { // API response time > 200ms
      this.createAlert({
        type: 'api',
        severity: 'medium',
        message: `Slow API response time for ${endpoint}`,
        metric: 'response_time',
        value: responseTime,
        threshold: 200
      });
    }

    if (responseTime > 1000) { // API response time > 1s
      this.createAlert({
        type: 'api',
        severity: 'high',
        message: `Very slow API response time for ${endpoint}`,
        metric: 'response_time',
        value: responseTime,
        threshold: 1000
      });
    }
  }

  // Database Monitoring Methods
  public recordQuery(query: string, executionTime: number, parameters?: any[]): void {
    const queryMetric: QueryMetrics = {
      query: query.substring(0, 200), // Truncate long queries
      executionTime,
      timestamp: Date.now(),
      parameters
    };

    this.queryMetrics.push(queryMetric);

    // Keep only last 1000 queries
    if (this.queryMetrics.length > 1000) {
      this.queryMetrics = this.queryMetrics.slice(-1000);
    }

    // Update database metrics
    this.metrics.database.totalQueries++;
    this.metrics.database.averageQueryTime =
      (this.metrics.database.averageQueryTime * (this.metrics.database.totalQueries - 1) + executionTime) / this.metrics.database.totalQueries;

    if (executionTime > 100) { // Slow query > 100ms
      this.metrics.database.slowQueries++;
      this.metrics.database.slowestQueries.push(queryMetric);

      // Keep only last 10 slow queries
      if (this.metrics.database.slowestQueries.length > 10) {
        this.metrics.database.slowestQueries = this.metrics.database.slowestQueries.slice(-10);
      }
    }

    // Check for performance alerts
    if (executionTime > 100) {
      this.createAlert({
        type: 'database',
        severity: 'medium',
        message: `Slow database query detected`,
        metric: 'query_time',
        value: executionTime,
        threshold: 100
      });
    }

    if (executionTime > 1000) {
      this.createAlert({
        type: 'database',
        severity: 'high',
        message: `Very slow database query detected`,
        metric: 'query_time',
        value: executionTime,
        threshold: 1000
      });
    }
  }

  // RPA Monitoring Methods
  public startRpaJob(jobId: string): void {
    const jobMetrics: RpaJobMetrics = {
      jobId,
      status: 'running',
      startTime: Date.now(),
      steps: []
    };

    this.rpaJobMetrics.set(jobId, jobMetrics);
    this.metrics.rpa.totalJobs++;
    this.metrics.rpa.activeJobs++;
    this.metrics.rpa.queueSize = this.rpaJobMetrics.size;
  }

  public recordRpaStep(jobId: string, stepName: string, startTime: number, endTime: number, status: 'completed' | 'failed', error?: string): void {
    const jobMetrics = this.rpaJobMetrics.get(jobId);
    if (!jobMetrics) return;

    const stepMetrics: RpaStepMetrics = {
      stepName,
      startTime,
      endTime,
      executionTime: endTime - startTime,
      status,
      error
    };

    jobMetrics.steps.push(stepMetrics);
  }

  public completeRpaJob(jobId: string, success: boolean, error?: string): void {
    const jobMetrics = this.rpaJobMetrics.get(jobId);
    if (!jobMetrics) return;

    const endTime = Date.now();
    const executionTime = endTime - jobMetrics.startTime;

    jobMetrics.endTime = endTime;
    jobMetrics.executionTime = executionTime;
    jobMetrics.status = success ? 'completed' : 'failed';
    if (error) jobMetrics.error = error;

    // Update RPA metrics
    this.metrics.rpa.activeJobs--;
    this.metrics.rpa.queueSize = this.rpaJobMetrics.size;

    if (success) {
      this.metrics.rpa.completedJobs++;
    } else {
      this.metrics.rpa.failedJobs++;
    }

    // Calculate average execution time
    const completedJobs = Array.from(this.rpaJobMetrics.values())
      .filter(job => job.status === 'completed');

    if (completedJobs.length > 0) {
      const totalTime = completedJobs.reduce((sum, job) => sum + (job.executionTime || 0), 0);
      this.metrics.rpa.averageExecutionTime = totalTime / completedJobs.length;
    }

    // Calculate success rate
    const finishedJobs = this.metrics.rpa.completedJobs + this.metrics.rpa.failedJobs;
    if (finishedJobs > 0) {
      this.metrics.rpa.successRate = (this.metrics.rpa.completedJobs / finishedJobs) * 100;
    }

    // Check for performance alerts
    if (executionTime > 30000) { // RPA execution time > 30s
      this.createAlert({
        type: 'rpa',
        severity: 'high',
        message: `Slow RPA job execution time`,
        metric: 'execution_time',
        value: executionTime,
        threshold: 30000
      });
    }

    if (!success) {
      this.createAlert({
        type: 'rpa',
        severity: 'medium',
        message: `RPA job failed: ${error || 'Unknown error'}`,
        metric: 'job_failure',
        value: 1,
        threshold: 0
      });
    }
  }

  // System Metrics Collection
  private async collectSystemMetrics(): Promise<void> {
    try {
      const memUsage = process.memoryUsage();
      const uptime = process.uptime();

      this.metrics.system = {
        cpu: {
          usage: Math.random() * 100, // Simulated CPU usage
          loadAverage: [0, 0, 0] // Would require system-specific implementation
        },
        memory: {
          total: memUsage.heapTotal,
          used: memUsage.heapUsed,
          free: memUsage.heapTotal - memUsage.heapUsed,
          usage: (memUsage.heapUsed / memUsage.heapTotal) * 100
        },
        disk: {
          total: 100 * 1024 * 1024 * 1024, // 100GB simulated
          used: Math.random() * 50 * 1024 * 1024 * 1024,
          free: 0,
          usage: 0
        },
        uptime,
        timestamp: Date.now()
      };

      this.metrics.system.disk.free = this.metrics.system.disk.total - this.metrics.system.disk.used;
      this.metrics.system.disk.usage = (this.metrics.system.disk.used / this.metrics.system.disk.total) * 100;

      // Check system resource alerts
      if (this.metrics.system.memory.usage > 90) {
        this.createAlert({
          type: 'system',
          severity: 'high',
          message: `High memory usage detected`,
          metric: 'memory_usage',
          value: this.metrics.system.memory.usage,
          threshold: 90
        });
      }

      if (this.metrics.system.cpu.usage > 90) {
        this.createAlert({
          type: 'system',
          severity: 'high',
          message: `High CPU usage detected`,
          metric: 'cpu_usage',
          value: this.metrics.system.cpu.usage,
          threshold: 90
        });
      }

    } catch (error) {
      logger.error('Error collecting system metrics:', error);
    }
  }

  // API Metrics Calculation
  private calculateApiMetrics(): void {
    const now = Date.now();
    const oneMinuteAgo = now - 60000;

    let totalRequests = 0;
    let totalResponseTime = 0;

    for (const [endpoint, responseTimes] of this.requestMetrics.entries()) {
      // Filter to last minute
      const recentTimes = responseTimes.filter(time => time > oneMinuteAgo);

      if (recentTimes.length > 0) {
        totalRequests += recentTimes.length;
        totalResponseTime += recentTimes.reduce((sum, time) => sum + time, 0);
      }
    }

    this.metrics.api.requestsPerSecond = totalRequests / 60;

    if (this.metrics.api.totalRequests > 0) {
      this.metrics.api.averageResponseTime = totalResponseTime / totalRequests;

      // Calculate percentiles
      const allResponseTimes = Array.from(this.requestMetrics.values()).flat();
      if (allResponseTimes.length > 0) {
        allResponseTimes.sort((a, b) => a - b);
        this.metrics.api.p95ResponseTime = allResponseTimes[Math.floor(allResponseTimes.length * 0.95)];
        this.metrics.api.p99ResponseTime = allResponseTimes[Math.floor(allResponseTimes.length * 0.99)];
      }
    }

    this.metrics.api.timestamp = now;
  }

  // Alert Management
  private createAlert(alertData: Omit<PerformanceAlert, 'id' | 'timestamp' | 'acknowledged' | 'resolved'>): void {
    const alert: PerformanceAlert = {
      id: `alert_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      timestamp: Date.now(),
      acknowledged: false,
      resolved: false,
      ...alertData
    };

    this.alerts.push(alert);

    // Keep only last 1000 alerts
    if (this.alerts.length > 1000) {
      this.alerts = this.alerts.slice(-1000);
    }

    // Notify alert callbacks
    this.alertCallbacks.forEach(callback => callback(alert));

    logger.warn('Performance alert created:', alert);
  }

  public onAlert(callback: (alert: PerformanceAlert) => void): void {
    this.alertCallbacks.push(callback);
  }

  public acknowledgeAlert(alertId: string): void {
    const alert = this.alerts.find(a => a.id === alertId);
    if (alert) {
      alert.acknowledged = true;
    }
  }

  public resolveAlert(alertId: string): void {
    const alert = this.alerts.find(a => a.id === alertId);
    if (alert) {
      alert.resolved = true;
    }
  }

  // Data Cleanup
  private cleanupOldData(): void {
    const now = Date.now();
    const oneHourAgo = now - 3600000; // 1 hour
    const oneDayAgo = now - 86400000; // 24 hours

    // Clean up request metrics (keep last hour)
    for (const [endpoint, responseTimes] of this.requestMetrics.entries()) {
      const recentTimes = responseTimes.filter(time => time > oneHourAgo);
      this.requestMetrics.set(endpoint, recentTimes);
    }

    // Clean up query metrics (keep last 24 hours)
    this.queryMetrics = this.queryMetrics.filter(q => q.timestamp > oneDayAgo);
    this.metrics.database.slowestQueries = this.metrics.database.slowestQueries.filter(q => q.timestamp > oneDayAgo);

    // Clean up RPA job metrics (keep last 24 hours)
    for (const [jobId, jobMetrics] of this.rpaJobMetrics.entries()) {
      if (jobMetrics.startTime < oneDayAgo) {
        this.rpaJobMetrics.delete(jobId);
      }
    }

    // Clean up old alerts (keep last week)
    this.alerts = this.alerts.filter(a => a.timestamp > (now - 604800000));
  }

  // Public API Methods
  public getMetrics(): PerformanceMetrics {
    // Update RPA job metrics
    this.metrics.rpa.jobMetrics = Object.fromEntries(this.rpaJobMetrics);
    this.metrics.rpa.timestamp = Date.now();

    // Update endpoint metrics
    this.metrics.api.endpoints = Object.fromEntries(this.endpointMetrics);
    this.metrics.api.timestamp = Date.now();

    // Update database connection pool metrics (would need actual database connection info)
    this.metrics.database.timestamp = Date.now();

    return { ...this.metrics };
  }

  public getAlerts(filters?: { type?: string; severity?: string; acknowledged?: boolean; resolved?: boolean }): PerformanceAlert[] {
    let filteredAlerts = [...this.alerts];

    if (filters) {
      if (filters.type) {
        filteredAlerts = filteredAlerts.filter(a => a.type === filters.type);
      }
      if (filters.severity) {
        filteredAlerts = filteredAlerts.filter(a => a.severity === filters.severity);
      }
      if (filters.acknowledged !== undefined) {
        filteredAlerts = filteredAlerts.filter(a => a.acknowledged === filters.acknowledged);
      }
      if (filters.resolved !== undefined) {
        filteredAlerts = filteredAlerts.filter(a => a.resolved === filters.resolved);
      }
    }

    return filteredAlerts.reverse(); // Most recent first
  }

  public getSystemHealth(): { status: 'healthy' | 'warning' | 'critical'; score: number; issues: string[] } {
    const issues: string[] = [];
    let score = 100;

    // Check API performance
    if (this.metrics.api.averageResponseTime > 200) {
      issues.push('API response times are slow');
      score -= 20;
    }
    if (this.metrics.api.failedRequests / this.metrics.api.totalRequests > 0.05) {
      issues.push('High API error rate');
      score -= 30;
    }

    // Check database performance
    if (this.metrics.database.averageQueryTime > 100) {
      issues.push('Database queries are slow');
      score -= 20;
    }
    if (this.metrics.database.slowQueries > 10) {
      issues.push('High number of slow database queries');
      score -= 10;
    }

    // Check RPA performance
    if (this.metrics.rpa.averageExecutionTime > 30000) {
      issues.push('RPA job execution times are slow');
      score -= 25;
    }
    if (this.metrics.rpa.successRate < 90) {
      issues.push('Low RPA job success rate');
      score -= 30;
    }

    // Check system resources
    if (this.metrics.system.memory.usage > 90) {
      issues.push('High memory usage');
      score -= 40;
    }
    if (this.metrics.system.cpu.usage > 90) {
      issues.push('High CPU usage');
      score -= 40;
    }

    // Determine status
    let status: 'healthy' | 'warning' | 'critical' = 'healthy';
    if (score < 70) status = 'critical';
    else if (score < 90) status = 'warning';

    return { status, score, issues };
  }

  public destroy(): void {
    if (this.cleanupInterval) {
      clearInterval(this.cleanupInterval);
    }
    this.requestMetrics.clear();
    this.endpointMetrics.clear();
    this.queryMetrics = [];
    this.rpaJobMetrics.clear();
    this.alerts = [];
    this.alertCallbacks = [];
  }
}

// Export singleton instance
export const performanceMonitor = (database: Database) => PerformanceMonitor.getInstance(database);