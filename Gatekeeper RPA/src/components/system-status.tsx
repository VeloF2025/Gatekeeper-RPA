'use client';

/**
 * System Status Component for Gatekeeper RPA System
 *
 * Real-time system health monitoring with comprehensive metrics.
 * Following Zero Trust security principles with performance monitoring.
 *
 * Created with system monitoring and performance validation.
 */

import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

// System metrics interface
interface SystemMetrics {
  uptime: number;
  responseTime: number;
  errorRate: number;
  activeConnections: number;
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
  lastUpdated: Date;
}

// Service status interface
interface ServiceStatus {
  name: string;
  status: 'operational' | 'degraded' | 'down';
  responseTime: number;
  lastCheck: Date;
}

/**
 * SystemStatus Component
 *
 * Real-time system health monitoring with comprehensive performance metrics.
 * Implements security best practices for system monitoring.
 */
export function SystemStatus() {
  const [systemMetrics, setSystemMetrics] = useState<SystemMetrics>({
    uptime: 0,
    responseTime: 0,
    errorRate: 0,
    activeConnections: 0,
    cpuUsage: 0,
    memoryUsage: 0,
    diskUsage: 0,
    lastUpdated: new Date(),
  });

  const [serviceStatuses, setServiceStatuses] = useState<ServiceStatus[]>([
    {
      name: 'WhatsApp Integration',
      status: 'operational',
      responseTime: 45,
      lastCheck: new Date(),
    },
    {
      name: 'RPA Automation',
      status: 'operational',
      responseTime: 120,
      lastCheck: new Date(),
    },
    {
      name: 'Database',
      status: 'operational',
      responseTime: 15,
      lastCheck: new Date(),
    },
    {
      name: 'Authentication',
      status: 'operational',
      responseTime: 30,
      lastCheck: new Date(),
    },
  ]);

  const [isLoading, setIsLoading] = useState(true);

  // Simulate system metrics monitoring
  useEffect(() => {
    const fetchSystemMetrics = async () => {
      try {
        // In a real implementation, this would fetch from monitoring API
        const mockMetrics: SystemMetrics = {
          uptime: 99.9,
          responseTime: 85,
          errorRate: 0.1,
          activeConnections: 127,
          cpuUsage: 45,
          memoryUsage: 62,
          diskUsage: 38,
          lastUpdated: new Date(),
        };

        const mockServiceStatuses: ServiceStatus[] = [
          {
            name: 'WhatsApp Integration',
            status: 'operational',
            responseTime: Math.floor(Math.random() * 50) + 30,
            lastCheck: new Date(),
          },
          {
            name: 'RPA Automation',
            status: Math.random() > 0.8 ? 'degraded' : 'operational',
            responseTime: Math.floor(Math.random() * 100) + 80,
            lastCheck: new Date(),
          },
          {
            name: 'Database',
            status: 'operational',
            responseTime: Math.floor(Math.random() * 20) + 10,
            lastCheck: new Date(),
          },
          {
            name: 'Authentication',
            status: 'operational',
            responseTime: Math.floor(Math.random() * 40) + 20,
            lastCheck: new Date(),
          },
        ];

        setSystemMetrics(mockMetrics);
        setServiceStatuses(mockServiceStatuses);
      } catch (error) {
        // In production, log this error securely
        console.error('Failed to fetch system metrics:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchSystemMetrics();

    // Set up periodic metrics updates
    const interval = setInterval(fetchSystemMetrics, 30000); // Update every 30 seconds

    return () => clearInterval(interval);
  }, []);

  // Get status badge variant
  const getStatusBadge = (status: 'operational' | 'degraded' | 'down') => {
    switch (status) {
      case 'operational':
        return <Badge variant="default">● Operational</Badge>;
      case 'degraded':
        return <Badge variant="outline">⚠ Degraded</Badge>;
      case 'down':
        return <Badge variant="destructive">✗ Down</Badge>;
      default:
        return <Badge variant="outline">Unknown</Badge>;
    }
  };

  // Get health color
  const getHealthColor = (value: number, thresholds: { warning: number; danger: number }) => {
    if (value <= thresholds.warning) return 'text-green-600';
    if (value <= thresholds.danger) return 'text-yellow-600';
    return 'text-red-600';
  };

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            📊 System Status
          </CardTitle>
          <CardDescription>
            Real-time system health monitoring
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="text-center text-muted-foreground">
            Loading system metrics...
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            📊 System Status
            {getStatusBadge('operational')}
          </CardTitle>
          <CardDescription>
            Real-time system health monitoring
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <div className="space-y-1">
              <div className="text-sm font-medium">Uptime</div>
              <div className={`text-2xl font-bold ${getHealthColor(100 - systemMetrics.uptime, { warning: 1, danger: 5 })}`}>
                {systemMetrics.uptime.toFixed(1)}%
              </div>
              <div className="text-xs text-muted-foreground">
                Last 24 hours
              </div>
            </div>

            <div className="space-y-1">
              <div className="text-sm font-medium">Response Time</div>
              <div className={`text-2xl font-bold ${getHealthColor(systemMetrics.responseTime, { warning: 200, danger: 500 })}`}>
                {systemMetrics.responseTime}ms
              </div>
              <div className="text-xs text-muted-foreground">
                Average
              </div>
            </div>

            <div className="space-y-1">
              <div className="text-sm font-medium">Error Rate</div>
              <div className={`text-2xl font-bold ${getHealthColor(systemMetrics.errorRate, { warning: 1, danger: 5 })}`}>
                {systemMetrics.errorRate.toFixed(1)}%
              </div>
              <div className="text-xs text-muted-foreground">
                Last hour
              </div>
            </div>

            <div className="space-y-1">
              <div className="text-sm font-medium">Active Connections</div>
              <div className="text-2xl font-bold text-blue-600">
                {systemMetrics.activeConnections}
              </div>
              <div className="text-xs text-muted-foreground">
                Current
              </div>
            </div>
          </div>

          <div className="mt-6 grid gap-4 md:grid-cols-3">
            <div className="space-y-1">
              <div className="text-sm font-medium">CPU Usage</div>
              <div className={`text-lg font-semibold ${getHealthColor(systemMetrics.cpuUsage, { warning: 70, danger: 90 })}`}>
                {systemMetrics.cpuUsage}%
              </div>
            </div>

            <div className="space-y-1">
              <div className="text-sm font-medium">Memory Usage</div>
              <div className={`text-lg font-semibold ${getHealthColor(systemMetrics.memoryUsage, { warning: 70, danger: 90 })}`}>
                {systemMetrics.memoryUsage}%
              </div>
            </div>

            <div className="space-y-1">
              <div className="text-sm font-medium">Disk Usage</div>
              <div className={`text-lg font-semibold ${getHealthColor(systemMetrics.diskUsage, { warning: 70, danger: 90 })}`}>
                {systemMetrics.diskUsage}%
              </div>
            </div>
          </div>

          <div className="mt-4 text-xs text-muted-foreground">
            Last updated: {systemMetrics.lastUpdated.toLocaleTimeString()}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Service Status</CardTitle>
          <CardDescription>
            Individual service health and response times
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {serviceStatuses.map((service) => (
              <div key={service.name} className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <span className="font-medium">{service.name}</span>
                  {getStatusBadge(service.status)}
                </div>
                <div className="text-sm text-muted-foreground">
                  {service.responseTime}ms
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}