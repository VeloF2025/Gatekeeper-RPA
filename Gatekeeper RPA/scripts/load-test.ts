/**
 * Load Testing Script for Gatekeeper RPA
 * Simulates high-load scenarios to test system performance
 */

import http from 'http';
import https from 'https';
import { URL } from 'url';
import { performance } from 'perf_hooks';

interface LoadTestConfig {
  targetUrl: string;
  concurrentUsers: number;
  requestsPerUser: number;
  rampUpTime: number; // seconds
  duration: number; // seconds
  method: 'GET' | 'POST' | 'PUT' | 'DELETE';
  headers?: Record<string, string>;
  body?: string;
}

interface TestResult {
  requestId: string;
  userId: number;
  statusCode: number;
  responseTime: number;
  timestamp: number;
  error?: string;
}

interface TestSummary {
  totalRequests: number;
  successfulRequests: number;
  failedRequests: number;
  averageResponseTime: number;
  minResponseTime: number;
  maxResponseTime: number;
  p95ResponseTime: number;
  p99ResponseTime: number;
  requestsPerSecond: number;
  throughput: number; // bytes per second
  startTime: number;
  endTime: number;
  duration: number;
  errorDistribution: Record<string, number>;
  statusDistribution: Record<number, number>;
}

class LoadTester {
  private results: TestResult[] = [];
  private activeConnections = 0;
  private completedConnections = 0;

  async runTest(config: LoadTestConfig): Promise<TestSummary> {
    console.log('🚀 Starting load test...');
    console.log('Configuration:', {
      targetUrl: config.targetUrl,
      concurrentUsers: config.concurrentUsers,
      requestsPerUser: config.requestsPerUser,
      rampUpTime: config.rampUpTime,
      duration: config.duration,
      method: config.method
    });

    const startTime = performance.now();
    const rampUpInterval = config.rampUpTime / config.concurrentUsers;

    // Start users with ramp-up
    for (let i = 0; i < config.concurrentUsers; i++) {
      setTimeout(() => {
        this.runUser(config, i + 1);
      }, i * rampUpInterval * 1000);
    }

    // Wait for test completion
    await new Promise(resolve => {
      const checkCompletion = () => {
        if (this.completedConnections >= config.concurrentUsers) {
          resolve(null);
        } else {
          setTimeout(checkCompletion, 100);
        }
      };
      checkCompletion();
    });

    const endTime = performance.now();
    const duration = (endTime - startTime) / 1000;

    // Generate summary
    const summary = this.generateSummary(startTime, endTime, duration);

    console.log('\n📊 Load Test Complete!');
    this.printSummary(summary);

    return summary;
  }

  private async runUser(config: LoadTestConfig, userId: number): Promise<void> {
    this.activeConnections++;
    const userStartTime = performance.now();

    for (let i = 0; i < config.requestsPerUser; i++) {
      // Check if test duration exceeded
      if (performance.now() - userStartTime > config.duration * 1000) {
        break;
      }

      const result = await this.makeRequest(config, userId, i);
      this.results.push(result);

      // Add small delay between requests
      if (i < config.requestsPerUser - 1) {
        await new Promise(resolve => setTimeout(resolve, Math.random() * 100));
      }
    }

    this.activeConnections--;
    this.completedConnections++;
  }

  private async makeRequest(
    config: LoadTestConfig,
    userId: number,
    requestIndex: number
  ): Promise<TestResult> {
    const startTime = performance.now();
    const requestId = `${userId}-${requestIndex}-${Date.now()}`;

    try {
      const url = new URL(config.targetUrl);
      const isHttps = url.protocol === 'https:';
      const httpModule = isHttps ? https : http;

      const options: http.RequestOptions = {
        hostname: url.hostname,
        port: url.port || (isHttps ? 443 : 80),
        path: url.pathname + url.search,
        method: config.method,
        headers: {
          'User-Agent': 'Gatekeeper-LoadTester/1.0',
          'Content-Type': 'application/json',
          'X-Request-ID': requestId,
          'X-User-ID': userId.toString(),
          ...config.headers
        }
      };

      return new Promise((resolve) => {
        const req = httpModule.request(options, (res) => {
          let data = '';
          res.on('data', (chunk) => data += chunk);
          res.on('end', () => {
            const responseTime = performance.now() - startTime;
            resolve({
              requestId,
              userId,
              statusCode: res.statusCode || 0,
              responseTime,
              timestamp: startTime
            });
          });
        });

        req.on('error', (error) => {
          const responseTime = performance.now() - startTime;
          resolve({
            requestId,
            userId,
            statusCode: 0,
            responseTime,
            timestamp: startTime,
            error: error.message
          });
        });

        if (config.body) {
          req.write(config.body);
        }

        req.end();
      });

    } catch (error) {
      const responseTime = performance.now() - startTime;
      return {
        requestId,
        userId,
        statusCode: 0,
        responseTime,
        timestamp: startTime,
        error: error instanceof Error ? error.message : 'Unknown error'
      };
    }
  }

  private generateSummary(startTime: number, endTime: number, duration: number): TestSummary {
    const responseTimes = this.results.map(r => r.responseTime).sort((a, b) => a - b);
    const successfulRequests = this.results.filter(r => r.statusCode >= 200 && r.statusCode < 300).length;
    const failedRequests = this.results.length - successfulRequests;

    // Calculate percentiles
    const p95Index = Math.floor(responseTimes.length * 0.95);
    const p99Index = Math.floor(responseTimes.length * 0.99);

    const errorDistribution = this.results.reduce((acc, result) => {
      if (result.error) {
        acc[result.error] = (acc[result.error] || 0) + 1;
      }
      return acc;
    }, {} as Record<string, number>);

    const statusDistribution = this.results.reduce((acc, result) => {
      acc[result.statusCode] = (acc[result.statusCode] || 0) + 1;
      return acc;
    }, {} as Record<number, number>);

    return {
      totalRequests: this.results.length,
      successfulRequests,
      failedRequests,
      averageResponseTime: responseTimes.reduce((a, b) => a + b, 0) / responseTimes.length,
      minResponseTime: responseTimes[0] || 0,
      maxResponseTime: responseTimes[responseTimes.length - 1] || 0,
      p95ResponseTime: responseTimes[p95Index] || 0,
      p99ResponseTime: responseTimes[p99Index] || 0,
      requestsPerSecond: this.results.length / duration,
      throughput: 0, // Would need to track response sizes
      startTime,
      endTime,
      duration,
      errorDistribution,
      statusDistribution
    };
  }

  private printSummary(summary: TestSummary): void {
    console.log('\n=== Test Summary ===');
    console.log(`Duration: ${summary.duration.toFixed(2)} seconds`);
    console.log(`Total Requests: ${summary.totalRequests}`);
    console.log(`Successful Requests: ${summary.successfulRequests} (${(summary.successfulRequests / summary.totalRequests * 100).toFixed(2)}%)`);
    console.log(`Failed Requests: ${summary.failedRequests} (${(summary.failedRequests / summary.totalRequests * 100).toFixed(2)}%)`);
    console.log(`Requests per Second: ${summary.requestsPerSecond.toFixed(2)}`);

    console.log('\n=== Response Times ===');
    console.log(`Average: ${summary.averageResponseTime.toFixed(2)}ms`);
    console.log(`Minimum: ${summary.minResponseTime.toFixed(2)}ms`);
    console.log(`Maximum: ${summary.maxResponseTime.toFixed(2)}ms`);
    console.log(`95th Percentile: ${summary.p95ResponseTime.toFixed(2)}ms`);
    console.log(`99th Percentile: ${summary.p99ResponseTime.toFixed(2)}ms`);

    console.log('\n=== Status Code Distribution ===');
    Object.entries(summary.statusDistribution).forEach(([status, count]) => {
      console.log(`${status}: ${count} (${(count / summary.totalRequests * 100).toFixed(2)}%)`);
    });

    if (Object.keys(summary.errorDistribution).length > 0) {
      console.log('\n=== Error Distribution ===');
      Object.entries(summary.errorDistribution).forEach(([error, count]) => {
        console.log(`${error}: ${count}`);
      });
    }

    // Performance assessment
    console.log('\n=== Performance Assessment ===');

    if (summary.averageResponseTime < 200) {
      console.log('✅ Average response time is excellent (< 200ms)');
    } else if (summary.averageResponseTime < 500) {
      console.log('⚠️  Average response time is acceptable (< 500ms)');
    } else {
      console.log('❌ Average response time is too slow (> 500ms)');
    }

    if (summary.failedRequests / summary.totalRequests < 0.01) {
      console.log('✅ Error rate is excellent (< 1%)');
    } else if (summary.failedRequests / summary.totalRequests < 0.05) {
      console.log('⚠️  Error rate is acceptable (< 5%)');
    } else {
      console.log('❌ Error rate is too high (> 5%)');
    }

    if (summary.p95ResponseTime < 1000) {
      console.log('✅ 95th percentile response time is good (< 1s)');
    } else {
      console.log('❌ 95th percentile response time is too slow (> 1s)');
    }
  }

  // Export results to CSV
  exportToCSV(filename?: string): void {
    const defaultFilename = `load-test-results-${Date.now()}.csv`;
    const filePath = filename || defaultFilename;

    const headers = [
      'RequestID',
      'UserID',
      'StatusCode',
      'ResponseTime',
      'Timestamp',
      'Error'
    ];

    const rows = this.results.map(r => [
      r.requestId,
      r.userId,
      r.statusCode,
      r.responseTime,
      new Date(r.timestamp).toISOString(),
      r.error || ''
    ]);

    const csv = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    require('fs').writeFileSync(filePath, csv);
    console.log(`\n📄 Results exported to: ${filePath}`);
  }
}

// Command line interface
if (require.main === module) {
  const args = process.argv.slice(2);

  // Parse command line arguments
  const config: LoadTestConfig = {
    targetUrl: args[0] || 'http://localhost:3020/api/health',
    concurrentUsers: parseInt(args[1]) || 10,
    requestsPerUser: parseInt(args[2]) || 100,
    rampUpTime: parseInt(args[3]) || 10,
    duration: parseInt(args[4]) || 60,
    method: 'GET'
  };

  const tester = new LoadTester();

  tester.runTest(config)
    .then(() => {
      tester.exportToCSV();
      process.exit(0);
    })
    .catch(error => {
      console.error('Load test failed:', error);
      process.exit(1);
    });
}

export default LoadTester;