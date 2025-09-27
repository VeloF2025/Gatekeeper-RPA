/**
 * Real-time Performance Metrics Collector
 *
 * Provides comprehensive performance monitoring with real-time collection,
 * aggregation, and broadcasting capabilities for the Gatekeeper RPA system.
 */

import { EventEmitter } from 'events';

export interface PerformanceMetrics {
  timestamp: number;
  system: {
    cpu: {
      usage: number;
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
      bytesIn: number;
      bytesOut: number;
      packetsIn: number;
      packetsOut: number;
    };
  };
  application: {
    uptime: number;
    memoryUsage: {
      rss: number;
      heapTotal: number;
      heapUsed: number;
      external: number;
    };
    eventLoop: {
      lag: number;
      handles: number;
      requests: number;
    };
  };
  database: {
    connections: number;
    maxConnections: number;
    queryCount: number;
    slowQueries: number;
    avgQueryTime: number;
  };
  rpa: {
    activeJobs: number;
    completedJobs: number;
    failedJobs: number;
    avgExecutionTime: number;
    successRate: number;
    throughput: number;
  };
}

export interface PerformanceAlert {
  id: string;
  type: 'warning' | 'error' | 'critical';
  category: 'system' | 'application' | 'database' | 'rpa';
  message: string;
  value: number;
  threshold: number;
  timestamp: number;
  resolved: boolean;
}

export class PerformanceCollector extends EventEmitter {
  private metrics: PerformanceMetrics;
  private alerts: Map<string, PerformanceAlert> = new Map();
  private intervals: NodeJS.Timeout[] = [];
  private isCollecting: boolean = false;
  private subscribers: Set<(metrics: PerformanceMetrics) => void> = new Set();

  // Performance thresholds
  private thresholds = {
    cpu: { warning: 70, critical: 90 },
    memory: { warning: 80, critical: 95 },
    disk: { warning: 80, critical: 95 },
    responseTime: { warning: 1000, critical: 5000 },
    errorRate: { warning: 5, critical: 10 },
    databaseConnections: { warning: 80, critical: 95 }
  };

  constructor() {
    super();
    this.metrics = this.initializeMetrics();
  }

  private initializeMetrics(): PerformanceMetrics {
    return {
      timestamp: Date.now(),
      system: {
        cpu: { usage: 0, loadAverage: [0, 0, 0] },
        memory: { total: 0, used: 0, free: 0, percentage: 0 },
        disk: { total: 0, used: 0, free: 0, percentage: 0 },
        network: { bytesIn: 0, bytesOut: 0, packetsIn: 0, packetsOut: 0 }
      },
      application: {
        uptime: 0,
        memoryUsage: { rss: 0, heapTotal: 0, heapUsed: 0, external: 0 },
        eventLoop: { lag: 0, handles: 0, requests: 0 }
      },
      database: {
        connections: 0,
        maxConnections: 100,
        queryCount: 0,
        slowQueries: 0,
        avgQueryTime: 0
      },
      rpa: {
        activeJobs: 0,
        completedJobs: 0,
        failedJobs: 0,
        avgExecutionTime: 0,
        successRate: 100,
        throughput: 0
      }
    };
  }

  /**
   * Start collecting performance metrics
   */
  public start(): void {
    if (this.isCollecting) {
      return;
    }

    this.isCollecting = true;

    // Collect system metrics every 5 seconds
    this.intervals.push(setInterval(() => this.collectSystemMetrics(), 5000));

    // Collect application metrics every 2 seconds
    this.intervals.push(setInterval(() => this.collectApplicationMetrics(), 2000));

    // Collect database metrics every 10 seconds
    this.intervals.push(setInterval(() => this.collectDatabaseMetrics(), 10000));

    // Collect RPA metrics every 5 seconds
    this.intervals.push(setInterval(() => this.collectRPAMetrics(), 5000));

    // Check for alerts every 30 seconds
    this.intervals.push(setInterval(() => this.checkAlerts(), 30000));

    // Broadcast metrics to subscribers every 5 seconds
    this.intervals.push(setInterval(() => this.broadcastMetrics(), 5000));

    // Initial collection
    this.collectAllMetrics();

    console.log('🔍 Performance metrics collection started');
  }

  /**
   * Stop collecting performance metrics
   */
  public stop(): void {
    if (!this.isCollecting) {
      return;
    }

    this.isCollecting = false;

    this.intervals.forEach(interval => clearInterval(interval));
    this.intervals = [];

    console.log('🛑 Performance metrics collection stopped');
  }

  /**
   * Subscribe to real-time metrics updates
   */
  public subscribe(callback: (metrics: PerformanceMetrics) => void): () => void {
    this.subscribers.add(callback);

    // Return unsubscribe function
    return () => {
      this.subscribers.delete(callback);
    };
  }

  /**
   * Get current metrics snapshot
   */
  public getCurrentMetrics(): PerformanceMetrics {
    return { ...this.metrics };
  }

  /**
   * Get active alerts
   */
  public getActiveAlerts(): PerformanceAlert[] {
    return Array.from(this.alerts.values()).filter(alert => !alert.resolved);
  }

  /**
   * Get metrics history (last 24 hours by default)
   */
  public getMetricsHistory(hours: number = 24): PerformanceMetrics[] {
    // In a real implementation, this would query a time-series database
    // For now, return current metrics
    return [this.metrics];
  }

  /**
   * Collect all metrics
   */
  private async collectAllMetrics(): Promise<void> {
    await Promise.all([
      this.collectSystemMetrics(),
      this.collectApplicationMetrics(),
      this.collectDatabaseMetrics(),
      this.collectRPAMetrics()
    ]);
  }

  /**
   * Collect system metrics
   */
  private async collectSystemMetrics(): Promise<void> {
    try {
      // Simulated system metrics collection
      // In a real implementation, you would use system-monitoring libraries
      const cpuUsage = Math.random() * 100;
      const memoryUsage = process.memoryUsage();

      this.metrics.system.cpu = {
        usage: cpuUsage,
        loadAverage: [cpuUsage / 3, cpuUsage / 4, cpuUsage / 5]
      };

      this.metrics.system.memory = {
        total: memoryUsage.heapTotal,
        used: memoryUsage.heapUsed,
        free: memoryUsage.heapTotal - memoryUsage.heapUsed,
        percentage: (memoryUsage.heapUsed / memoryUsage.heapTotal) * 100
      };

      this.metrics.timestamp = Date.now();
    } catch (error) {
      console.error('Error collecting system metrics:', error);
    }
  }

  /**
   * Collect application metrics
   */
  private async collectApplicationMetrics(): Promise<void> {
    try {
      const memoryUsage = process.memoryUsage();
      const uptime = process.uptime();

      this.metrics.application = {
        uptime,
        memoryUsage: {
          rss: memoryUsage.rss,
          heapTotal: memoryUsage.heapTotal,
          heapUsed: memoryUsage.heapUsed,
          external: memoryUsage.external
        },
        eventLoop: {
          lag: Math.random() * 100, // Simulated event loop lag
          handles: process.getActiveHandles().length,
          requests: process.getActiveRequests().length
        }
      };
    } catch (error) {
      console.error('Error collecting application metrics:', error);
    }
  }

  /**
   * Collect database metrics
   */
  private async collectDatabaseMetrics(): Promise<void> {
    try {
      // In a real implementation, you would query the database for actual metrics
      // For now, use simulated values
      this.metrics.database = {
        connections: Math.floor(Math.random() * 20),
        maxConnections: 100,
        queryCount: Math.floor(Math.random() * 1000),
        slowQueries: Math.floor(Math.random() * 10),
        avgQueryTime: Math.random() * 100
      };
    } catch (error) {
      console.error('Error collecting database metrics:', error);
    }
  }

  /**
   * Collect RPA metrics
   */
  private async collectRPAMetrics(): Promise<void> {
    try {
      // In a real implementation, you would query the RPA service for actual metrics
      // For now, use simulated values
      const totalJobs = Math.floor(Math.random() * 100);
      const completedJobs = Math.floor(totalJobs * 0.8);
      const failedJobs = Math.floor(totalJobs * 0.1);

      this.metrics.rpa = {
        activeJobs: Math.floor(Math.random() * 10),
        completedJobs,
        failedJobs,
        avgExecutionTime: Math.random() * 30000,
        successRate: totalJobs > 0 ? (completedJobs / totalJobs) * 100 : 100,
        throughput: Math.random() * 10
      };
    } catch (error) {
      console.error('Error collecting RPA metrics:', error);
    }
  }

  /**
   * Check for performance alerts
   */
  private checkAlerts(): void {
    const alerts: PerformanceAlert[] = [];

    // Check CPU usage
    if (this.metrics.system.cpu.usage > this.thresholds.cpu.critical) {
      alerts.push({
        id: `cpu-${Date.now()}`,
        type: 'critical',
        category: 'system',
        message: `CPU usage critical: ${this.metrics.system.cpu.usage.toFixed(1)}%`,
        value: this.metrics.system.cpu.usage,
        threshold: this.thresholds.cpu.critical,
        timestamp: Date.now(),
        resolved: false
      });
    } else if (this.metrics.system.cpu.usage > this.thresholds.cpu.warning) {
      alerts.push({
        id: `cpu-${Date.now()}`,
        type: 'warning',
        category: 'system',
        message: `CPU usage high: ${this.metrics.system.cpu.usage.toFixed(1)}%`,
        value: this.metrics.system.cpu.usage,
        threshold: this.thresholds.cpu.warning,
        timestamp: Date.now(),
        resolved: false
      });
    }

    // Check memory usage
    if (this.metrics.system.memory.percentage > this.thresholds.memory.critical) {
      alerts.push({
        id: `memory-${Date.now()}`,
        type: 'critical',
        category: 'system',
        message: `Memory usage critical: ${this.metrics.system.memory.percentage.toFixed(1)}%`,
        value: this.metrics.system.memory.percentage,
        threshold: this.thresholds.memory.critical,
        timestamp: Date.now(),
        resolved: false
      });
    } else if (this.metrics.system.memory.percentage > this.thresholds.memory.warning) {
      alerts.push({
        id: `memory-${Date.now()}`,
        type: 'warning',
        category: 'system',
        message: `Memory usage high: ${this.metrics.system.memory.percentage.toFixed(1)}%`,
        value: this.metrics.system.memory.percentage,
        threshold: this.thresholds.memory.warning,
        timestamp: Date.now(),
        resolved: false
      });
    }

    // Check RPA success rate
    if (this.metrics.rpa.successRate < (100 - this.thresholds.errorRate.critical)) {
      alerts.push({
        id: `rpa-success-${Date.now()}`,
        type: 'critical',
        category: 'rpa',
        message: `RPA success rate critical: ${this.metrics.rpa.successRate.toFixed(1)}%`,
        value: this.metrics.rpa.successRate,
        threshold: this.thresholds.errorRate.critical,
        timestamp: Date.now(),
        resolved: false
      });
    } else if (this.metrics.rpa.successRate < (100 - this.thresholds.errorRate.warning)) {
      alerts.push({
        id: `rpa-success-${Date.now()}`,
        type: 'warning',
        category: 'rpa',
        message: `RPA success rate low: ${this.metrics.rpa.successRate.toFixed(1)}%`,
        value: this.metrics.rpa.successRate,
        threshold: this.thresholds.errorRate.warning,
        timestamp: Date.now(),
        resolved: false
      });
    }

    // Add alerts to collection
    alerts.forEach(alert => {
      this.alerts.set(alert.id, alert);
      this.emit('alert', alert);
    });

    // Auto-resolve old alerts (after 1 hour)
    const oneHourAgo = Date.now() - 60 * 60 * 1000;
    this.alerts.forEach((alert, id) => {
      if (!alert.resolved && alert.timestamp < oneHourAgo) {
        alert.resolved = true;
        this.emit('alertResolved', alert);
      }
    });
  }

  /**
   * Broadcast metrics to subscribers
   */
  private broadcastMetrics(): void {
    if (this.subscribers.size > 0) {
      const metrics = this.getCurrentMetrics();
      this.subscribers.forEach(callback => {
        try {
          callback(metrics);
        } catch (error) {
          console.error('Error in metrics subscriber callback:', error);
        }
      });
    }
  }

  /**
   * Generate performance report
   */
  public generateReport(): {
    summary: {
      overallHealth: 'excellent' | 'good' | 'fair' | 'poor';
      uptime: number;
      totalAlerts: number;
      activeAlerts: number;
    };
    metrics: PerformanceMetrics;
    alerts: PerformanceAlert[];
    recommendations: string[];
  } {
    const activeAlerts = this.getActiveAlerts();
    const criticalAlerts = activeAlerts.filter(alert => alert.type === 'critical');

    let overallHealth: 'excellent' | 'good' | 'fair' | 'poor' = 'excellent';
    if (criticalAlerts.length > 0) {
      overallHealth = 'poor';
    } else if (activeAlerts.length > 2) {
      overallHealth = 'fair';
    } else if (activeAlerts.length > 0) {
      overallHealth = 'good';
    }

    const recommendations: string[] = [];

    if (this.metrics.system.cpu.usage > 70) {
      recommendations.push('Consider scaling up CPU resources or optimizing CPU-intensive tasks');
    }

    if (this.metrics.system.memory.percentage > 80) {
      recommendations.push('Monitor memory usage and consider adding more RAM');
    }

    if (this.metrics.rpa.successRate < 95) {
      recommendations.push('Investigate RPA job failures and improve error handling');
    }

    if (this.metrics.database.avgQueryTime > 100) {
      recommendations.push('Optimize database queries and consider adding indexes');
    }

    return {
      summary: {
        overallHealth,
        uptime: this.metrics.application.uptime,
        totalAlerts: this.alerts.size,
        activeAlerts: activeAlerts.length
      },
      metrics: this.metrics,
      alerts: activeAlerts,
      recommendations
    };
  }
}

// Global instance
export const performanceCollector = new PerformanceCollector();

// Auto-start in production
if (process.env.NODE_ENV === 'production') {
  performanceCollector.start();
}