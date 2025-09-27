# Performance Monitoring System Implementation Guide

## Overview

The Gatekeeper RPA Performance Monitoring System provides comprehensive real-time monitoring, alerting, and performance analysis capabilities for the entire application stack. This system ensures optimal performance, early detection of issues, and data-driven optimization decisions.

## Architecture

### Core Components

1. **Metrics Collection Service** (`performance-monitor.service.ts`)
   - Real-time metrics collection from all system components
   - Integration with Prometheus for metrics aggregation
   - Automatic buffering and flushing to database
   - System resource monitoring (CPU, memory, disk, network)

2. **Alert Management System** (`alert.service.ts`)
   - Configurable alert rules with thresholds
   - Multi-channel notifications (email, Slack, webhooks)
   - Alert acknowledgment and resolution workflow
   - Alert history and analytics

3. **Database Performance Analyzer** (`query-analyzer.service.ts`)
   - Query execution analysis with EXPLAIN
   - Index optimization recommendations
   - Slow query detection and reporting
   - Database statistics and health metrics

4. **RPA Performance Monitor** (`rpa-performance.tsx`)
   - Job execution tracking
   - Queue management metrics
   - Success rate monitoring
   - Performance trend analysis

5. **Dashboard Components**
   - Real-time performance metrics display
   - Interactive charts and visualizations
   - Historical data analysis
   - System health indicators

## Key Features

### 1. Real-time Metrics Collection

The system automatically collects metrics for:
- **API Performance**: Response times, status codes, request volumes
- **Database Performance**: Query execution times, connection usage, slow queries
- **System Resources**: CPU usage, memory consumption, disk I/O, network traffic
- **RPA Operations**: Job execution times, success rates, queue lengths
- **Business Metrics**: Tickets processed, audit completion rates

### 2. Automated Alerting

Pre-configured alert rules monitor:
- High CPU usage (>80%)
- High memory usage (>85%)
- Slow API responses (>200ms)
- Slow RPA executions (>30s)
- High error rates (>5%)
- Database connection limits

### 3. Performance Dashboard

The performance dashboard provides:
- System health overview
- Real-time metrics visualization
- Historical trend analysis
- Alert management interface
- RPA performance metrics

## Implementation Steps

### 1. Setup Dependencies

```bash
npm install prom-client recharts
npm install -D @types/prom-client
```

### 2. Initialize Monitoring

Add to your application entry point (e.g., `src/index.ts`):

```typescript
import { performanceMonitor } from '@/services/monitoring/performance-monitor.service';
import { alertService } from '@/services/monitoring/alert.service';

// Initialize monitoring services
performanceMonitor.initialize();
alertService.start();
```

### 3. Add Performance Middleware

Apply to API routes:

```typescript
import { withPerformanceMonitoring } from '@/middleware/performance';

export const GET = withPerformanceMonitoring(async (req) => {
  // Your handler logic
});
```

### 4. Monitor Database Queries

Wrap your database operations:

```typescript
import { queryAnalyzer } from '@/services/database/query-analyzer.service';

async function getUserData(id: string) {
  const query = 'SELECT * FROM users WHERE id = $1';
  const analysis = await queryAnalyzer.analyzeQuery(query, [id]);

  // Log or handle slow queries
  if (analysis.duration > 100) {
    logger.warn('Slow query detected', analysis);
  }

  return db.execute(query, [id]);
}
```

### 5. Monitor RPA Operations

Track RPA job performance:

```typescript
import { performanceMonitor } from '@/services/monitoring/performance-monitor.service';

async function executeRPAJob(drNumber: string) {
  const startTime = Date.now();

  try {
    const result = await rpaService.executeJob(drNumber);
    const duration = Date.now() - startTime;

    // Record metrics
    performanceMonitor.recordRPAExecution(
      drNumber,
      result.status,
      duration,
      result.success
    );

    return result;
  } catch (error) {
    const duration = Date.now() - startTime;
    performanceMonitor.recordRPAExecution(
      drNumber,
      'failed',
      duration,
      false
    );
    throw error;
  }
}
```

## Configuration

### Environment Variables

Add to your `.env` file:

```env
# Performance monitoring
METRICS_ENABLED=true
METRICS_PORT=9090
ALERT_WEBHOOK_URL=https://hooks.slack.com/services/...
PROMETHEUS_ENDPOINT=http://localhost:9090

# Alert thresholds
CPU_THRESHOLD=80
MEMORY_THRESHOLD=85
RESPONSE_TIME_THRESHOLD=200
RPA_EXECUTION_THRESHOLD=30000
```

### Custom Alert Rules

```typescript
import { alertService } from '@/services/monitoring/alert.service';

const customRule = {
  id: 'custom-metric-alert',
  name: 'Custom Metric Alert',
  description: 'Custom metric exceeds threshold',
  metric: 'custom_metric_name',
  condition: 'gt' as const,
  threshold: 100,
  severity: 'high' as const,
  duration: 5,
  enabled: true,
  notifications: [
    {
      type: 'slack',
      config: { webhook: process.env.SLACK_WEBHOOK }
    }
  ]
};

alertService.addRule(customRule);
```

## Monitoring Endpoints

### 1. Metrics API

- `GET /api/performance/metrics?type=system` - System metrics
- `GET /api/performance/metrics?type=database` - Database metrics
- `GET /api/performance/metrics?type=rpa` - RPA metrics
- `GET /api/performance/metrics?type=alerts` - Active alerts

### 2. Prometheus Exporter

- `GET /api/performance/prometheus` - Prometheus format metrics

### 3. Database Analysis

- `GET /api/performance/metrics?type=database-stats` - Database statistics
- `GET /api/performance/metrics?type=slow-queries` - Slow query report
- `GET /api/performance/metrics?type=index-recommendations` - Index optimization suggestions

## Performance Dashboard

Access the performance dashboard at `/performance` to view:
- Real-time system metrics
- Historical performance trends
- Active alerts and notifications
- RPA job performance
- Database health indicators

## Load Testing

Run load tests to validate system performance:

```bash
# Basic load test
node scripts/load-test.ts http://localhost:3020/api/health 10 100 10 60

# High-load simulation
node scripts/load-test.ts http://localhost:3020/api/tickets 50 500 30 120
```

## Best Practices

### 1. Monitoring Strategy

- Monitor all critical paths (API, database, RPA)
- Set appropriate thresholds based on baseline metrics
- Implement progressive alerting (warning → critical)
- Review and adjust alert rules regularly

### 2. Performance Optimization

- Use metrics to identify bottlenecks
- Implement caching for frequently accessed data
- Optimize database queries based on analysis
- Scale resources based on load patterns

### 3. Alert Management

- Avoid alert fatigue with meaningful thresholds
- Implement on-call rotation for critical alerts
- Document runbooks for common alerts
- Regularly review alert effectiveness

### 4. Dashboard Usage

- Create dashboards for different user roles
- Include both technical and business metrics
- Set appropriate time ranges for different analyses
- Share dashboards with stakeholders

## Troubleshooting

### Common Issues

1. **High Memory Usage**
   - Check for memory leaks in application code
   - Review database connection pooling
   - Monitor cache usage

2. **Slow API Responses**
   - Analyze database query performance
   - Check for external service latency
   - Review application logic complexity

3. **RPA Job Failures**
   - Monitor external system availability
   - Check for authentication issues
   - Review job configuration

### Debug Commands

```bash
# Check active alerts
curl http://localhost:3020/api/performance/metrics?type=alerts

# Get database statistics
curl http://localhost:3020/api/performance/metrics?type=database-stats

# Export metrics for analysis
curl http://localhost:3020/api/performance/prometheus > metrics.txt
```

## Integration with External Systems

### 1. Prometheus

Add to `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'gatekeeper-rpa'
    static_configs:
      - targets: ['localhost:3020']
    metrics_path: '/api/performance/prometheus'
    scrape_interval: 15s
```

### 2. Grafana

Import the provided dashboard template to visualize metrics.

### 3. AlertManager

Configure AlertManager for advanced alert routing and escalation.

## Maintenance

### Regular Tasks

1. Review alert rules monthly
2. Archive old metrics data
3. Update dashboards based on evolving needs
4. Performance test after major changes
5. Monitor storage usage for metrics

### Scaling Considerations

- Use external metrics storage for high-volume environments
- Implement metrics aggregation for large deployments
- Consider distributed tracing for microservices architecture
- Plan for increased storage requirements

## Security Considerations

1. Secure metrics endpoints with authentication
2. Monitor for suspicious activity patterns
3. Regularly rotate alert webhook credentials
4. Limit access to sensitive performance data
5. Audit alert configuration changes

## Support

For issues or questions:
- Review the troubleshooting section
- Check application logs
- Contact the system administrator
- Create an issue in the project repository