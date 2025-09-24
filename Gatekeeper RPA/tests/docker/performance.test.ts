/**
 * Docker Performance Tests
 * Performance validation for Docker containers and services
 */

import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { PerformanceTester } from '../helpers/performance-tester';
import { DockerHelper } from '../helpers/docker-helper';

describe('Docker Performance Tests', () => {
  let performanceTester: PerformanceTester;
  let dockerHelper: DockerHelper;

  beforeAll(async () => {
    performanceTester = new PerformanceTester();
    dockerHelper = new DockerHelper();
  });

  describe('Container Startup Performance', () => {
    it('should start containers within acceptable time', async () => {
      const maxStartupTime = 30000; // 30 seconds

      const nextjsStartup = await performanceTester.measureContainerStartup(
        'gatekeeper-rpa-nextjs:latest',
        { environment: { NODE_ENV: 'production' } }
      );

      const rpaStartup = await performanceTester.measureContainerStartup(
        'gatekeeper-rpa-rpa:latest',
        { environment: { NODE_ENV: 'production' } }
      );

      const queueStartup = await performanceTester.measureContainerStartup(
        'gatekeeper-rpa-queue:latest',
        { environment: { NODE_ENV: 'production' } }
      );

      expect(nextjsStartup.time).toBeLessThan(maxStartupTime);
      expect(rpaStartup.time).toBeLessThan(maxStartupTime);
      expect(queueStartup.time).toBeLessThan(maxStartupTime);
    });

    it('should have consistent startup times', async () => {
      const iterations = 3;
      const startupTimes = [];

      for (let i = 0; i < iterations; i++) {
        const startup = await performanceTester.measureContainerStartup(
          'gatekeeper-rpa-nextjs:latest',
          { environment: { NODE_ENV: 'production' } }
        );
        startupTimes.push(startup.time);
      }

      const average = startupTimes.reduce((a, b) => a + b, 0) / startupTimes.length;
      const variance = startupTimes.reduce((sum, time) => sum + Math.pow(time - average, 2), 0) / startupTimes.length;
      const standardDeviation = Math.sqrt(variance);

      // Should be within 20% variance
      expect(standardDeviation / average).toBeLessThan(0.2);
    });
  });

  describe('Memory Usage Performance', () => {
    it('should not exceed memory limits', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' }
      });

      // Wait for container to stabilize
      await new Promise(resolve => setTimeout(resolve, 10000));

      const memoryUsage = await performanceTester.getMemoryUsage(container);
      const maxMemoryMB = 512; // 512MB limit

      expect(memoryUsage.rssMB).toBeLessThan(maxMemoryMB);
      expect(memoryUsage.heapUsedMB).toBeLessThan(maxMemoryMB * 0.8);

      await dockerHelper.stopContainer(container);
    });

    it('should have stable memory usage', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' }
      });

      const memoryMeasurements = [];
      const measurementInterval = 5000; // 5 seconds
      const measurementDuration = 30000; // 30 seconds

      for (let i = 0; i < measurementDuration / measurementInterval; i++) {
        const memory = await performanceTester.getMemoryUsage(container);
        memoryMeasurements.push(memory);
        await new Promise(resolve => setTimeout(resolve, measurementInterval));
      }

      const memoryValues = memoryMeasurements.map(m => m.heapUsedMB);
      const average = memoryValues.reduce((a, b) => a + b, 0) / memoryValues.length;
      const maxMemory = Math.max(...memoryValues);
      const minMemory = Math.min(...memoryValues);

      // Memory usage should not fluctuate more than 50%
      expect((maxMemory - minMemory) / average).toBeLessThan(0.5);

      await dockerHelper.stopContainer(container);
    });
  });

  describe('CPU Performance', () => {
    it('should not exceed CPU limits', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' }
      });

      await new Promise(resolve => setTimeout(resolve, 10000));

      const cpuUsage = await performanceTester.getCPUUsage(container);
      const maxCPUUsage = 80; // 80%

      expect(cpuUsage).toBeLessThan(maxCPUUsage);

      await dockerHelper.stopContainer(container);
    });

    it('should handle concurrent requests efficiently', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' },
        ports: { '3000': '3000' }
      });

      await new Promise(resolve => setTimeout(resolve, 10000));

      const concurrentRequests = 50;
      const responseTimes = [];

      const requestPromises = Array(concurrentRequests).fill(0).map(() =>
        fetch('http://localhost:3000/api/health')
          .then(response => {
            responseTimes.push(Date.now());
            return response;
          })
      );

      await Promise.all(requestPromises);

      const startTime = responseTimes[0];
      const endTime = responseTimes[responseTimes.length - 1];
      const totalDuration = endTime - startTime;

      // All requests should complete within 10 seconds
      expect(totalDuration).toBeLessThan(10000);

      await dockerHelper.stopContainer(container);
    });
  });

  describe('Network Performance', () => {
    it('should have low response times', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' },
        ports: { '3000': '3000' }
      });

      await new Promise(resolve => setTimeout(resolve, 10000));

      const responseTimes = [];
      const numRequests = 100;

      for (let i = 0; i < numRequests; i++) {
        const startTime = Date.now();
        await fetch('http://localhost:3000/api/health');
        responseTimes.push(Date.now() - startTime);
      }

      const averageResponseTime = responseTimes.reduce((a, b) => a + b, 0) / responseTimes.length;
      const maxResponseTime = Math.max(...responseTimes);

      // Average response time should be under 200ms
      expect(averageResponseTime).toBeLessThan(200);
      // Max response time should be under 1000ms
      expect(maxResponseTime).toBeLessThan(1000);

      await dockerHelper.stopContainer(container);
    });

    it('should handle network throughput', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' },
        ports: { '3000': '3000' }
      });

      await new Promise(resolve => setTimeout(resolve, 10000));

      const testData = { data: 'x'.repeat(1000) }; // 1KB payload
      const numRequests = 1000;
      const startTime = Date.now();

      const requestPromises = Array(numRequests).fill(0).map(() =>
        fetch('http://localhost:3000/api/test', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(testData)
        })
      );

      await Promise.all(requestPromises);
      const endTime = Date.now();
      const totalDuration = (endTime - startTime) / 1000; // Convert to seconds

      const throughput = (numRequests * 1000) / totalDuration; // bytes per second
      const requestsPerSecond = numRequests / totalDuration;

      // Should handle at least 100 requests per second
      expect(requestsPerSecond).toBeGreaterThan(100);
      // Should handle at least 100KB per second
      expect(throughput).toBeGreaterThan(100 * 1000);

      await dockerHelper.stopContainer(container);
    });
  });

  describe('Resource Scaling', () => {
    it('should scale horizontally', async () => {
      const numContainers = 3;
      const containers = [];

      for (let i = 0; i < numContainers; i++) {
        const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
          environment: { NODE_ENV: 'production' },
          ports: { [`300${i}`]: '3000' }
        });
        containers.push(container);
      }

      await new Promise(resolve => setTimeout(resolve, 10000));

      // Test that all containers are responsive
      const healthPromises = containers.map((_, index) =>
        fetch(`http://localhost:300${index}/api/health`)
      );

      const healthResponses = await Promise.all(healthPromises);
      const allHealthy = healthResponses.every(response => response.ok);

      expect(allHealthy).toBe(true);

      // Clean up
      for (const container of containers) {
        await dockerHelper.stopContainer(container);
      }
    });

    it('should handle load gracefully', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' },
        ports: { '3000': '3000' }
      });

      await new Promise(resolve => setTimeout(resolve, 10000));

      // Test with increasing load
      const loadLevels = [10, 50, 100, 200]; // Concurrent requests
      const performanceData = [];

      for (const load of loadLevels) {
        const startTime = Date.now();

        const requests = Array(load).fill(0).map(() =>
          fetch('http://localhost:3000/api/health')
        );

        await Promise.all(requests);
        const duration = Date.now() - startTime;

        performanceData.push({
          load,
          duration,
          requestsPerSecond: load / (duration / 1000)
        });
      }

      // Performance should degrade gracefully
      for (let i = 1; i < performanceData.length; i++) {
        const current = performanceData[i];
        const previous = performanceData[i - 1];

        // Performance should not drop more than 50% when load doubles
        const performanceRatio = current.requestsPerSecond / previous.requestsPerSecond;
        const loadRatio = current.load / previous.load;

        expect(performanceRatio).toBeGreaterThan(0.5 / loadRatio);
      }

      await dockerHelper.stopContainer(container);
    });
  });

  describe('Database Performance', () => {
    it('should handle database queries efficiently', async () => {
      const container = await dockerHelper.createContainer('gatekeeper-rpa-nextjs:latest', {
        environment: { NODE_ENV: 'production' },
        ports: { '3000': '3000' }
      });

      await new Promise(resolve => setTimeout(resolve, 10000));

      const queryTimes = [];
      const numQueries = 100;

      for (let i = 0; i < numQueries; i++) {
        const startTime = Date.now();
        await fetch('http://localhost:3000/api/test/db-query');
        queryTimes.push(Date.now() - startTime);
      }

      const averageQueryTime = queryTimes.reduce((a, b) => a + b, 0) / queryTimes.length;
      const maxQueryTime = Math.max(...queryTimes);

      // Average query time should be under 100ms
      expect(averageQueryTime).toBeLessThan(100);
      // Max query time should be under 500ms
      expect(maxQueryTime).toBeLessThan(500);

      await dockerHelper.stopContainer(container);
    });
  });
});