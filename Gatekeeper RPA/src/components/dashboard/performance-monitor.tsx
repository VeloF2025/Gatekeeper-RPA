/**
 * Performance Monitoring Dashboard Component
 *
 * Provides real-time performance monitoring, visualization,
 * and optimization controls for the Gatekeeper RPA system.
 */

'use client';

import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from '@/components/ui/alert';
import { Progress } from '@/components/ui/progress';
import {
  Activity,
  Cpu,
  MemoryStick,
  Database,
  AlertTriangle,
  CheckCircle,
  XCircle,
  RefreshCw,
} from 'lucide-react';

// Performance metrics types
interface PerformanceMetrics {
  timestamp: number;
  system: {
    cpu: {
      usage: number;
      loadAverage: number[];
    };
    memory: {
      total: number;
      used: number;
      free: number;
      percentage: number;
    };
    disk: {
      total: number;
      used: number;
      free: number;
      percentage: number;
    };
    network: {
      bytesIn: number;
      bytesOut: number;
      packetsIn: number;
      packetsOut: number;
    };
  };
  application: {
    uptime: number;
    memoryUsage: {
      rss: number;
      heapTotal: number;
      heapUsed: number;
      external: number;
    };
    eventLoop: {
      lag: number;
      handles: number;
      requests: number;
    };
  };
  database: {
    connections: number;
    maxConnections: number;
    queryCount: number;
    slowQueries: number;
    avgQueryTime: number;
  };
  rpa: {
    activeJobs: number;
    completedJobs: number;
    failedJobs: number;
    avgExecutionTime: number;
    successRate: number;
    throughput: number;
  };
}

interface PerformanceAlert {
  id: string;
  type: 'warning' | 'error' | 'critical';
  category: 'system' | 'application' | 'database' | 'rpa';
  message: string;
  value: number;
  threshold: number;
  timestamp: number;
  resolved: boolean;
}

export default function PerformanceMonitor() {
  const [metrics, setMetrics] = useState<PerformanceMetrics | null>(null);
  const [alerts, setAlerts] = useState<PerformanceAlert[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null);

  // Initialize WebSocket connection for real-time updates
  useEffect(() => {
    const connectWebSocket = () => {
      const eventSource = new EventSource('/api/performance/ws?clientId=dashboard');

      eventSource.onopen = () => {
        setIsConnected(true);
        console.log('📡 Performance monitoring connected');
      };

      eventSource.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);

          if (data.type === 'update' && data.metrics) {
            setMetrics(data.metrics);
            setLastUpdate(new Date());
          } else if (data.type === 'alert' && data.alert) {
            setAlerts(prev => [data.alert, ...prev].slice(0, 10));
          } else if (data.type === 'initial' && data.metrics) {
            setMetrics(data.metrics);
            setLastUpdate(new Date());
          }
        } catch (error) {
          console.error('Error processing WebSocket message:', error);
        }
      };

      eventSource.onerror = (error) => {
        console.error('WebSocket error:', error);
        setIsConnected(false);
        eventSource.close();

        // Attempt to reconnect after 5 seconds
        setTimeout(connectWebSocket, 5000);
      };

      return eventSource;
    };

    const eventSource = connectWebSocket();

    // Cleanup on unmount
    return () => {
      eventSource.close();
    };
  }, []);

  // Manual refresh
  const refreshData = async () => {
    setIsRefreshing(true);
    try {
      const response = await fetch('/api/performance');
      const data = await response.json();

      if (data.success && data.metrics) {
        setMetrics(data.metrics);
        setLastUpdate(new Date());
      }

      if (data.success && data.alerts) {
        setAlerts(data.alerts.active);
      }
    } catch (error) {
      console.error('Error refreshing performance data:', error);
    } finally {
      setIsRefreshing(false);
    }
  };

  // Format uptime
  const formatUptime = (seconds: number) => {
    const days = Math.floor(seconds / 86400);
    const hours = Math.floor((seconds % 86400) / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (days > 0) {
      return `${days}d ${hours}h ${minutes}m ${secs}s`;
    } else if (hours > 0) {
      return `${hours}h ${minutes}m ${secs}s`;
    } else if (minutes > 0) {
      return `${minutes}m ${secs}s`;
    } else {
      return `${secs}s`;
    }
  };

  // Format bytes
  const formatBytes = (bytes: number) => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  // Get health status
  const getHealthStatus = () => {
    if (!metrics) return { status: 'unknown', color: 'gray' };

    const issues = [];
    if (metrics.system.cpu.usage > 80) issues.push('CPU');
    if (metrics.system.memory.percentage > 85) issues.push('Memory');
    if (metrics.rpa.successRate < 90) issues.push('RPA Success Rate');

    if (issues.length === 0) {
      return { status: 'healthy', color: 'green' };
    } else if (issues.length <= 2) {
      return { status: 'degraded', color: 'yellow' };
    } else {
      return { status: 'critical', color: 'red' };
    }
  };

  const healthStatus = getHealthStatus();

  if (!metrics) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <RefreshCw className="h-8 w-8 animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Loading performance metrics...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Performance Monitor</h2>
          <p className="text-muted-foreground">
            Real-time system performance and optimization
          </p>
        </div>
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <div className={`w-2 h-2 rounded-full bg-${healthStatus.color}-500`} />
            <span className="text-sm font-medium capitalize">{healthStatus.status}</span>
          </div>
          <Badge variant={isConnected ? "default" : "destructive"}>
            {isConnected ? "Live" : "Disconnected"}
          </Badge>
          <Button
            variant="outline"
            size="sm"
            onClick={refreshData}
            disabled={isRefreshing}
          >
            <RefreshCw className={`h-4 w-4 mr-2 ${isRefreshing ? 'animate-spin' : ''}`} />
            Refresh
          </Button>
        </div>
      </div>

      {/* Alerts */}
      {alerts.length > 0 && (
        <div className="space-y-2">
          {alerts.slice(0, 3).map((alert) => (
            <Alert key={alert.id} className={alert.type === 'critical' ? 'border-red-500' : ''}>
              <AlertTriangle className="h-4 w-4" />
              <AlertTitle className="capitalize">{alert.type}</AlertTitle>
              <AlertDescription>{alert.message}</AlertDescription>
            </Alert>
          ))}
        </div>
      )}

      {/* Metrics Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">CPU Usage</CardTitle>
            <Cpu className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{metrics.system.cpu.usage.toFixed(1)}%</div>
            <Progress value={metrics.system.cpu.usage} className="mt-2" />
            <p className="text-xs text-muted-foreground mt-2">
              Load: {metrics.system.cpu.loadAverage[0].toFixed(2)}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Memory Usage</CardTitle>
            <MemoryStick className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{metrics.system.memory.percentage.toFixed(1)}%</div>
            <Progress value={metrics.system.memory.percentage} className="mt-2" />
            <p className="text-xs text-muted-foreground mt-2">
              {formatBytes(metrics.system.memory.used)} / {formatBytes(metrics.system.memory.total)}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">RPA Jobs</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{metrics.rpa.activeJobs}</div>
            <p className="text-xs text-muted-foreground mt-2">
              Success Rate: {metrics.rpa.successRate.toFixed(1)}%
            </p>
            <div className="flex items-center gap-2 mt-2">
              <CheckCircle className="h-3 w-3 text-green-500" />
              <span className="text-xs">{metrics.rpa.completedJobs}</span>
              <XCircle className="h-3 w-3 text-red-500" />
              <span className="text-xs">{metrics.rpa.failedJobs}</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Database</CardTitle>
            <Database className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{metrics.database.connections}</div>
            <p className="text-xs text-muted-foreground mt-2">
              Connections ({metrics.database.maxConnections} max)
            </p>
            <Progress
              value={(metrics.database.connections / metrics.database.maxConnections) * 100}
              className="mt-2"
            />
          </CardContent>
        </Card>
      </div>

      {/* Detailed Metrics */}
      <Tabs defaultValue="system" className="space-y-4">
        <TabsList>
          <TabsTrigger value="system">System</TabsTrigger>
          <TabsTrigger value="application">Application</TabsTrigger>
          <TabsTrigger value="rpa">RPA</TabsTrigger>
          <TabsTrigger value="alerts">Alerts</TabsTrigger>
        </TabsList>

        <TabsContent value="system" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>System Resources</CardTitle>
                <CardDescription>Current system resource utilization</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium">CPU Usage</span>
                    <span className="text-sm text-muted-foreground">
                      {metrics.system.cpu.usage.toFixed(1)}%
                    </span>
                  </div>
                  <Progress value={metrics.system.cpu.usage} />
                </div>

                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium">Memory Usage</span>
                    <span className="text-sm text-muted-foreground">
                      {metrics.system.memory.percentage.toFixed(1)}%
                    </span>
                  </div>
                  <Progress value={metrics.system.memory.percentage} />
                </div>

                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium">Disk Usage</span>
                    <span className="text-sm text-muted-foreground">
                      {metrics.system.disk.percentage.toFixed(1)}%
                    </span>
                  </div>
                  <Progress value={metrics.system.disk.percentage} />
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Load Average</CardTitle>
                <CardDescription>System load averages</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-sm">1 min:</span>
                    <span className="text-sm font-medium">
                      {metrics.system.cpu.loadAverage[0].toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm">5 min:</span>
                    <span className="text-sm font-medium">
                      {metrics.system.cpu.loadAverage[1].toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm">15 min:</span>
                    <span className="text-sm font-medium">
                      {metrics.system.cpu.loadAverage[2].toFixed(2)}
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="application" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>Application Status</CardTitle>
                <CardDescription>Application runtime information</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Uptime</span>
                  <span className="text-sm">{formatUptime(metrics.application.uptime)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">RSS Memory</span>
                  <span className="text-sm">{formatBytes(metrics.application.memoryUsage.rss)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Heap Used</span>
                  <span className="text-sm">{formatBytes(metrics.application.memoryUsage.heapUsed)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Heap Total</span>
                  <span className="text-sm">{formatBytes(metrics.application.memoryUsage.heapTotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Event Loop Lag</span>
                  <span className="text-sm">{metrics.application.eventLoop.lag.toFixed(1)}ms</span>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Event Loop</CardTitle>
                <CardDescription>Node.js event loop metrics</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Active Handles</span>
                  <span className="text-sm">{metrics.application.eventLoop.handles}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Active Requests</span>
                  <span className="text-sm">{metrics.application.eventLoop.requests}</span>
                </div>
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium">Event Loop Lag</span>
                    <span className="text-sm text-muted-foreground">
                      {metrics.application.eventLoop.lag.toFixed(1)}ms
                    </span>
                  </div>
                  <Progress
                    value={Math.min(metrics.application.eventLoop.lag / 100 * 100, 100)}
                  />
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="rpa" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>RPA Performance</CardTitle>
                <CardDescription>RPA job execution metrics</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Active Jobs</span>
                  <span className="text-sm">{metrics.rpa.activeJobs}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Completed Jobs</span>
                  <span className="text-sm">{metrics.rpa.completedJobs}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Failed Jobs</span>
                  <span className="text-sm">{metrics.rpa.failedJobs}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Success Rate</span>
                  <span className="text-sm">{metrics.rpa.successRate.toFixed(1)}%</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Avg Execution Time</span>
                  <span className="text-sm">
                    {(metrics.rpa.avgExecutionTime / 1000).toFixed(1)}s
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm font-medium">Throughput</span>
                  <span className="text-sm">{metrics.rpa.throughput.toFixed(1)} jobs/min</span>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>RPA Success Rate</CardTitle>
                <CardDescription>Job success rate over time</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium">Success Rate</span>
                    <span className="text-sm text-muted-foreground">
                      {metrics.rpa.successRate.toFixed(1)}%
                    </span>
                  </div>
                  <Progress value={metrics.rpa.successRate} />

                  {metrics.rpa.successRate < 95 && (
                    <Alert>
                      <AlertTriangle className="h-4 w-4" />
                      <AlertTitle>Low Success Rate</AlertTitle>
                      <AlertDescription>
                        RPA success rate is below 95%. Consider investigating job failures.
                      </AlertDescription>
                    </Alert>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="alerts" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Active Alerts</CardTitle>
              <CardDescription>Current system alerts and warnings</CardDescription>
            </CardHeader>
            <CardContent>
              {alerts.length === 0 ? (
                <div className="text-center py-8">
                  <CheckCircle className="h-8 w-8 text-green-500 mx-auto mb-4" />
                  <p className="text-muted-foreground">No active alerts</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {alerts.map((alert) => (
                    <div
                      key={alert.id}
                      className={`p-4 rounded-lg border ${
                        alert.type === 'critical'
                          ? 'border-red-200 bg-red-50'
                          : alert.type === 'error'
                          ? 'border-orange-200 bg-orange-50'
                          : 'border-yellow-200 bg-yellow-50'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <AlertTriangle className={`h-4 w-4 ${
                            alert.type === 'critical'
                              ? 'text-red-500'
                              : alert.type === 'error'
                              ? 'text-orange-500'
                              : 'text-yellow-500'
                          }`} />
                          <span className="font-medium capitalize">{alert.type}</span>
                          <Badge variant="outline">{alert.category}</Badge>
                        </div>
                        <span className="text-sm text-muted-foreground">
                          {new Date(alert.timestamp).toLocaleString()}
                        </span>
                      </div>
                      <p className="text-sm mt-2">{alert.message}</p>
                      <div className="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
                        <span>Value: {alert.value.toFixed(1)}</span>
                        <span>Threshold: {alert.threshold.toFixed(1)}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Footer */}
      <div className="flex items-center justify-between text-sm text-muted-foreground">
        <div>
          Last updated: {lastUpdate ? lastUpdate.toLocaleString() : 'Never'}
        </div>
        <div>
          Monitoring {Object.keys(metrics).length} metrics categories
        </div>
      </div>
    </div>
  );
}