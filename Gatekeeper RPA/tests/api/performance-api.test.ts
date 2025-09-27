/**
 * Performance API Integration Tests
 *
 * Tests for the performance monitoring API endpoints including
 * REST APIs and WebSocket streaming functionality.
 */

import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { NextRequest } from 'next/server';
import { GET, POST } from '@/app/api/performance/route';
import { performanceCollector } from '@/lib/performance/performance-collector';
import { performanceOptimizer } from '@/lib/performance/performance-optimizer';

// Mock auth middleware
vi.mock('@/middleware/auth', () => ({
  authMiddleware: vi.fn(() => ({ userId: 'test-user' }))
}));

// Mock logger
vi.mock('@/utils/logger', () => ({
  logger: {
    info: vi.fn(),
    error: vi.fn(),
    warn: vi.fn(),
    debug: vi.fn()
  }
}));

describe('Performance API Endpoints', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    performanceCollector.start();
  });

  afterEach(() => {
    performanceCollector.stop();
  });

  describe('GET /api/performance', () => {
    it('should return comprehensive performance data', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.timestamp).toBeDefined();
      expect(data.performance).toBeDefined();
      expect(data.metrics).toBeDefined();
      expect(data.alerts).toBeDefined();
      expect(data.optimization).toBeDefined();
    });

    it('should handle query parameters correctly', async () => {
      const request = new NextRequest(
        'http://localhost:3000/api/performance?metrics=false&alerts=false&optimization=false',
        { method: 'GET' }
      );

      const response = await GET(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.metrics).toBeUndefined();
      expect(data.alerts).toBeUndefined();
      expect(data.optimization).toBeUndefined();
    });

    it('should include history when requested', async () => {
      const request = new NextRequest(
        'http://localhost:3000/api/performance?history=true',
        { method: 'GET' }
      );

      const response = await GET(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.history).toBeDefined();
      expect(data.history.metrics).toBeDefined();
    });

    it('should include comprehensive report when requested', async () => {
      const request = new NextRequest(
        'http://localhost:3000/api/performance?report=true',
        { method: 'GET' }
      );

      const response = await GET(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.report).toBeDefined();
      expect(data.report.performance).toBeDefined();
      expect(data.report.optimization).toBeDefined();
    });

    it('should handle authentication errors', async () => {
      // Mock auth middleware to return error
      const { authMiddleware } = await import('@/middleware/auth');
      vi.mocked(authMiddleware).mockReturnValue(new Response('Unauthorized', { status: 401 }));

      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);

      expect(response.status).toBe(401);
    });
  });

  describe('POST /api/performance', () => {
    it('should run all optimization strategies by default', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({})
      });

      const response = await POST(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.data).toBeDefined();
      expect(data.data.results).toBeDefined();
      expect(data.data.summary).toBeDefined();
      expect(Array.isArray(data.data.results)).toBe(true);
    });

    it('should run specific optimization strategy when requested', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ strategy: 'memory-gc' })
      });

      const response = await POST(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.data.results).toBeDefined();
      expect(data.data.results.length).toBeGreaterThan(0);

      // Check if the specific strategy was executed
      const results = data.data.results;
      expect(results.some((r: any) => r.strategyId === 'memory-gc')).toBe(true);
    });

    it('should handle invalid strategy gracefully', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ strategy: 'invalid-strategy' })
      });

      const response = await POST(request);
      const data = await response.json();

      expect(response.status).toBe(200);
      expect(data.success).toBe(true);
      expect(data.data.results).toBeDefined();
      expect(Array.isArray(data.data.results)).toBe(true);
    });

    it('should handle different trigger types', async () => {
      const triggers = ['manual', 'critical', 'scheduled'];

      for (const trigger of triggers) {
        const request = new NextRequest('http://localhost:3000/api/performance', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ trigger })
        });

        const response = await POST(request);
        const data = await response.json();

        expect(response.status).toBe(200);
        expect(data.success).toBe(true);
      }
    });

    it('should handle malformed JSON gracefully', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: 'invalid-json'
      });

      const response = await POST(request);

      expect(response.status).toBe(500);
      const data = await response.json();
      expect(data.success).toBe(false);
      expect(data.error).toBeDefined();
    });
  });

  describe('Performance Data Structure', () => {
    it('should return properly structured metrics', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);
      const data = await response.json();

      expect(data.metrics).toMatchObject({
        timestamp: expect.any(Number),
        system: expect.objectContaining({
          cpu: expect.objectContaining({
            usage: expect.any(Number),
            loadAverage: expect.arrayContaining([expect.any(Number)])
          }),
          memory: expect.objectContaining({
            total: expect.any(Number),
            used: expect.any(Number),
            percentage: expect.any(Number)
          })
        }),
        application: expect.objectContaining({
          uptime: expect.any(Number),
          memoryUsage: expect.objectContaining({
            rss: expect.any(Number),
            heapTotal: expect.any(Number),
            heapUsed: expect.any(Number)
          })
        }),
        database: expect.objectContaining({
          connections: expect.any(Number),
          maxConnections: expect.any(Number),
          queryCount: expect.any(Number)
        }),
        rpa: expect.objectContaining({
          activeJobs: expect.any(Number),
          completedJobs: expect.any(Number),
          failedJobs: expect.any(Number),
          successRate: expect.any(Number)
        })
      });
    });

    it('should return properly structured alerts', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);
      const data = await response.json();

      expect(data.alerts).toBeDefined();
      expect(data.alerts.active).toBeDefined();
      expect(data.alerts.total).toBeDefined();
      expect(Array.isArray(data.alerts.active)).toBe(true);

      // If there are alerts, they should have proper structure
      if (data.alerts.active.length > 0) {
        expect(data.alerts.active[0]).toMatchObject({
          id: expect.any(String),
          type: expect.stringMatching(/warning|error|critical/),
          category: expect.stringMatching(/system|application|database|rpa/),
          message: expect.any(String),
          value: expect.any(Number),
          threshold: expect.any(Number),
          timestamp: expect.any(Number),
          resolved: expect.any(Boolean)
        });
      }
    });

    it('should return properly structured optimization data', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);
      const data = await response.json();

      expect(data.optimization).toBeDefined();
      expect(data.optimization.strategies).toBeDefined();
      expect(data.optimization.statistics).toBeDefined();
      expect(data.optimization.recentResults).toBeDefined();

      expect(Array.isArray(data.optimization.strategies)).toBe(true);
      expect(data.optimization.statistics).toMatchObject({
        totalExecutions: expect.any(Number),
        successfulExecutions: expect.any(Number),
        failedExecutions: expect.any(Number),
        averageExecutionTime: expect.any(Number)
      });
    });
  });

  describe('Performance Response Times', () => {
    it('should respond within acceptable time limits', async () => {
      const startTime = Date.now();

      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);
      const endTime = Date.now();

      expect(response.status).toBe(200);
      expect(endTime - startTime).toBeLessThan(1000); // Should respond within 1 second

      const data = await response.json();
      expect(data.performance.responseTime).toBeDefined();
      expect(data.performance.responseTime).toBeLessThan(1000);
    });

    it('should handle optimization requests within acceptable time', async () => {
      const startTime = Date.now();

      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({})
      });

      const response = await POST(request);
      const endTime = Date.now();

      expect(response.status).toBe(200);
      expect(endTime - startTime).toBeLessThan(3000); // Should complete within 3 seconds

      const data = await response.json();
      expect(data.performance.responseTime).toBeDefined();
      expect(data.performance.responseTime).toBeLessThan(3000);
    });
  });

  describe('Error Handling and Edge Cases', () => {
    it('should handle concurrent requests gracefully', async () => {
      const requests = Array.from({ length: 10 }, () =>
        new NextRequest('http://localhost:3000/api/performance', { method: 'GET' })
      );

      const responses = await Promise.allSettled(requests.map(GET));

      // All requests should succeed
      responses.forEach(result => {
        expect(result.status).toBe('fulfilled');
        if (result.status === 'fulfilled') {
          expect(result.value.status).toBe(200);
        }
      });
    });

    it('should handle malformed query parameters', async () => {
      const request = new NextRequest(
        'http://localhost:3000/api/performance?invalid-param=value',
        { method: 'GET' }
      );

      const response = await GET(request);

      expect(response.status).toBe(200);
      const data = await response.json();
      expect(data.success).toBe(true);
    });

    it('should include performance metadata in responses', async () => {
      const request = new NextRequest('http://localhost:3000/api/performance', {
        method: 'GET'
      });

      const response = await GET(request);
      const data = await response.json();

      expect(data.performance).toBeDefined();
      expect(data.performance.responseTime).toBeDefined();
      expect(data.performance.timestamp).toBeDefined();
      expect(new Date(data.performance.timestamp)).toBeInstanceOf(Date);
    });
  });
});