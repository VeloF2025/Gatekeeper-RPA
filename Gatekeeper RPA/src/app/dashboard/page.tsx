/**
 * Enhanced Dashboard Page - Gatekeeper RPA System
 *
 * Modern enterprise-grade dashboard with:
 * - Real-time metrics with animated indicators
 * - Interactive data visualization charts
 * - Enhanced quick action cards with micro-interactions
 * - Comprehensive activity timeline with filtering
 * - Live system status monitoring
 *
 * Created with comprehensive accessibility and responsive design.
 */

'use client';

import { Suspense, useState, useEffect } from 'react';
import Link from 'next/link';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { SecurityIndicator } from '@/components/security-indicator';
import { SystemStatus } from '@/components/system-status';
import {
  TicketsMetricCard,
  ProcessingRateCard,
  SuccessRateCard,
  SystemHealthCard
} from '@/components/dashboard/enhanced-metric-card';
import { DashboardCharts } from '@/components/dashboard/data-charts';
import { QuickActionsGrid } from '@/components/dashboard/quick-action-cards';
import { ActivityTimeline } from '@/components/dashboard/activity-timeline';
import { SystemMetrics, ServiceStatusGrid } from '@/components/dashboard/progress-indicators';
import {
  Activity,
  BarChart3,
  Settings,
  RefreshCw,
  ArrowRight,
  TrendingUp,
  Zap,
  Users,
  Clock
} from 'lucide-react';

// Enhanced loading component
function DashboardLoading() {
  return (
    <div className="space-y-6">
      {/* Header skeleton */}
      <div className="flex items-center justify-between">
        <div>
          <Skeleton className="h-8 w-32 mb-2" />
          <Skeleton className="h-4 w-48" />
        </div>
        <div className="flex space-x-2">
          <Skeleton className="h-10 w-20" />
          <Skeleton className="h-10 w-24" />
        </div>
      </div>

      {/* Security indicator skeleton */}
      <Skeleton className="h-16 w-full" />

      {/* Metrics grid skeleton */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {[...Array(4)].map((_, i) => (
          <Card key={i}>
            <CardHeader className="pb-2">
              <div className="flex items-center justify-between">
                <Skeleton className="h-4 w-20" />
                <Skeleton className="h-6 w-6 rounded-full" />
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <Skeleton className="h-8 w-16" />
                <Skeleton className="h-3 w-24" />
                <div className="flex items-center space-x-2">
                  <Skeleton className="h-4 w-4" />
                  <Skeleton className="h-3 w-16" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Charts skeleton */}
      <div className="grid gap-6 md:grid-cols-2">
        {[...Array(3)].map((_, i) => (
          <Card key={i}>
            <CardHeader>
              <Skeleton className="h-6 w-32" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-64 w-full" />
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}

// Dashboard metrics data type
interface DashboardMetrics {
  activeTickets: number;
  processingRate: number;
  successRate: number;
  systemHealth: number;
  ticketsTrend: { direction: 'up' | 'down' | 'stable'; value: string };
  processingTrend: { direction: 'up' | 'down' | 'stable'; value: string };
  successTrend: { direction: 'up' | 'down' | 'stable'; value: string };
  healthTrend: { direction: 'up' | 'down' | 'stable'; value: string };
}

// Simulated real-time data
const useDashboardData = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics>({
    activeTickets: 47,
    processingRate: 23,
    successRate: 98,
    systemHealth: 96,
    ticketsTrend: { direction: 'up', value: '+12%' },
    processingTrend: { direction: 'up', value: '+8%' },
    successTrend: { direction: 'stable', value: '0%' },
    healthTrend: { direction: 'up', value: '+2%' }
  });

  const [isLoading, setIsLoading] = useState(false);

  // Simulate real-time updates
  useEffect(() => {
    const interval = setInterval(() => {
      setMetrics(prev => ({
        ...prev,
        activeTickets: Math.max(0, prev.activeTickets + Math.floor(Math.random() * 5) - 2),
        processingRate: Math.max(0, prev.processingRate + Math.floor(Math.random() * 3) - 1),
        successRate: Math.min(100, Math.max(85, prev.successRate + Math.floor(Math.random() * 3) - 1)),
        systemHealth: Math.min(100, Math.max(90, prev.systemHealth + Math.floor(Math.random() * 2) - 1))
      }));
    }, 5000); // Update every 5 seconds

    return () => clearInterval(interval);
  }, []);

  const refreshData = async () => {
    setIsLoading(true);
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    setIsLoading(false);
  };

  return { metrics, isLoading, refreshData };
};

export default function DashboardPage() {
  const { metrics, isLoading, refreshData } = useDashboardData();
  const [currentTime, setCurrentTime] = useState(new Date());

  // Update time every minute
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 60000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-muted/20">
      <div className="container mx-auto px-4 py-8">
        {/* Enhanced Header */}
        <div className="mb-8">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0">
            <div className="space-y-2">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg">
                  <BarChart3 className="h-6 w-6 text-white" />
                </div>
                <div>
                  <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
                  <p className="text-muted-foreground">
                    Real-time system monitoring and insights
                  </p>
                </div>
              </div>
              <div className="flex items-center space-x-4 text-sm text-muted-foreground">
                <div className="flex items-center space-x-1">
                  <Clock className="h-4 w-4" />
                  <span>{currentTime.toLocaleTimeString()}</span>
                </div>
                <Badge variant="outline" className="text-xs">
                  Live Updates
                </Badge>
              </div>
            </div>
            <div className="flex flex-wrap gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={refreshData}
                disabled={isLoading}
                className="transition-all duration-200"
              >
                <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                Refresh
              </Button>
              <Button variant="outline" size="sm" asChild>
                <Link href="/">
                  <Activity className="h-4 w-4 mr-2" />
                  Home
                </Link>
              </Button>
              <Button size="sm" asChild className="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700">
                <Link href="/tickets">
                  View Tickets
                  <ArrowRight className="h-4 w-4 ml-2" />
                </Link>
              </Button>
            </div>
          </div>
        </div>

        {/* Security status banner */}
        <div className="mb-6">
          <SecurityIndicator />
        </div>

        {/* Main content grid */}
        <div className="space-y-8">
          {/* Key metrics with enhanced cards */}
          <Suspense fallback={<DashboardLoading />}>
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
              <TicketsMetricCard
                value={metrics.activeTickets}
                trend={metrics.ticketsTrend}
                loading={isLoading}
              />
              <ProcessingRateCard
                value={metrics.processingRate}
                trend={metrics.processingTrend}
                loading={isLoading}
              />
              <SuccessRateCard
                value={metrics.successRate}
                trend={metrics.successTrend}
                loading={isLoading}
              />
              <SystemHealthCard
                value={metrics.systemHealth}
                trend={metrics.healthTrend}
                loading={isLoading}
              />
            </div>
          </Suspense>

          {/* Data visualization and quick actions */}
          <div className="grid gap-6 lg:grid-cols-3">
            <div className="lg:col-span-2">
              <Suspense fallback={<DashboardLoading />}>
                <DashboardCharts loading={isLoading} />
              </Suspense>
            </div>
            <div>
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center space-x-2">
                    <Zap className="h-5 w-5" />
                    <span>Quick Actions</span>
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="grid gap-3">
                    <Button variant="outline" className="w-full justify-start" asChild>
                      <Link href="/tickets">
                        <BarChart3 className="h-4 w-4 mr-3" />
                        View All Tickets
                      </Link>
                    </Button>
                    <Button variant="outline" className="w-full justify-start" asChild>
                      <Link href="/analytics">
                        <TrendingUp className="h-4 w-4 mr-3" />
                        Analytics Dashboard
                      </Link>
                    </Button>
                    <Button variant="outline" className="w-full justify-start" asChild>
                      <Link href="/settings">
                        <Settings className="h-4 w-4 mr-3" />
                        System Settings
                      </Link>
                    </Button>
                    <Button variant="outline" className="w-full justify-start" asChild>
                      <Link href="/users">
                        <Users className="h-4 w-4 mr-3" />
                        User Management
                      </Link>
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>

          {/* System status and metrics */}
          <div className="grid gap-6 lg:grid-cols-2">
            <Suspense fallback={<DashboardLoading />}>
              <div className="space-y-6">
                <SystemMetrics
                  cpu={45}
                  memory={67}
                  disk={34}
                  network={23}
                  uptime="15 days, 4 hours"
                  loading={isLoading}
                />
                <ServiceStatusGrid loading={isLoading} />
              </div>
            </Suspense>
            <Suspense fallback={<DashboardLoading />}>
              <div className="space-y-6">
                {/* System status card */}
                <div>
                  <h2 className="text-xl font-semibold mb-4">System Status</h2>
                  <SystemStatus />
                </div>

                {/* Recent activity timeline */}
                <div>
                  <h2 className="text-xl font-semibold mb-4">Recent Activity</h2>
                  <ActivityTimeline
                    limit={5}
                    showSearch={false}
                    showFilter={false}
                    loading={isLoading}
                  />
                </div>
              </div>
            </Suspense>
          </div>

          {/* Quick actions grid */}
          <div>
            <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
            <QuickActionsGrid />
          </div>
        </div>
      </div>
    </div>
  );
}