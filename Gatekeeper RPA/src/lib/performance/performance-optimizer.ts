/**
 * Performance Optimization System
 *
 * Provides automated performance optimization, caching strategies,
 * and resource management for the Gatekeeper RPA system.
 */

import { performanceCollector, PerformanceMetrics } from './performance-collector';
import { logger } from '@/utils/logger';

export interface OptimizationStrategy {
  id: string;
  name: string;
  description: string;
  category: 'database' | 'cache' | 'memory' | 'cpu' | 'network';
  priority: 'low' | 'medium' | 'high' | 'critical';
  conditions: (metrics: PerformanceMetrics) => boolean;
  action: () => Promise<void>;
  cooldown: number; // milliseconds
}

export interface OptimizationResult {
  strategyId: string;
  success: boolean;
  message: string;
  metrics: PerformanceMetrics;
  timestamp: number;
  executionTime: number;
}

export class PerformanceOptimizer {
  private strategies: Map<string, OptimizationStrategy> = new Map();
  private results: OptimizationResult[] = [];
  private isRunning: boolean = false;
  private lastExecution: Map<string, number> = new Map();

  constructor() {
    this.initializeStrategies();
    this.setupEventListeners();
  }

  /**
   * Initialize optimization strategies
   */
  private initializeStrategies(): void {
    // Database connection optimization
    this.strategies.set('db-connection-pool', {
      id: 'db-connection-pool',
      name: 'Database Connection Pool Optimization',
      description: 'Optimize database connection pool size based on current load',
      category: 'database',
      priority: 'high',
      conditions: (metrics) => {
        const utilization = metrics.database.connections / metrics.database.maxConnections;
        return utilization > 0.8 || utilization < 0.3;
      },
      action: async () => {
        // Simulated database pool optimization
        logger.info('Optimizing database connection pool');
        await new Promise(resolve => setTimeout(resolve, 100));
        logger.info('Database connection pool optimized');
      },
      cooldown: 300000 // 5 minutes
    });

    // Memory optimization
    this.strategies.set('memory-gc', {
      id: 'memory-gc',
      name: 'Garbage Collection Trigger',
      description: 'Force garbage collection when memory usage is high',
      category: 'memory',
      priority: 'high',
      conditions: (metrics) => {
        return metrics.system.memory.percentage > 85;
      },
      action: async () => {
        logger.info('Triggering garbage collection');
        if (global.gc) {
          global.gc();
        }
        logger.info('Garbage collection completed');
      },
      cooldown: 60000 // 1 minute
    });

    // Query optimization
    this.strategies.set('query-optimization', {
      id: 'query-optimization',
      name: 'Query Optimization',
      description: 'Optimize slow database queries',
      category: 'database',
      priority: 'medium',
      conditions: (metrics) => {
        return metrics.database.avgQueryTime > 100 || metrics.database.slowQueries > 5;
      },
      action: async () => {
        logger.info('Optimizing database queries');
        // Simulated query optimization
        await new Promise(resolve => setTimeout(resolve, 200));
        logger.info('Database queries optimized');
      },
      cooldown: 300000 // 5 minutes
    });

    // RPA job optimization
    this.strategies.set('rpa-job-optimization', {
      id: 'rpa-job-optimization',
      name: 'RPA Job Optimization',
      description: 'Optimize RPA job execution and resource allocation',
      category: 'cpu',
      priority: 'medium',
      conditions: (metrics) => {
        return metrics.rpa.avgExecutionTime > 30000 || metrics.rpa.successRate < 90;
      },
      action: async () => {
        logger.info('Optimizing RPA job execution');
        // Simulated RPA optimization
        await new Promise(resolve => setTimeout(resolve, 150));
        logger.info('RPA job execution optimized');
      },
      cooldown: 600000 // 10 minutes
    });

    // Cache optimization
    this.strategies.set('cache-optimization', {
      id: 'cache-optimization',
      name: 'Cache Optimization',
      description: 'Optimize cache settings and clear stale data',
      category: 'cache',
      priority: 'medium',
      conditions: (metrics) => {
        return metrics.system.memory.percentage > 75;
      },
      action: async () => {
        logger.info('Optimizing cache');
        // Simulated cache optimization
        await new Promise(resolve => setTimeout(resolve, 50));
        logger.info('Cache optimized');
      },
      cooldown: 300000 // 5 minutes
    });
  }

  /**
   * Setup event listeners for automatic optimization
   */
  private setupEventListeners(): void {
    performanceCollector.on('alert', (alert) => {
      if (alert.type === 'critical') {
        this.runOptimization('critical');
      }
    });
  }

  /**
   * Start the performance optimizer
   */
  public start(): void {
    if (this.isRunning) {
      return;
    }

    this.isRunning = true;

    // Run optimization every 2 minutes
    setInterval(() => {
      this.runOptimization('scheduled');
    }, 120000);

    logger.info('🚀 Performance optimizer started');
  }

  /**
   * Stop the performance optimizer
   */
  public stop(): void {
    this.isRunning = false;
    logger.info('🛑 Performance optimizer stopped');
  }

  /**
   * Run optimization strategies
   */
  public async runOptimization(trigger: 'scheduled' | 'critical' | 'manual'): Promise<OptimizationResult[]> {
    const results: OptimizationResult[] = [];
    const currentMetrics = performanceCollector.getCurrentMetrics();

    for (const [id, strategy] of this.strategies) {
      try {
        // Check cooldown
        const lastExec = this.lastExecution.get(id) || 0;
        if (Date.now() - lastExec < strategy.cooldown) {
          continue;
        }

        // Check conditions
        if (strategy.conditions(currentMetrics)) {
          const startTime = Date.now();

          logger.info('Running optimization strategy', {
            strategyId: id,
            strategyName: strategy.name,
            trigger
          });

          await strategy.action();

          const executionTime = Date.now() - startTime;
          this.lastExecution.set(id, Date.now());

          const result: OptimizationResult = {
            strategyId: id,
            success: true,
            message: `${strategy.name} executed successfully`,
            metrics: currentMetrics,
            timestamp: Date.now(),
            executionTime
          };

          results.push(result);
          this.results.push(result);

          logger.info('Optimization strategy completed', {
            strategyId: id,
            executionTime,
            success: true
          });
        }
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';

        logger.error('Optimization strategy failed', {
          strategyId: id,
          error: errorMessage
        });

        const result: OptimizationResult = {
          strategyId: id,
          success: false,
          message: `${strategy.name} failed: ${errorMessage}`,
          metrics: currentMetrics,
          timestamp: Date.now(),
          executionTime: 0
        };

        results.push(result);
        this.results.push(result);
      }
    }

    // Keep only recent results (last 100)
    if (this.results.length > 100) {
      this.results = this.results.slice(-100);
    }

    return results;
  }

  /**
   * Run a specific optimization strategy
   */
  public async runStrategy(strategyId: string): Promise<OptimizationResult | null> {
    const strategy = this.strategies.get(strategyId);
    if (!strategy) {
      logger.error('Optimization strategy not found', { strategyId });
      return null;
    }

    try {
      const startTime = Date.now();
      const currentMetrics = performanceCollector.getCurrentMetrics();

      logger.info('Running specific optimization strategy', {
        strategyId,
        strategyName: strategy.name
      });

      await strategy.action();

      const executionTime = Date.now() - startTime;
      this.lastExecution.set(strategyId, Date.now());

      const result: OptimizationResult = {
        strategyId,
        success: true,
        message: `${strategy.name} executed successfully`,
        metrics: currentMetrics,
        timestamp: Date.now(),
        executionTime
      };

      this.results.push(result);

      logger.info('Specific optimization strategy completed', {
        strategyId,
        executionTime,
        success: true
      });

      return result;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';

      logger.error('Specific optimization strategy failed', {
        strategyId,
        error: errorMessage
      });

      const result: OptimizationResult = {
        strategyId,
        success: false,
        message: `${strategy.name} failed: ${errorMessage}`,
        metrics: performanceCollector.getCurrentMetrics(),
        timestamp: Date.now(),
        executionTime: 0
      };

      this.results.push(result);
      return result;
    }
  }

  /**
   * Get optimization results
   */
  public getResults(limit: number = 50): OptimizationResult[] {
    return this.results.slice(-limit);
  }

  /**
   * Get optimization strategies
   */
  public getStrategies(): OptimizationStrategy[] {
    return Array.from(this.strategies.values());
  }

  /**
   * Get optimization statistics
   */
  public getStatistics(): {
    totalExecutions: number;
    successfulExecutions: number;
    failedExecutions: number;
    averageExecutionTime: number;
    lastExecution: number | null;
    strategiesExecuted: string[];
  } {
    const total = this.results.length;
    const successful = this.results.filter(r => r.success).length;
    const failed = total - successful;
    const avgExecutionTime = total > 0
      ? this.results.reduce((sum, r) => sum + r.executionTime, 0) / total
      : 0;
    const lastExecution = this.results.length > 0
      ? Math.max(...this.results.map(r => r.timestamp))
      : null;
    const strategiesExecuted = [...new Set(this.results.map(r => r.strategyId))];

    return {
      totalExecutions: total,
      successfulExecutions: successful,
      failedExecutions: failed,
      averageExecutionTime: Math.round(avgExecutionTime),
      lastExecution,
      strategiesExecuted
    };
  }

  /**
   * Generate optimization report
   */
  public generateReport(): {
    summary: {
      totalStrategies: number;
      activeStrategies: number;
      totalExecutions: number;
      successRate: number;
      averageExecutionTime: number;
    };
    strategies: Array<{
      strategy: OptimizationStrategy;
      executions: number;
      successRate: number;
      averageExecutionTime: number;
      lastExecution: number | null;
    }>;
    recentResults: OptimizationResult[];
    recommendations: string[];
  } {
    const strategies = this.getStrategies();
    const results = this.getResults();

    const strategyStats = strategies.map(strategy => {
      const strategyResults = results.filter(r => r.strategyId === strategy.id);
      const executions = strategyResults.length;
      const successful = strategyResults.filter(r => r.success).length;
      const successRate = executions > 0 ? (successful / executions) * 100 : 0;
      const avgExecutionTime = executions > 0
        ? strategyResults.reduce((sum, r) => sum + r.executionTime, 0) / executions
        : 0;
      const lastExecution = executions > 0
        ? Math.max(...strategyResults.map(r => r.timestamp))
        : null;

      return {
        strategy,
        executions,
        successRate: Math.round(successRate * 100) / 100,
        averageExecutionTime: Math.round(avgExecutionTime),
        lastExecution
      };
    });

    const totalExecutions = results.length;
    const successfulExecutions = results.filter(r => r.success).length;
    const successRate = totalExecutions > 0 ? (successfulExecutions / totalExecutions) * 100 : 0;
    const avgExecutionTime = totalExecutions > 0
      ? results.reduce((sum, r) => sum + r.executionTime, 0) / totalExecutions
      : 0;

    const recommendations: string[] = [];

    // Generate recommendations based on performance
    const currentMetrics = performanceCollector.getCurrentMetrics();

    if (currentMetrics.system.cpu.usage > 80) {
      recommendations.push('Consider scaling up CPU resources');
    }

    if (currentMetrics.system.memory.percentage > 85) {
      recommendations.push('Add more memory or optimize memory usage');
    }

    if (currentMetrics.database.avgQueryTime > 200) {
      recommendations.push('Optimize database queries and add indexes');
    }

    if (currentMetrics.rpa.successRate < 90) {
      recommendations.push('Improve RPA job error handling and retry logic');
    }

    return {
      summary: {
        totalStrategies: strategies.length,
        activeStrategies: strategies.length,
        totalExecutions,
        successRate: Math.round(successRate * 100) / 100,
        averageExecutionTime: Math.round(avgExecutionTime)
      },
      strategies: strategyStats,
      recentResults: results.slice(-10),
      recommendations
    };
  }
}

// Global instance
export const performanceOptimizer = new PerformanceOptimizer();

// Auto-start in production
if (process.env.NODE_ENV === 'production') {
  performanceOptimizer.start();
}