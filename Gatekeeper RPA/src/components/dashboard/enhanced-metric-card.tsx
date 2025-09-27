/**
 * Enhanced Metric Card Component for Dashboard
 *
 * Modern enterprise-grade metric display with large numbers,
 * trend indicators, status badges, and micro-interactions.
 *
 * Created with comprehensive animations and accessibility features.
 */

import * as React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import { TrendingUp, TrendingDown, Minus, Activity, Zap, Shield, Clock } from 'lucide-react';

// Enhanced metric card props
interface EnhancedMetricCardProps {
  title: string;
  value: string | number;
  description?: string;
  trend?: {
    direction: 'up' | 'down' | 'stable';
    value: string;
    isPositive?: boolean;
  };
  status?: 'success' | 'warning' | 'error' | 'info';
  icon?: React.ReactNode;
  loading?: boolean;
  format?: 'number' | 'percentage' | 'currency' | 'rate';
  change?: string;
  subtitle?: string;
  className?: string;
}

// Format value based on type
const formatValue = (value: string | number, format?: string): string => {
  if (typeof value === 'string') return value;

  switch (format) {
    case 'percentage':
      return `${value}%`;
    case 'currency':
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
      }).format(value);
    case 'rate':
      return `${value}/hr`;
    default:
      return new Intl.NumberFormat('en-US').format(value);
  }
};

// Get trend icon based on direction
const getTrendIcon = (direction: 'up' | 'down' | 'stable') => {
  switch (direction) {
    case 'up':
      return <TrendingUp className="h-4 w-4" />;
    case 'down':
      return <TrendingDown className="h-4 w-4" />;
    default:
      return <Minus className="h-4 w-4" />;
  }
};

// Get status color
const getStatusColor = (status?: string) => {
  switch (status) {
    case 'success':
      return 'bg-green-500';
    case 'warning':
      return 'bg-yellow-500';
    case 'error':
      return 'bg-red-500';
    case 'info':
      return 'bg-blue-500';
    default:
      return 'bg-gray-500';
  }
};

// Loading skeleton component
function MetricCardSkeleton() {
  return (
    <Card className="relative overflow-hidden">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <Skeleton className="h-4 w-24" />
          <Skeleton className="h-6 w-6 rounded-full" />
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <Skeleton className="h-8 w-20" />
          <Skeleton className="h-3 w-32" />
          <div className="flex items-center space-x-2">
            <Skeleton className="h-4 w-4" />
            <Skeleton className="h-3 w-16" />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

/**
 * Enhanced Metric Card Component
 *
 * Features:
 * - Large, prominent numbers with proper formatting
 * - Animated trend indicators with icons
 * - Status badges with color coding
 * - Micro-interactions and hover effects
 * - Loading states with skeleton
 * - Responsive design
 */
export const EnhancedMetricCard: React.FC<EnhancedMetricCardProps> = ({
  title,
  value,
  description,
  trend,
  status,
  icon,
  loading = false,
  format,
  change,
  subtitle,
  className
}) => {
  if (loading) {
    return <MetricCardSkeleton />;
  }

  return (
    <Card className={cn(
      'relative overflow-hidden transition-all duration-300 hover:shadow-lg hover:scale-[1.02]',
      'border-l-4',
      status === 'success' && 'border-l-green-500',
      status === 'warning' && 'border-l-yellow-500',
      status === 'error' && 'border-l-red-500',
      status === 'info' && 'border-l-blue-500',
      className
    )}>
      {/* Animated background effect */}
      <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent opacity-0 hover:opacity-100 transition-opacity duration-300" />

      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            {icon && (
              <div className="text-muted-foreground">
                {icon}
              </div>
            )}
            <CardTitle className="text-sm font-semibold text-muted-foreground uppercase tracking-wider">
              {title}
            </CardTitle>
          </div>
          {status && (
            <div className="flex items-center space-x-2">
              <div className={cn('w-2 h-2 rounded-full animate-pulse', getStatusColor(status))} />
              <Badge
                variant={status === 'error' ? 'destructive' : 'secondary'}
                className="text-xs"
              >
                {status}
              </Badge>
            </div>
          )}
        </div>
      </CardHeader>

      <CardContent className="space-y-3">
        <div className="flex items-baseline justify-between">
          <div className="text-3xl font-bold text-foreground">
            {formatValue(value, format)}
          </div>
          {trend && (
            <div className={cn(
              'flex items-center space-x-1 text-sm font-medium',
              trend.isPositive ? 'text-green-600' : 'text-red-600'
            )}>
              {getTrendIcon(trend.direction)}
              <span>{trend.value}</span>
            </div>
          )}
        </div>

        {subtitle && (
          <div className="text-sm text-muted-foreground">
            {subtitle}
          </div>
        )}

        {description && (
          <p className="text-sm text-muted-foreground leading-relaxed">
            {description}
          </p>
        )}

        {change && (
          <div className="flex items-center space-x-2 text-xs text-muted-foreground">
            <Clock className="h-3 w-3" />
            <span>{change}</span>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

// Preset metric cards for common use cases
export const TicketsMetricCard: React.FC<{
  value: number;
  trend?: { direction: 'up' | 'down' | 'stable'; value: string };
  loading?: boolean;
}> = ({ value, trend, loading }) => (
  <EnhancedMetricCard
    title="Active Tickets"
    value={value}
    description="Currently in processing queue"
    trend={trend}
    status={value > 50 ? 'warning' : 'success'}
    icon={<Activity className="h-5 w-5" />}
    loading={loading}
    format="number"
  />
);

export const ProcessingRateCard: React.FC<{
  value: number;
  trend?: { direction: 'up' | 'down' | 'stable'; value: string };
  loading?: boolean;
}> = ({ value, trend, loading }) => (
  <EnhancedMetricCard
    title="Processing Rate"
    value={value}
    description="Average tickets per hour"
    trend={trend}
    status={value > 20 ? 'success' : 'warning'}
    icon={<Zap className="h-5 w-5" />}
    loading={loading}
    format="rate"
  />
);

export const SuccessRateCard: React.FC<{
  value: number;
  trend?: { direction: 'up' | 'down' | 'stable'; value: string };
  loading?: boolean;
}> = ({ value, trend, loading }) => (
  <EnhancedMetricCard
    title="Success Rate"
    value={value}
    description="Successfully processed tickets"
    trend={trend}
    status={value >= 95 ? 'success' : value >= 85 ? 'warning' : 'error'}
    icon={<TrendingUp className="h-5 w-5" />}
    loading={loading}
    format="percentage"
  />
);

export const SystemHealthCard: React.FC<{
  value: number;
  trend?: { direction: 'up' | 'down' | 'stable'; value: string };
  loading?: boolean;
}> = ({ value, trend, loading }) => (
  <EnhancedMetricCard
    title="System Health"
    value={value}
    description="All systems operational"
    trend={trend}
    status={value >= 95 ? 'success' : value >= 85 ? 'warning' : 'error'}
    icon={<Shield className="h-5 w-5" />}
    loading={loading}
    format="percentage"
  />
);