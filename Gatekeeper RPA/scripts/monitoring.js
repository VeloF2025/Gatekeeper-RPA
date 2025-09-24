/**
 * Gatekeeper RPA Monitoring & Metrics System
 * Comprehensive monitoring for Zero Trust infrastructure
 */

const { createHash } = require('crypto');
const promClient = require('prom-client');

// Initialize Prometheus metrics
const prometheus = new promClient.Registry();

// Default metrics
promClient.collectDefaultMetrics({
    register: prometheus,
    prefix: 'gatekeeper_rpa_',
    labels: { app: 'gatekeeper-rpa' },
    timeout: 5000,
    gcDurationBuckets: [0.001, 0.01, 0.1, 1, 2, 5]
});

// Custom metrics
const metrics = {
    // HTTP request metrics
    httpRequestDuration: new promClient.Histogram({
        name: 'http_request_duration_seconds',
        help: 'Duration of HTTP requests in seconds',
        labelNames: ['method', 'route', 'status_code'],
        buckets: [0.1, 0.5, 1, 2, 5, 10]
    }),

    httpRequestCount: new promClient.Counter({
        name: 'http_requests_total',
        help: 'Total number of HTTP requests',
        labelNames: ['method', 'route', 'status_code']
    }),

    // Security metrics
    securityEvents: new promClient.Counter({
        name: 'security_events_total',
        help: 'Total number of security events',
        labelNames: ['event_type', 'severity']
    }),

    failedAuthAttempts: new promClient.Counter({
        name: 'failed_auth_attempts_total',
        help: 'Total number of failed authentication attempts',
        labelNames: ['auth_type', 'reason']
    }),

    // Business metrics
    rpaJobsProcessed: new promClient.Counter({
        name: 'rpa_jobs_processed_total',
        help: 'Total number of RPA jobs processed',
        labelNames: ['job_type', 'status']
    }),

    rpaJobDuration: new promClient.Histogram({
        name: 'rpa_job_duration_seconds',
        help: 'Duration of RPA jobs in seconds',
        labelNames: ['job_type', 'status'],
        buckets: [30, 60, 120, 300, 600, 1800]
    }),

    // Database metrics
    databaseQueryDuration: new promClient.Histogram({
        name: 'database_query_duration_seconds',
        help: 'Duration of database queries in seconds',
        labelNames: ['operation', 'table'],
        buckets: [0.001, 0.01, 0.1, 1, 5]
    }),

    // WhatsApp metrics
    whatsappMessagesSent: new promClient.Counter({
        name: 'whatsapp_messages_sent_total',
        help: 'Total number of WhatsApp messages sent',
        labelNames: ['message_type', 'status']
    }),

    whatsappMessagesReceived: new promClient.Counter({
        name: 'whatsapp_messages_received_total',
        help: 'Total number of WhatsApp messages received',
        labelNames: ['message_type']
    }),

    // System metrics
    activeConnections: new promClient.Gauge({
        name: 'active_connections',
        help: 'Number of active connections',
        labelNames: ['type']
    }),

    memoryUsage: new promClient.Gauge({
        name: 'memory_usage_bytes',
        help: 'Memory usage in bytes',
        labelNames: ['type']
    }),

    cpuUsage: new promClient.Gauge({
        name: 'cpu_usage_percent',
        help: 'CPU usage percentage'
    })
};

// Register all metrics
Object.values(metrics).forEach(metric => prometheus.registerMetric(metric));

// Monitoring system
const monitoring = {
    // Request monitoring middleware
    requestMiddleware: (req, res, next) => {
        const start = Date.now();
        const end = res.end;
        const json = res.json;

        res.end = function(chunk) {
            const duration = (Date.now() - start) / 1000;
            const route = req.route?.path || req.path || 'unknown';

            metrics.httpRequestDuration
                .labels(req.method, route, res.statusCode)
                .observe(duration);

            metrics.httpRequestCount
                .labels(req.method, route, res.statusCode)
                .inc();

            return end.call(this, chunk);
        };

        res.json = function(obj) {
            const duration = (Date.now() - start) / 1000;
            const route = req.route?.path || req.path || 'unknown';

            metrics.httpRequestDuration
                .labels(req.method, route, res.statusCode)
                .observe(duration);

            metrics.httpRequestCount
                .labels(req.method, route, res.statusCode)
                .inc();

            return json.call(this, obj);
        };

        next();
    },

    // Security event monitoring
    logSecurityEvent(eventType, severity, details = {}) {
        metrics.securityEvents
            .labels(eventType, severity)
            .inc();

        // Log structured security event
        const event = {
            timestamp: new Date().toISOString(),
            event_type: eventType,
            severity,
            details,
            correlation_id: generateCorrelationId(),
            environment: process.env.NODE_ENV || 'development'
        };

        console.log(JSON.stringify(event));

        // Send to external monitoring if configured
        if (process.env.SECURITY_MONITORING_URL) {
            sendToMonitoringService(event);
        }
    },

    // Authentication monitoring
    logAuthAttempt(authType, success, reason = '') {
        if (!success) {
            metrics.failedAuthAttempts
                .labels(authType, reason)
                .inc();
        }

        const event = {
            timestamp: new Date().toISOString(),
            auth_type: authType,
            success,
            reason,
            ip_address: getClientIP(),
            user_agent: this.getUserAgent()
        };

        console.log(JSON.stringify(event));
    },

    // RPA job monitoring
    logRPAJob(jobType, status, duration, details = {}) {
        metrics.rpaJobsProcessed
            .labels(jobType, status)
            .inc();

        metrics.rpaJobDuration
            .labels(jobType, status)
            .observe(duration);

        const job = {
            timestamp: new Date().toISOString(),
            job_type: jobType,
            status,
            duration,
            details,
            correlation_id: generateCorrelationId()
        };

        console.log(JSON.stringify(job));
    },

    // Database monitoring
    logDatabaseQuery(operation, table, duration, success = true) {
        metrics.databaseQueryDuration
            .labels(operation, table)
            .observe(duration);

        if (!success) {
            this.logSecurityEvent('DATABASE_ERROR', 'high', {
                operation,
                table,
                duration,
                error: 'Query failed'
            });
        }
    },

    // WhatsApp monitoring
    logWhatsAppMessage(messageType, direction, status, details = {}) {
        if (direction === 'sent') {
            metrics.whatsappMessagesSent
                .labels(messageType, status)
                .inc();
        } else {
            metrics.whatsappMessagesReceived
                .labels(messageType)
                .inc();
        }

        const message = {
            timestamp: new Date().toISOString(),
            message_type: messageType,
            direction,
            status,
            details
        };

        console.log(JSON.stringify(message));
    },

    // System metrics collection
    collectSystemMetrics() {
        const memUsage = process.memoryUsage();
        const cpuUsage = process.cpuUsage();

        metrics.memoryUsage
            .labels('heap_used')
            .set(memUsage.heapUsed);

        metrics.memoryUsage
            .labels('heap_total')
            .set(memUsage.heapTotal);

        metrics.memoryUsage
            .labels('rss')
            .set(memUsage.rss);

        metrics.cpuUsage
            .set(calculateCPUUsage(cpuUsage));
    },

    // Health check
    healthCheck() {
        return {
            status: 'healthy',
            timestamp: new Date().toISOString(),
            version: process.env.npm_package_version || '1.0.0',
            uptime: process.uptime(),
            memory: process.memoryUsage(),
            cpu: process.cpuUsage(),
            metrics: {
                http_requests: metrics.httpRequestCount.hashMap.size,
                security_events: metrics.securityEvents.hashMap.size,
                rpa_jobs: metrics.rpaJobsProcessed.hashMap.size
            },
            services: {
                database: checkDatabase(),
                redis: checkRedis(),
                whatsapp: checkWhatsApp()
            }
        };
    },

    // Prometheus metrics endpoint
    getMetrics: async () => {
        this.collectSystemMetrics();
        return await prometheus.metrics();
    },

    // Reset metrics (for testing)
    resetMetrics: async () => {
        return await prometheus.resetMetrics();
    }
};

// Utility functions
function generateCorrelationId() {
    return createHash('sha256')
        .update(Date.now().toString() + Math.random().toString())
        .digest('hex')
        .substring(0, 12);
}

function getClientIP() {
    // Implementation depends on proxy setup
    return '127.0.0.1'; // Placeholder
}

function getUserAgent() {
    // Implementation to get user agent from request context
    return 'Unknown'; // Placeholder
}

function calculateCPUUsage(cpuUsage) {
    // Calculate CPU usage percentage
    return 0; // Placeholder implementation
}

function checkDatabase() {
    // Check database connectivity
    return {
        status: 'healthy',
        response_time: 0 // Placeholder
    };
}

function checkRedis() {
    // Check Redis connectivity
    return {
        status: 'healthy',
        response_time: 0 // Placeholder
    };
}

function checkWhatsApp() {
    // Check WhatsApp API connectivity
    return {
        status: 'healthy',
        response_time: 0 // Placeholder
    };
}

function sendToMonitoringService(event) {
    // Send to external monitoring service (Datadog, Sentry, etc.)
    // Implementation depends on service
}

// Export monitoring system
module.exports = monitoring;