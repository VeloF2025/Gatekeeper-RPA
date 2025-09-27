/**
 * Alert Service
 * Automated performance monitoring and alerting system
 */

import { performanceMonitor } from './performance-monitor.service';
import { logger } from '@/lib/logger';
import { db } from '@/database/database';
import { security_events } from '@/database/schema';
import { eq, and, gte, lte } from 'drizzle-orm';

export interface AlertRule {
  id: string;
  name: string;
  description: string;
  metric: string;
  condition: 'gt' | 'lt' | 'eq' | 'between';
  threshold: number | [number, number];
  severity: 'low' | 'medium' | 'high' | 'critical';
  duration: number; // minutes
  enabled: boolean;
  notifications: NotificationChannel[];
}

export interface NotificationChannel {
  type: 'email' | 'webhook' | 'slack' | 'pagerduty';
  config: Record<string, any>;
}

export interface Alert {
  id: string;
  ruleId: string;
  ruleName: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
  message: string;
  value: number;
  threshold: number | [number, number];
  timestamp: Date;
  acknowledged: boolean;
  acknowledgedBy?: string;
  acknowledgedAt?: Date;
  resolved: boolean;
  resolvedAt?: Date;
  resolvedBy?: string;
  metadata?: Record<string, any>;
}

class AlertService {
  private rules: Map<string, AlertRule> = new Map();
  private activeAlerts: Map<string, Alert> = new Map();
  private checkInterval?: NodeJS.Timeout;

  constructor() {
    this.initializeDefaultRules();
  }

  /**
   * Initialize default alert rules
   */
  private initializeDefaultRules(): void {
    const defaultRules: AlertRule[] = [
      {
        id: 'cpu-high',
        name: 'High CPU Usage',
        description: 'CPU usage exceeds 80%',
        metric: 'system_cpu_usage',
        condition: 'gt',
        threshold: 80,
        severity: 'high',
        duration: 5,
        enabled: true,
        notifications: []
      },
      {
        id: 'memory-high',
        name: 'High Memory Usage',
        description: 'Memory usage exceeds 85%',
        metric: 'system_memory_usage',
        condition: 'gt',
        threshold: 85,
        severity: 'high',
        duration: 5,
        enabled: true,
        notifications: []
      },
      {
        id: 'response-time-slow',
        name: 'Slow API Response Time',
        description: 'Average response time exceeds 200ms',
        metric: 'api_request_duration',
        condition: 'gt',
        threshold: 200,
        severity: 'medium',
        duration: 3,
        enabled: true,
        notifications: []
      },
      {
        id: 'rpa-execution-slow',
        name: 'Slow RPA Execution',
        description: 'RPA job execution exceeds 30 seconds',
        metric: 'rpa_execution_duration',
        condition: 'gt',
        threshold: 30000,
        severity: 'medium',
        duration: 1,
        enabled: true,
        notifications: []
      },
      {
        id: 'error-rate-high',
        name: 'High Error Rate',
        description: 'Error rate exceeds 5%',
        metric: 'error_rate',
        condition: 'gt',
        threshold: 5,
        severity: 'high',
        duration: 5,
        enabled: true,
        notifications: []
      },
      {
        id: 'database-slow-query',
        name: 'Slow Database Query',
        description: 'Database query exceeds 100ms',
        metric: 'database_query_duration',
        condition: 'gt',
        threshold: 100,
        severity: 'medium',
        duration: 1,
        enabled: true,
        notifications: []
      }
    ];

    defaultRules.forEach(rule => {
      this.rules.set(rule.id, rule);
    });
  }

  /**
   * Start alert monitoring
   */
  start(): void {
    if (this.checkInterval) return;

    // Check alerts every 30 seconds
    this.checkInterval = setInterval(() => {
      this.checkAllRules();
    }, 30000);

    logger.info('Alert service started');
  }

  /**
   * Stop alert monitoring
   */
  stop(): void {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
      this.checkInterval = undefined;
    }

    logger.info('Alert service stopped');
  }

  /**
   * Add custom alert rule
   */
  addRule(rule: AlertRule): void {
    this.rules.set(rule.id, rule);
    logger.info('Alert rule added', { ruleId: rule.id, name: rule.name });
  }

  /**
   * Remove alert rule
   */
  removeRule(ruleId: string): void {
    this.rules.delete(ruleId);
    logger.info('Alert rule removed', { ruleId });
  }

  /**
   * Check all alert rules
   */
  private async checkAllRules(): Promise<void> {
    try {
      const now = new Date();
      const fiveMinutesAgo = new Date(now.getTime() - 5 * 60 * 1000);

      for (const rule of this.rules.values()) {
        if (!rule.enabled) continue;

        try {
          await this.checkRule(rule, fiveMinutesAgo);
        } catch (error) {
          logger.error('Failed to check alert rule', {
            ruleId: rule.id,
            error: error instanceof Error ? error.message : error
          });
        }
      }

      // Check for resolved alerts
      await this.checkResolvedAlerts();

    } catch (error) {
      logger.error('Failed to check alert rules', {
        error: error instanceof Error ? error.message : error
      });
    }
  }

  /**
   * Check individual alert rule
   */
  private async checkRule(rule: AlertRule, since: Date): Promise<void> {
    // Get aggregated metrics for the rule
    const metrics = await performanceMonitor.getAggregatedMetrics('1h');
    const metricData = metrics.find(m => m.name === rule.metric);

    if (!metricData) return;

    const value = metricData.avg || 0;
    const isTriggered = this.evaluateCondition(value, rule.condition, rule.threshold);

    if (isTriggered) {
      // Check if we already have an active alert for this rule
      const existingAlert = Array.from(this.activeAlerts.values())
        .find(alert => alert.ruleId === rule.id && !alert.resolved);

      if (!existingAlert) {
        // Create new alert
        const alert: Alert = {
          id: `alert-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
          ruleId: rule.id,
          ruleName: rule.name,
          severity: rule.severity,
          message: `${rule.name}: ${rule.description} (Current: ${value.toFixed(2)})`,
          value,
          threshold: rule.threshold,
          timestamp: new Date(),
          acknowledged: false,
          resolved: false,
          metadata: {
            metric: rule.metric,
            condition: rule.condition,
            duration: rule.duration
          }
        };

        this.activeAlerts.set(alert.id, alert);

        // Log security event for critical alerts
        if (rule.severity === 'critical') {
          await db.insert(security_events).values({
            event_type: 'performance_alert',
            severity: 'high',
            description: alert.message,
            details: {
              alertId: alert.id,
              ruleId: rule.id,
              value,
              threshold: rule.threshold
            },
            timestamp: alert.timestamp
          });
        }

        // Send notifications
        await this.sendNotifications(alert, rule.notifications);

        logger.warn('Alert triggered', {
          alertId: alert.id,
          ruleId: rule.id,
          severity: rule.severity,
          value,
          threshold: rule.threshold
        });
      }
    }
  }

  /**
   * Check if alerts should be resolved
   */
  private async checkResolvedAlerts(): Promise<void> {
    const now = new Date();
    const tenMinutesAgo = new Date(now.getTime() - 10 * 60 * 1000);

    for (const [alertId, alert] of this.activeAlerts) {
      if (!alert.resolved && alert.timestamp < tenMinutesAgo) {
        // Check if the condition is still met
        const rule = this.rules.get(alert.ruleId);
        if (rule) {
          const metrics = await performanceMonitor.getAggregatedMetrics('1h');
          const metricData = metrics.find(m => m.name === rule.metric);

          if (metricData) {
            const value = metricData.avg || 0;
            const isTriggered = this.evaluateCondition(value, rule.condition, rule.threshold);

            if (!isTriggered) {
              // Resolve the alert
              alert.resolved = true;
              alert.resolvedAt = new Date();

              logger.info('Alert resolved', {
                alertId,
                ruleId: rule.id,
                resolvedAt: alert.resolvedAt
              });

              // Remove from active alerts after some time
              setTimeout(() => {
                this.activeAlerts.delete(alertId);
              }, 60000); // Keep resolved alerts for 1 minute
            }
          }
        }
      }
    }
  }

  /**
   * Evaluate alert condition
   */
  private evaluateCondition(value: number, condition: string, threshold: number | [number, number]): boolean {
    switch (condition) {
      case 'gt':
        return value > (threshold as number);
      case 'lt':
        return value < (threshold as number);
      case 'eq':
        return value === (threshold as number);
      case 'between':
        const [min, max] = threshold as [number, number];
        return value >= min && value <= max;
      default:
        return false;
    }
  }

  /**
   * Send alert notifications
   */
  private async sendNotifications(alert: Alert, channels: NotificationChannel[]): Promise<void> {
    // In a real implementation, this would send emails, webhooks, etc.
    // For now, just log the notification
    logger.info('Sending alert notifications', {
      alertId: alert.id,
      channels: channels.map(c => c.type),
      message: alert.message
    });

    // Example: Send to console for development
    if (process.env.NODE_ENV === 'development') {
      console.log(`🚨 ALERT [${alert.severity.toUpperCase()}]: ${alert.message}`);
    }
  }

  /**
   * Get all active alerts
   */
  getActiveAlerts(): Alert[] {
    return Array.from(this.activeAlerts.values()).filter(alert => !alert.resolved);
  }

  /**
   * Get alert history
   */
  async getAlertHistory(limit: number = 100): Promise<Alert[]> {
    // In a real implementation, this would query from a database table
    return Array.from(this.activeAlerts.values())
      .sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime())
      .slice(0, limit);
  }

  /**
   * Acknowledge alert
   */
  acknowledgeAlert(alertId: string, acknowledgedBy: string): boolean {
    const alert = this.activeAlerts.get(alertId);
    if (alert && !alert.acknowledged) {
      alert.acknowledged = true;
      alert.acknowledgedBy = acknowledgedBy;
      alert.acknowledgedAt = new Date();

      logger.info('Alert acknowledged', {
        alertId,
        acknowledgedBy
      });

      return true;
    }
    return false;
  }

  /**
   * Get alert statistics
   */
  getAlertStats() {
    const alerts = Array.from(this.activeAlerts.values());
    const now = new Date();
    const last24h = new Date(now.getTime() - 24 * 60 * 60 * 1000);
    const last7d = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);

    return {
      active: alerts.filter(a => !a.resolved && !a.acknowledged).length,
      acknowledged: alerts.filter(a => !a.resolved && a.acknowledged).length,
      last24h: alerts.filter(a => a.timestamp >= last24h).length,
      last7d: alerts.filter(a => a.timestamp >= last7d).length,
      bySeverity: {
        critical: alerts.filter(a => a.severity === 'critical' && !a.resolved).length,
        high: alerts.filter(a => a.severity === 'high' && !a.resolved).length,
        medium: alerts.filter(a => a.severity === 'medium' && !a.resolved).length,
        low: alerts.filter(a => a.severity === 'low' && !a.resolved).length
      }
    };
  }
}

// Export singleton instance
export const alertService = new AlertService();

// Start alert service on module load
if (process.env.NODE_ENV !== 'test') {
  alertService.start();
}

export default alertService;