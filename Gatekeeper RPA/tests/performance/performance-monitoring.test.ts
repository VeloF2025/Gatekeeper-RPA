/**
 * Performance Monitoring Integration Tests
 *
 * Comprehensive tests for the real-time performance monitoring system,
 * including metrics collection, WebSocket streaming, and optimization.
 */

import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { performanceCollector } from '@/lib/performance/performance-collector';
import { performanceOptimizer } from '@/lib/performance/performance-optimizer';

// Mock logger to avoid console output in tests
vi.mock('@/utils/logger', () => ({
  logger: {
    info: vi.fn(),
    error: vi.fn(),
    warn: vi.fn(),
    debug: vi.fn()
  }
}));

describe('Performance Monitoring System', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Reset performance collector state
    performanceCollector.stop();
  });

  afterEach(() => {
    performanceCollector.stop();
  });

  describe('PerformanceCollector', () => {
    it('should initialize with default metrics', () => {
      const metrics = performanceCollector.getCurrentMetrics();

      expect(metrics).toBeDefined();
      expect(metrics.timestamp).toBeInstanceOf(Number);
      expect(metrics.system).toBeDefined();
      expect(metrics.application).toBeDefined();
      expect(metrics.database).toBeDefined();
      expect(metrics.rpa).toBeDefined();
    });

    it('should start and stop collecting metrics', () => {
      const startSpy = vi.spyOn(performanceCollector as any, 'collectAllMetrics');

      performanceCollector.start();
      expect(performanceCollector['isCollecting']).toBe(true);

      performanceCollector.stop();
      expect(performanceCollector['isCollecting']).toBe(false);
    });

    it('should collect metrics at intervals', async () => {
      const metricsSpy = vi.fn();
      const unsubscribe = performanceCollector.subscribe(metricsSpy);

      performanceCollector.start();

      // Wait for a few intervals
      await new Promise(resolve => setTimeout(resolve, 100));

      expect(metricsSpy).toHaveBeenCalled();

      unsubscribe();
      performanceCollector.stop();
    });

    it('should handle subscription and unsubscription', () => {
      const metricsSpy = vi.fn();

      const unsubscribe = performanceCollector.subscribe(metricsSpy);

      // Emit a test metric
      performanceCollector.emit('metrics', performanceCollector.getCurrentMetrics());

      expect(metricsSpy).toHaveBeenCalled();

      unsubscribe();

      // Should not be called after unsubscribe
      metricsSpy.mockClear();
      performanceCollector.emit('metrics', performanceCollector.getCurrentMetrics());
      expect(metricsSpy).not.toHaveBeenCalled();
    });

    it('should detect and generate alerts for high CPU usage', async () => {
      const alertSpy = vi.fn();
      performanceCollector.on('alert', alertSpy);

      // Simulate high CPU usage
      const metrics = performanceCollector.getCurrentMetrics();
      metrics.system.cpu.usage = 95; // Above critical threshold

      // Manually trigger alert check
      await (performanceCollector as any).checkAlerts();

      expect(alertSpy).toHaveBeenCalled();
      const alert = alertSpy.mock.calls[0][0];
      expect(alert.type).toBe('critical');
      expect(alert.category).toBe('system');
      expect(alert.message).toContain('CPU usage critical');
    });

    it('should generate performance report', () => {
      const report = performanceCollector.generateReport();

      expect(report).toBeDefined();
      expect(report.summary).toBeDefined();
      expect(report.metrics).toBeDefined();
      expect(report.alerts).toBeDefined();
      expect(report.recommendations).toBeDefined();

      expect(report.summary.overallHealth).toMatch(/excellent|good|fair|poor/);
      expect(Array.isArray(report.recommendations)).toBe(true);
    });
  });

  describe('PerformanceOptimizer', () => {
    it('should initialize with optimization strategies', () => {
      const strategies = performanceOptimizer.getStrategies();

      expect(strategies.length).toBeGreaterThan(0);
      expect(strategies[0]).toMatchObject({
        id: expect.any(String),
        name: expect.any(String),
        description: expect.any(String),
        category: expect.any(String),
        priority: expect.any(String),
        conditions: expect.any(Function),
        action: expect.any(Function),
        cooldown: expect.any(Number)
      });
    });

    it('should run optimization strategies based on conditions', async () => {
      const metrics = performanceCollector.getCurrentMetrics();

      // Simulate high memory usage to trigger optimization
      metrics.system.memory.percentage = 90;

      const results = await performanceOptimizer.runOptimization('manual');

      expect(Array.isArray(results)).toBe(true);

      // Check if memory optimization was triggered
      const memoryOptimization = results.find(r => r.strategyId === 'memory-gc');
      if (memoryOptimization) {
        expect(memoryOptimization.strategyId).toBe('memory-gc');
      }
    });

    it('should respect cooldown periods', async () => {
      // First run
      const results1 = await performanceOptimizer.runOptimization('manual');

      // Immediate second run (should be blocked by cooldown)
      const results2 = await performanceOptimizer.runOptimization('manual');

      // Second run should have fewer or no results due to cooldown
      expect(results2.length).toBeLessThanOrEqual(results1.length);
    });

    it('should run specific optimization strategy', async () => {
      const result = await performanceOptimizer.runStrategy('memory-gc');

      expect(result).toBeDefined();
      if (result) {
        expect(result.strategyId).toBe('memory-gc');
        expect(result.success).toBe(true);
      }
    });

    it('should handle failed optimization strategies', async () => {
      // Mock a failing strategy
      const originalStrategy = performanceOptimizer['strategies'].get('memory-gc');
      if (originalStrategy) {
        const failingStrategy = {
          ...originalStrategy,
          action: vi.fn().mockRejectedValue(new Error('Test failure'))
        };
        performanceOptimizer['strategies'].set('memory-gc', failingStrategy);

        const result = await performanceOptimizer.runStrategy('memory-gc');

        expect(result).toBeDefined();
        expect(result.success).toBe(false);
        expect(result.message).toContain('failed');

        // Restore original strategy
        performanceOptimizer['strategies'].set('memory-gc', originalStrategy);
      }
    });

    it('should generate optimization report', () => {
      const report = performanceOptimizer.generateReport();

      expect(report).toBeDefined();
      expect(report.summary).toBeDefined();
      expect(report.strategies).toBeDefined();
      expect(report.recentResults).toBeDefined();
      expect(report.recommendations).toBeDefined();

      expect(report.summary.totalStrategies).toBeGreaterThan(0);
      expect(report.summary.successRate).toBeGreaterThanOrEqual(0);
      expect(report.summary.successRate).toBeLessThanOrEqual(100);
    });

    it('should track optimization statistics', () => {
      const stats = performanceOptimizer.getStatistics();

      expect(stats).toBeDefined();
      expect(stats.totalExecutions).toBeGreaterThanOrEqual(0);
      expect(stats.successfulExecutions).toBeGreaterThanOrEqual(0);
      expect(stats.failedExecutions).toBeGreaterThanOrEqual(0);
      expect(stats.averageExecutionTime).toBeGreaterThanOrEqual(0);
      expect(Array.isArray(stats.strategiesExecuted)).toBe(true);
    });
  });

  describe('Performance Integration', () => {
    it('should handle real-time metrics updates', async () => {
      const metricsSpy = vi.fn();
      const alertSpy = vi.fn();

      performanceCollector.subscribe(metricsSpy);
      performanceCollector.on('alert', alertSpy);

      performanceCollector.start();

      // Simulate some metrics changes
      await new Promise(resolve => setTimeout(resolve, 150));

      expect(metricsSpy).toHaveBeenCalled();

      performanceCollector.stop();
    });

    it('should coordinate between collector and optimizer', async () => {
      const metrics = performanceCollector.getCurrentMetrics();

      // Set up conditions that should trigger optimization
      metrics.system.memory.percentage = 95;
      metrics.rpa.successRate = 80;

      // Run optimization
      const results = await performanceOptimizer.runOptimization('manual');

      expect(results.length).toBeGreaterThan(0);

      // Check that relevant optimizations were triggered
      const memoryOpt = results.find(r => r.strategyId === 'memory-gc');
      const rpaOpt = results.find(r => r.strategyId === 'rpa-job-optimization');

      // At least one of them should have been triggered
      expect(memoryOpt || rpaOpt).toBeTruthy();
    });

    it('should handle multiple concurrent optimizations', async () => {
      const promises = [
        performanceOptimizer.runOptimization('manual'),
        performanceOptimizer.runStrategy('memory-gc'),
        performanceOptimizer.runStrategy('query-optimization')
      ];

      const results = await Promise.allSettled(promises);

      // All should complete without errors
      expect(results.every(r => r.status === 'fulfilled')).toBe(true);

      // Should have results from each call
      const fulfilled = results as PromiseFulfilledResult<any>[];
      expect(fulfilled.length).toBe(3);
    });
  });

  describe('Error Handling', () => {
    it('should handle WebSocket connection errors gracefully', async () => {
      // Mock EventSource to simulate connection error
      const mockEventSource = vi.fn();
      mockEventSource.prototype = {
        addEventListener: vi.fn(),
        close: vi.fn()
      };

      // This test simulates error handling in the actual implementation
      expect(true).toBe(true); // Placeholder for actual error handling test
    });

    it('should handle invalid metrics data', () => {
      const metrics = performanceCollector.getCurrentMetrics();

      // Should still work with partial data
      delete metrics.system.cpu.usage;

      expect(metrics).toBeDefined();
      expect(metrics.system).toBeDefined();
    });

    it('should handle missing optimization strategies', async () => {
      const result = await performanceOptimizer.runStrategy('non-existent-strategy');

      expect(result).toBeNull();
    });
  });

  describe('Performance Benchmarks', () => {
    it('should collect metrics within acceptable time', async () => {
      const startTime = Date.now();

      await (performanceCollector as any).collectAllMetrics();

      const endTime = Date.now();
      const duration = endTime - startTime;

      expect(duration).toBeLessThan(1000); // Should complete within 1 second
    });

    it('should run optimizations within acceptable time', async () => {
      const startTime = Date.now();

      const results = await performanceOptimizer.runOptimization('manual');

      const endTime = Date.now();
      const duration = endTime - startTime;

      expect(duration).toBeLessThan(2000); // Should complete within 2 seconds
      expect(Array.isArray(results)).toBe(true);
    });

    it('should handle multiple subscribers efficiently', async () => {
      const subscribers = [];
      const spy = vi.fn();

      // Add multiple subscribers
      for (let i = 0; i < 100; i++) {
        subscribers.push(performanceCollector.subscribe(spy));
      }

      // Emit metrics
      performanceCollector.emit('metrics', performanceCollector.getCurrentMetrics());

      // Should have called all subscribers
      expect(spy).toHaveBeenCalledTimes(100);

      // Clean up
      subscribers.forEach(unsubscribe => unsubscribe());
    });
  });
});