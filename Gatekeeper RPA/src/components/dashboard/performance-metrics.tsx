/**
 * Performance Metrics Dashboard Component
 * Real-time performance monitoring with visualizations
 */

'use client';

import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import {
  Activity,
  Zap,
  Database,
  Server,
  Clock,
  AlertTriangle,
  CheckCircle
} from 'lucide-react';

interface PerformanceData {
  timestamp: string;
  cpu: number;
  memory: number;
  disk: number;
  requests: number;
  responseTime: number;
}

interface Alert {
  id: string;
  type: 'warning' | 'error' | 'info';
  message: string;
  timestamp: string;
  resolved: boolean;
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

export function PerformanceMetrics() {
  const [timeRange, setTimeRange] = useState<'1h' | '24h' | '7d' | '30d'>('1h');
  const [isLoading, setIsLoading] = useState(false);
  const [performanceData, setPerformanceData] = useState<PerformanceData[]>([]);
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [currentMetrics, setCurrentMetrics] = useState({
    cpu: 45,
    memory: 67,
    disk: 34,
    responseTime: 145,
    uptime: '15d 4h',
    activeRequests: 23
  });

  // Simulate real-time data updates
  useEffect(() => {
    const interval = setInterval(() => {
      generatePerformanceData();
    }, 5000);

    // Initial data load
    generatePerformanceData();

    return () => clearInterval(interval);
  }, [timeRange]);

  const generatePerformanceData = () => {
    const newData: PerformanceData[] = [];
    const now = new Date();

    for (let i = 11; i >= 0; i--) {
      const timestamp = new Date(now.getTime() - i * 5 * 60 * 1000);
      newData.push({
        timestamp: timestamp.toLocaleTimeString(),
        cpu: Math.max(0, Math.min(100, currentMetrics.cpu + (Math.random() * 10 - 5))),
        memory: Math.max(0, Math.min(100, currentMetrics.memory + (Math.random() * 5 - 2.5))),
        disk: Math.max(0, Math.min(100, currentMetrics.disk + Math.random() * 2 - 1)),
        requests: Math.floor(Math.random() * 50) + 10,
        responseTime: Math.max(50, Math.min(500, currentMetrics.responseTime + (Math.random() * 50 - 25)))
      });
    }

    setPerformanceData(newData);

    // Update current metrics
    setCurrentMetrics(prev => ({
      ...prev,
      cpu: newData[newData.length - 1].cpu,
      memory: newData[newData.length - 1].memory,
      responseTime: newData[newData.length - 1].responseTime
    }));

    // Generate alerts based on thresholds
    generateAlerts(newData[newData.length - 1]);
  };

  const generateAlerts = (data: PerformanceData) => {
    const newAlerts: Alert[] = [];

    if (data.cpu > 80) {
      newAlerts.push({
        id: Date.now().toString(),
        type: 'error',
        message: `High CPU usage detected: ${data.cpu.toFixed(1)}%`,
        timestamp: new Date().toISOString(),
        resolved: false
      });
    }

    if (data.memory > 85) {
      newAlerts.push({
        id: (Date.now() + 1).toString(),
        type: 'error',
        message: `High memory usage detected: ${data.memory.toFixed(1)}%`,
        timestamp: new Date().toISOString(),
        resolved: false
      });
    }

    if (data.responseTime > 300) {
      newAlerts.push({
        id: (Date.now() + 2).toString(),
        type: 'warning',
        message: `Slow response time detected: ${data.responseTime.toFixed(0)}ms`,
        timestamp: new Date().toISOString(),
        resolved: false
      });
    }

    if (newAlerts.length > 0) {
      setAlerts(prev => [...newAlerts, ...prev.slice(0, 4)]);
    }
  };

  const resolveAlert = (id: string) => {
    setAlerts(prev => prev.map(alert =>
      alert.id === id ? { ...alert, resolved: true } : alert
    ));
  };

  const getStatusColor = (value: number, type: 'cpu' | 'memory' | 'response') => {
    if (type === 'response') {
      return value < 200 ? 'text-green-600' : value < 300 ? 'text-yellow-600' : 'text-red-600';
    }
    return value < 70 ? 'text-green-600' : value < 85 ? 'text-yellow-600' : 'text-red-600';
  };

  const getStatusIcon = (value: number, type: 'cpu' | 'memory' | 'response') => {
    if (type === 'response') {
      return value < 200 ? CheckCircle : value < 300 ? AlertTriangle : AlertTriangle;
    }
    return value < 70 ? CheckCircle : value < 85 ? AlertTriangle : AlertTriangle;
  };

  const handleExport = async () => {
    setIsLoading(true);
    try {
      // Simulate export functionality
      await new Promise(resolve => setTimeout(resolve, 2000));

      // Create export data
      const exportData = {
        metrics: currentMetrics,
        performanceData: performanceData,
        alerts: alerts,
        exportTime: new Date().toISOString(),
        timeRange
      };

      // Create and download file
      const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `performance-metrics-${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Export failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header with controls */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold">Performance Metrics</h2>
          <p className="text-muted-foreground">Real-time system performance monitoring</p>
        </div>
        <div className="flex flex-wrap gap-2">
          {(['1h', '24h', '7d', '30d'] as const).map((range) => (
            <Button
              key={range}
              variant={timeRange === range ? 'default' : 'outline'}
              size="sm"
              onClick={() => setTimeRange(range)}
            >
              {range}
            </Button>
          ))}
          <Button variant="outline" size="sm" disabled={isLoading} onClick={handleExport}>
            <Activity className="h-4 w-4 mr-2" />
            Export
          </Button>
        </div>
      </div>

      {/* Current metrics grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">CPU Usage</CardTitle>
            <Server className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="flex items-center space-x-2">
              <div className={`text-2xl font-bold ${getStatusColor(currentMetrics.cpu, 'cpu')}`}>
                {currentMetrics.cpu.toFixed(1)}%
              </div>
              {React.createElement(getStatusIcon(currentMetrics.cpu, 'cpu'), {
                className: `h-4 w-4 ${getStatusColor(currentMetrics.cpu, 'cpu')}`
              })}
            </div>
            <p className="text-xs text-muted-foreground">
              {currentMetrics.cpu < 70 ? 'Optimal' : currentMetrics.cpu < 85 ? 'Warning' : 'Critical'}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Memory Usage</CardTitle>
            <Database className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="flex items-center space-x-2">
              <div className={`text-2xl font-bold ${getStatusColor(currentMetrics.memory, 'memory')}`}>
                {currentMetrics.memory.toFixed(1)}%
              </div>
              {React.createElement(getStatusIcon(currentMetrics.memory, 'memory'), {
                className: `h-4 w-4 ${getStatusColor(currentMetrics.memory, 'memory')}`
              })}
            </div>
            <p className="text-xs text-muted-foreground">
              {currentMetrics.memory < 70 ? 'Optimal' : currentMetrics.memory < 85 ? 'Warning' : 'Critical'}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Response Time</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="flex items-center space-x-2">
              <div className={`text-2xl font-bold ${getStatusColor(currentMetrics.responseTime, 'response')}`}>
                {currentMetrics.responseTime.toFixed(0)}ms
              </div>
              {React.createElement(getStatusIcon(currentMetrics.responseTime, 'response'), {
                className: `h-4 w-4 ${getStatusColor(currentMetrics.responseTime, 'response')}`
              })}
            </div>
            <p className="text-xs text-muted-foreground">
              {currentMetrics.responseTime < 200 ? 'Fast' : currentMetrics.responseTime < 300 ? 'Slow' : 'Very Slow'}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Requests</CardTitle>
            <Zap className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{currentMetrics.activeRequests}</div>
            <p className="text-xs text-muted-foreground">
              Concurrent requests
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">System Uptime</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{currentMetrics.uptime}</div>
            <p className="text-xs text-muted-foreground">
              Since last restart
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Performance charts */}
      <div className="grid gap-6 lg:grid-cols-2">
        {/* Resource usage chart */}
        <Card>
          <CardHeader>
            <CardTitle>Resource Usage Over Time</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={performanceData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis />
                <Tooltip />
                <Area
                  type="monotone"
                  dataKey="cpu"
                  stackId="1"
                  stroke="#8884d8"
                  fill="#8884d8"
                  fillOpacity={0.6}
                  name="CPU %"
                />
                <Area
                  type="monotone"
                  dataKey="memory"
                  stackId="2"
                  stroke="#82ca9d"
                  fill="#82ca9d"
                  fillOpacity={0.6}
                  name="Memory %"
                />
              </AreaChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Response time chart */}
        <Card>
          <CardHeader>
            <CardTitle>Response Time Trends</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={performanceData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis />
                <Tooltip />
                <Line
                  type="monotone"
                  dataKey="responseTime"
                  stroke="#ff7300"
                  strokeWidth={2}
                  name="Response Time (ms)"
                />
                {/* Target line */}
                <Line
                  type="monotone"
                  dataKey={() => 200}
                  stroke="#00ff00"
                  strokeDasharray="5 5"
                  strokeWidth={1}
                  name="Target (200ms)"
                  dot={false}
                />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Request distribution */}
        <Card>
          <CardHeader>
            <CardTitle>Request Distribution</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={[
                    { name: 'API Requests', value: 65 },
                    { name: 'Database Queries', value: 20 },
                    { name: 'RPA Operations', value: 10 },
                    { name: 'WebSocket', value: 5 }
                  ]}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }: any) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {[
                    { name: 'API Requests', value: 65 },
                    { name: 'Database Queries', value: 20 },
                    { name: 'RPA Operations', value: 10 },
                    { name: 'WebSocket', value: 5 }
                  ].map((_, index: number) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Active alerts */}
        <Card>
          <CardHeader>
            <CardTitle>Active Alerts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3 max-h-[300px] overflow-y-auto">
              {alerts.length === 0 ? (
                <div className="text-center py-8 text-muted-foreground">
                  <CheckCircle className="h-12 w-12 mx-auto mb-2 text-green-500" />
                  <p>No active alerts</p>
                </div>
              ) : (
                alerts.map((alert) => (
                  <div
                    key={alert.id}
                    className={`p-3 rounded-lg border ${
                      alert.resolved
                        ? 'bg-muted/50 border-muted'
                        : alert.type === 'error'
                        ? 'bg-enterprise-error-50 border-enterprise-error-200'
                        : 'bg-enterprise-warning-50 border-enterprise-warning-200'
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex items-start space-x-3">
                        {alert.type === 'error' ? (
                          <AlertTriangle className="h-5 w-5 text-red-500 mt-0.5" />
                        ) : (
                          <AlertTriangle className="h-5 w-5 text-yellow-500 mt-0.5" />
                        )}
                        <div>
                          <p className={`text-sm font-medium ${
                            alert.resolved ? 'text-muted-foreground' :
                            alert.type === 'error' ? 'text-enterprise-error-800' : 'text-enterprise-warning-800'
                          }`}>
                            {alert.message}
                          </p>
                          <p className="text-xs text-muted-foreground mt-1">
                            {new Date(alert.timestamp).toLocaleString()}
                          </p>
                        </div>
                      </div>
                      {!alert.resolved && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => resolveAlert(alert.id)}
                        >
                          Resolve
                        </Button>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}