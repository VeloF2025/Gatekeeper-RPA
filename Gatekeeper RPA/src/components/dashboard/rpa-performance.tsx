/**
 * RPA Performance Monitor Component
 * Tracks RPA execution metrics and job performance
 */

'use client';

import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import {
  Bot,
  Clock,
  CheckCircle,
  XCircle,
  Activity,
  Zap,
  TrendingUp,
  AlertTriangle,
  Pause
} from 'lucide-react';

interface RPAJob {
  id: string;
  drNumber: string;
  status: 'running' | 'completed' | 'failed' | 'queued' | 'paused';
  progress: number;
  startTime: string;
  duration?: number;
  estimatedTime?: number;
  error?: string;
}

interface RPAStats {
  totalJobs: number;
  completedJobs: number;
  failedJobs: number;
  averageExecutionTime: number;
  successRate: number;
  jobsPerHour: number;
  queueLength: number;
}

interface PerformanceTrend {
  timestamp: string;
  executionTime: number;
  successRate: number;
  jobsProcessed: number;
}

export function RPAPerformance() {
  const [jobs, setJobs] = useState<RPAJob[]>([]);
  const [stats, setStats] = useState<RPAStats>({
    totalJobs: 0,
    completedJobs: 0,
    failedJobs: 0,
    averageExecutionTime: 0,
    successRate: 0,
    jobsPerHour: 0,
    queueLength: 0
  });
  const [trends, setTrends] = useState<PerformanceTrend[]>([]);

  // Simulate real-time updates
  useEffect(() => {
    const interval = setInterval(() => {
      updateJobs();
      updateStats();
    }, 3000);

    // Initial load
    updateJobs();
    updateStats();
    generateTrends();

    return () => clearInterval(interval);
  }, []);

  const updateJobs = () => {
    // Simulate job updates
    setJobs(prev => {
      const updatedJobs = prev.map(job => {
        if (job.status === 'running' && job.progress < 100) {
          return {
            ...job,
            progress: Math.min(100, job.progress + Math.random() * 10),
            duration: Date.now() - new Date(job.startTime).getTime()
          };
        }
        return job;
      });

      // Randomly complete running jobs
      const runningJobs = updatedJobs.filter(j => j.status === 'running');
      runningJobs.forEach(job => {
        if (job.progress >= 100 && Math.random() > 0.7) {
          const jobIndex = updatedJobs.findIndex(j => j.id === job.id);
          if (jobIndex !== -1) {
            updatedJobs[jobIndex] = {
              ...job,
              status: Math.random() > 0.1 ? 'completed' : 'failed',
              progress: 100,
              duration: Date.now() - new Date(job.startTime).getTime(),
              error: Math.random() > 0.9 ? 'Network timeout' : undefined
            };
          }
        }
      });

      // Add new jobs occasionally
      if (Math.random() > 0.8 && updatedJobs.length < 10) {
        const newJob: RPAJob = {
          id: Date.now().toString(),
          drNumber: `DR${Math.floor(Math.random() * 9000000) + 1000000}`,
          status: 'queued',
          progress: 0,
          startTime: new Date().toISOString()
        };
        updatedJobs.unshift(newJob);
      }

      return updatedJobs;
    });
  };

  const updateStats = () => {
    const completed = jobs.filter(j => j.status === 'completed').length;
    const failed = jobs.filter(j => j.status === 'failed').length;
    const totalExecutionTime = jobs
      .filter(j => j.status === 'completed' && j.duration)
      .reduce((sum, j) => sum + (j.duration || 0), 0);
    const avgTime = completed > 0 ? totalExecutionTime / completed : 0;

    const totalJobsCount = jobs.length;
    setStats({
      totalJobs: totalJobsCount,
      completedJobs: completed,
      failedJobs: failed,
      averageExecutionTime: avgTime,
      successRate: totalJobsCount > 0 ? (completed / totalJobsCount) * 100 : 0,
      jobsPerHour: Math.floor(completed * (3600 / 3000)), // Rough estimate
      queueLength: jobs.filter(j => j.status === 'queued').length
    });
  };

  const generateTrends = () => {
    const newTrends: PerformanceTrend[] = [];
    const now = new Date();

    for (let i = 23; i >= 0; i--) {
      const timestamp = new Date(now.getTime() - i * 60 * 60 * 1000);
      newTrends.push({
        timestamp: timestamp.getHours() + ':00',
        executionTime: Math.max(15000, Math.min(45000, 25000 + (Math.random() * 10000 - 5000))),
        successRate: Math.max(85, Math.min(100, 95 + (Math.random() * 5 - 2.5))),
        jobsProcessed: Math.floor(Math.random() * 20) + 10
      });
    }

    setTrends(newTrends);
  };

  const getStatusColor = (status: RPAJob['status']) => {
    switch (status) {
      case 'running': return 'bg-blue-500';
      case 'completed': return 'bg-green-500';
      case 'failed': return 'bg-red-500';
      case 'queued': return 'bg-yellow-500';
      case 'paused': return 'bg-gray-500';
      default: return 'bg-gray-500';
    }
  };

  const getStatusIcon = (status: RPAJob['status']) => {
    switch (status) {
      case 'running': return Activity;
      case 'completed': return CheckCircle;
      case 'failed': return XCircle;
      case 'queued': return Clock;
      case 'paused': return Pause;
      default: return AlertTriangle;
    }
  };

  const formatDuration = (ms: number) => {
    if (ms < 1000) return `${ms}ms`;
    if (ms < 60000) return `${(ms / 1000).toFixed(1)}s`;
    return `${(ms / 60000).toFixed(1)}m`;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold">RPA Performance Monitor</h2>
          <p className="text-muted-foreground">Real-time automation job tracking and performance metrics</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm">
            <TrendingUp className="h-4 w-4 mr-2" />
            View Report
          </Button>
          <Button size="sm">
            <Bot className="h-4 w-4 mr-2" />
            New Job
          </Button>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Jobs</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalJobs}</div>
            <p className="text-xs text-muted-foreground">
              All time
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Completed</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">{stats.completedJobs}</div>
            <p className="text-xs text-muted-foreground">
              {stats.successRate.toFixed(1)}% success rate
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Failed</CardTitle>
            <XCircle className="h-4 w-4 text-red-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">{stats.failedJobs}</div>
            <p className="text-xs text-muted-foreground">
              {stats.failedJobs > 0 ? 'Needs attention' : 'All good'}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Avg Execution</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatDuration(stats.averageExecutionTime)}</div>
            <p className="text-xs text-muted-foreground">
              {stats.averageExecutionTime < 30000 ? 'On target' : 'Slow'}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Jobs/Hour</CardTitle>
            <Zap className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.jobsPerHour}</div>
            <p className="text-xs text-muted-foreground">
              Processing rate
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Queue</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.queueLength}</div>
            <p className="text-xs text-muted-foreground">
              Waiting jobs
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Charts and active jobs */}
      <div className="grid gap-6 lg:grid-cols-2">
        {/* Performance trends */}
        <Card>
          <CardHeader>
            <CardTitle>24-Hour Performance Trends</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={trends}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis yAxisId="left" />
                <YAxis yAxisId="right" orientation="right" />
                <Tooltip />
                <Line
                  yAxisId="left"
                  type="monotone"
                  dataKey="executionTime"
                  stroke="#8884d8"
                  strokeWidth={2}
                  name="Avg Execution (ms)"
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="successRate"
                  stroke="#82ca9d"
                  strokeWidth={2}
                  name="Success Rate (%)"
                />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Job distribution */}
        <Card>
          <CardHeader>
            <CardTitle>Jobs by Status</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={[
                { status: 'Running', count: jobs.filter(j => j.status === 'running').length },
                { status: 'Completed', count: jobs.filter(j => j.status === 'completed').length },
                { status: 'Failed', count: jobs.filter(j => j.status === 'failed').length },
                { status: 'Queued', count: jobs.filter(j => j.status === 'queued').length },
                { status: 'Paused', count: jobs.filter(j => j.status === 'paused').length }
              ]}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="status" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="count" fill="#8884d8" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* Active jobs list */}
      <Card>
        <CardHeader>
          <CardTitle>Active Jobs</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {jobs.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                <Bot className="h-12 w-12 mx-auto mb-2" />
                <p>No active jobs</p>
              </div>
            ) : (
              jobs.map((job) => {
                const StatusIcon = getStatusIcon(job.status);
                const isRunning = job.status === 'running';
                const duration = job.duration ? formatDuration(job.duration) : '-';

                return (
                  <div key={job.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="flex items-center space-x-4">
                      <div className={`w-3 h-3 rounded-full ${getStatusColor(job.status)}`} />
                      <StatusIcon className={`h-5 w-5 ${
                        job.status === 'completed' ? 'text-green-500' :
                        job.status === 'failed' ? 'text-red-500' :
                        job.status === 'running' ? 'text-blue-500' :
                        'text-muted-foreground'
                      }`} />
                      <div>
                        <div className="font-medium">{job.drNumber}</div>
                        <div className="text-sm text-muted-foreground">
                          Started {new Date(job.startTime).toLocaleTimeString()}
                        </div>
                        {job.error && (
                          <div className="text-sm text-red-600 mt-1">{job.error}</div>
                        )}
                      </div>
                    </div>

                    <div className="flex items-center space-x-4">
                      <div className="text-right">
                        <div className="text-sm text-muted-foreground">Duration</div>
                        <div className="font-medium">{duration}</div>
                      </div>

                      {isRunning && (
                        <div className="w-32">
                          <div className="flex justify-between text-sm mb-1">
                            <span>Progress</span>
                            <span>{job.progress.toFixed(0)}%</span>
                          </div>
                          <Progress value={job.progress} className="h-2" />
                        </div>
                      )}

                      <Badge variant={
                        job.status === 'completed' ? 'default' :
                        job.status === 'failed' ? 'destructive' :
                        job.status === 'running' ? 'secondary' :
                        'outline'
                      }>
                        {job.status}
                      </Badge>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}