/**
 * Application Metrics Collection
 * Prometheus metrics for monitoring and alerting
 */

import client from 'prom-client';

// Initialize Prometheus client
const register = new client.Registry();

// Default metrics (CPU, memory, etc.)
client.collectDefaultMetrics({
  register,
  prefix: 'gatekeeper_rpa_',
  labels: { app: 'gatekeeper-rpa' },
});

// HTTP Request Metrics
const httpRequestDurationMicroseconds = new client.Histogram({
  name: 'gatekeeper_rpa_http_request_duration_seconds',
  help: 'Duration of HTTP requests in seconds',
  labelNames: ['method', 'route', 'status_code'],
  buckets: [0.1, 0.5, 1, 2, 5, 10],
  registers: [register],
});

const httpRequestsTotal = new client.Counter({
  name: 'gatekeeper_rpa_http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code'],
  registers: [register],
});

// Authentication Metrics
const authAttemptsTotal = new client.Counter({
  name: 'gatekeeper_rpa_auth_attempts_total',
  help: 'Total number of authentication attempts',
  labelNames: ['result', 'auth_method'],
  registers: [register],
});

const authFailedAttemptsTotal = new client.Counter({
  name: 'gatekeeper_rpa_auth_failed_attempts_total',
  help: 'Total number of failed authentication attempts',
  labelNames: ['reason', 'ip_address'],
  registers: [register],
});

// Security Metrics
const securityEventsTotal = new client.Counter({
  name: 'gatekeeper_rpa_security_events_total',
  help: 'Total number of security events',
  labelNames: ['event_type', 'severity', 'source'],
  registers: [register],
});

const suspiciousActivitiesTotal = new client.Counter({
  name: 'gatekeeper_rpa_suspicious_activities_total',
  help: 'Total number of suspicious activities detected',
  labelNames: ['activity_type', 'threat_level'],
  registers: [register],
});

// Business Metrics
const ticketsProcessedTotal = new client.Counter({
  name: 'gatekeeper_rpa_tickets_processed_total',
  help: 'Total number of tickets processed',
  labelNames: ['status', 'priority', 'source'],
  registers: [register],
});

const auditsCompletedTotal = new client.Counter({
  name: 'gatekeeper_rpa_audits_completed_total',
  help: 'Total number of audits completed',
  labelNames: ['audit_type', 'result', 'duration_range'],
  registers: [register],
});

const auditFailuresTotal = new client.Counter({
  name: 'gatekeeper_rpa_audit_failures_total',
  help: 'Total number of audit failures',
  labelNames: ['failure_reason', 'audit_type'],
  registers: [register],
});

// WhatsApp Metrics
const whatsappMessagesSentTotal = new client.Counter({
  name: 'gatekeeper_rpa_whatsapp_messages_sent_total',
  help: 'Total number of WhatsApp messages sent',
  labelNames: ['message_type', 'status', 'recipient_type'],
  registers: [register],
});

const whatsappMessagesReceivedTotal = new client.Counter({
  name: 'gatekeeper_rpa_whatsapp_messages_received_total',
  help: 'Total number of WhatsApp messages received',
  labelNames: ['message_type', 'source'],
  registers: [register],
});

// RPA Metrics
const rpaWorkerUtilization = new client.Gauge({
  name: 'gatekeeper_rpa_rpa_worker_utilization',
  help: 'RPA worker utilization percentage',
  labelNames: ['worker_id', 'task_type'],
  registers: [register],
});

const rpaTasksCompletedTotal = new client.Counter({
  name: 'gatekeeper_rpa_rpa_tasks_completed_total',
  help: 'Total number of RPA tasks completed',
  labelNames: ['task_type', 'status', 'duration_range'],
  registers: [register],
});

const rpaErrorsTotal = new client.Counter({
  name: 'gatekeeper_rpa_rpa_errors_total',
  help: 'Total number of RPA errors',
  labelNames: ['error_type', 'task_type', 'worker_id'],
  registers: [register],
});

// Database Metrics
const databaseQueryDuration = new client.Histogram({
  name: 'gatekeeper_rpa_database_query_duration_seconds',
  help: 'Duration of database queries in seconds',
  labelNames: ['query_type', 'table', 'operation'],
  buckets: [0.01, 0.05, 0.1, 0.5, 1, 5],
  registers: [register],
});

const databaseConnectionsActive = new client.Gauge({
  name: 'gatekeeper_rpa_database_connections_active',
  help: 'Number of active database connections',
  labelNames: ['database_type'],
  registers: [register],
});

// Queue Metrics
const queueLength = new client.Gauge({
  name: 'gatekeeper_rpa_queue_length',
  help: 'Number of items in processing queue',
  labelNames: ['queue_name', 'priority'],
  registers: [register],
});

const queueProcessingDuration = new client.Histogram({
  name: 'gatekeeper_rpa_queue_processing_duration_seconds',
  help: 'Duration of queue processing in seconds',
  labelNames: ['queue_name', 'task_type'],
  buckets: [0.1, 0.5, 1, 5, 10, 30],
  registers: [register],
});

// Performance Metrics
const memoryUsageBytes = new client.Gauge({
  name: 'gatekeeper_rpa_memory_usage_bytes',
  help: 'Memory usage in bytes',
  labelNames: ['type'],
  registers: [register],
});

const cpuUsagePercent = new client.Gauge({
  name: 'gatekeeper_rpa_cpu_usage_percent',
  help: 'CPU usage percentage',
  labelNames: ['core'],
  registers: [register],
});

// Error Metrics
const errorsTotal = new client.Counter({
  name: 'gatekeeper_rpa_errors_total',
  help: 'Total number of errors',
  labelNames: ['error_type', 'component', 'severity'],
  registers: [register],
});

// User Metrics
const activeUsers = new client.Gauge({
  name: 'gatekeeper_rpa_active_users',
  help: 'Number of active users',
  labelNames: ['role'],
  registers: [register],
});

const userSessionsTotal = new client.Counter({
  name: 'gatekeeper_rpa_user_sessions_total',
  help: 'Total number of user sessions',
  labelNames: ['action', 'auth_method'],
  registers: [register],
});

// Integration Metrics
const integrationLatency = new client.Histogram({
  name: 'gatekeeper_rpa_integration_latency_seconds',
  help: 'Integration API latency in seconds',
  labelNames: ['integration_name', 'endpoint', 'status'],
  buckets: [0.1, 0.5, 1, 2, 5, 10],
  registers: [register],
});

const integrationErrorsTotal = new client.Counter({
  name: 'gatekeeper_rpa_integration_errors_total',
  help: 'Total number of integration errors',
  labelNames: ['integration_name', 'error_type', 'endpoint'],
  registers: [register],
});

// Export metrics endpoint
export async function getMetrics(): Promise<string> {
  return await register.metrics();
}

// Helper functions for incrementing counters
export const metrics = {
  // HTTP metrics
  recordHttpRequest: (method: string, route: string, statusCode: string, duration: number) => {
    httpRequestDurationMicroseconds.observe({ method, route, status_code: statusCode }, duration);
    httpRequestsTotal.inc({ method, route, status_code: statusCode });
  },

  // Authentication metrics
  recordAuthAttempt: (result: string, authMethod: string) => {
    authAttemptsTotal.inc({ result, auth_method: authMethod });
  },

  recordAuthFailure: (reason: string, ipAddress: string) => {
    authFailedAttemptsTotal.inc({ reason, ip_address: ipAddress });
  },

  // Security metrics
  recordSecurityEvent: (eventType: string, severity: string, source: string) => {
    securityEventsTotal.inc({ event_type: eventType, severity, source });
  },

  recordSuspiciousActivity: (activityType: string, threatLevel: string) => {
    suspiciousActivitiesTotal.inc({ activity_type: activityType, threat_level: threatLevel });
  },

  // Business metrics
  recordTicketProcessed: (status: string, priority: string, source: string) => {
    ticketsProcessedTotal.inc({ status, priority, source });
  },

  recordAuditCompleted: (auditType: string, result: string, duration: number) => {
    const durationRange = getDurationRange(duration);
    auditsCompletedTotal.inc({ audit_type: auditType, result, duration_range });
  },

  recordAuditFailure: (failureReason: string, auditType: string) => {
    auditFailuresTotal.inc({ failure_reason: failureReason, audit_type: auditType });
  },

  // WhatsApp metrics
  recordWhatsAppMessageSent: (messageType: string, status: string, recipientType: string) => {
    whatsappMessagesSentTotal.inc({ message_type: messageType, status, recipient_type: recipientType });
  },

  recordWhatsAppMessageReceived: (messageType: string, source: string) => {
    whatsappMessagesReceivedTotal.inc({ message_type: messageType, source });
  },

  // RPA metrics
  setRPAWorkerUtilization: (workerId: string, taskType: string, utilization: number) => {
    rpaWorkerUtilization.set({ worker_id: workerId, task_type: taskType }, utilization);
  },

  recordRPATaskCompleted: (taskType: string, status: string, duration: number) => {
    const durationRange = getDurationRange(duration);
    rpaTasksCompletedTotal.inc({ task_type: taskType, status, duration_range });
  },

  recordRPAError: (errorType: string, taskType: string, workerId: string) => {
    rpaErrorsTotal.inc({ error_type: errorType, task_type: taskType, worker_id: workerId });
  },

  // Database metrics
  recordDatabaseQuery: (queryType: string, table: string, operation: string, duration: number) => {
    databaseQueryDuration.observe({ query_type: queryType, table, operation }, duration);
  },

  setDatabaseConnections: (databaseType: string, connections: number) => {
    databaseConnectionsActive.set({ database_type: databaseType }, connections);
  },

  // Queue metrics
  setQueueLength: (queueName: string, priority: string, length: number) => {
    queueLength.set({ queue_name: queueName, priority }, length);
  },

  recordQueueProcessing: (queueName: string, taskType: string, duration: number) => {
    queueProcessingDuration.observe({ queue_name: queueName, task_type }, duration);
  },

  // Performance metrics
  setMemoryUsage: (type: string, bytes: number) => {
    memoryUsageBytes.set({ type }, bytes);
  },

  setCPUUsage: (core: string, percent: number) => {
    cpuUsagePercent.set({ core }, percent);
  },

  // Error metrics
  recordError: (errorType: string, component: string, severity: string) => {
    errorsTotal.inc({ error_type: errorType, component, severity });
  },

  // User metrics
  setActiveUsers: (role: string, count: number) => {
    activeUsers.set({ role }, count);
  },

  recordUserSession: (action: string, authMethod: string) => {
    userSessionsTotal.inc({ action, auth_method: authMethod });
  },

  // Integration metrics
  recordIntegrationLatency: (integrationName: string, endpoint: string, status: string, duration: number) => {
    integrationLatency.observe({ integration_name: integrationName, endpoint, status }, duration);
  },

  recordIntegrationError: (integrationName: string, errorType: string, endpoint: string) => {
    integrationErrorsTotal.inc({ integration_name: integrationName, error_type: errorType, endpoint });
  },
};

// Helper function to categorize duration ranges
function getDurationRange(duration: number): string {
  if (duration < 1) return '0-1s';
  if (duration < 5) return '1-5s';
  if (duration < 10) return '5-10s';
  if (duration < 30) return '10-30s';
  if (duration < 60) return '30-60s';
  return '60s+';
}

// Export for testing
export { register };
export default metrics;