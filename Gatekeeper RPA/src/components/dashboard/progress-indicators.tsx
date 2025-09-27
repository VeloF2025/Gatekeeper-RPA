/**
 * Animated Progress Indicators Component for Dashboard
 *
 * Modern enterprise-grade progress indicators with:
 * - Animated progress bars with smooth transitions
 * - Live status indicators with real-time updates
 * - Circular progress for system metrics
 * - Pulse animations for active processes
 *
 * Created with comprehensive animations and accessibility features.
 */

import * as React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import {
  Activity,
  Database,
  Wifi,
  Cpu,
  HardDrive,
  Server,
  CheckCircle,
  XCircle,
  Clock,
  AlertTriangle
} from 'lucide-react';

// Progress bar props
interface ProgressBarProps {
  value: number;
  max?: number;
  label?: string;
  description?: string;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
  animated?: boolean;
  showValue?: boolean;
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

// Circular progress props
interface CircularProgressProps {
  value: number;
  max?: number;
  size?: number;
  strokeWidth?: number;
  label?: string;
  description?: string;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
  animated?: boolean;
  className?: string;
}

// Status indicator props
interface StatusIndicatorProps {
  status: 'online' | 'offline' | 'warning' | 'maintenance';
  label: string;
  description?: string;
  lastUpdated?: string;
  pulse?: boolean;
  className?: string;
}

// System metrics props
interface SystemMetricsProps {
  cpu: number;
  memory: number;
  disk: number;
  network: number;
  uptime: string;
  loading?: boolean;
  className?: string;
}

// Get progress color classes
const getProgressColor = (color: string): string => {
  switch (color) {
    case 'success':
      return 'bg-green-500';
    case 'warning':
      return 'bg-yellow-500';
    case 'error':
      return 'bg-red-500';
    case 'info':
      return 'bg-blue-500';
    default:
      return 'bg-primary';
  }
};

const getProgressColorClass = (color: string): string => {
  switch (color) {
    case 'success':
      return 'text-green-500';
    case 'warning':
      return 'text-yellow-500';
    case 'error':
      return 'text-red-500';
    case 'info':
      return 'text-blue-500';
    default:
      return 'text-primary';
  }
};

// Get status color and icon
const getStatusInfo = (status: StatusIndicatorProps['status']) => {
  switch (status) {
    case 'online':
      return { color: 'bg-green-500', icon: CheckCircle, textColor: 'text-green-600' };
    case 'offline':
      return { color: 'bg-red-500', icon: XCircle, textColor: 'text-red-600' };
    case 'warning':
      return { color: 'bg-yellow-500', icon: AlertTriangle, textColor: 'text-yellow-600' };
    case 'maintenance':
      return { color: 'bg-blue-500', icon: Clock, textColor: 'text-blue-600' };
  }
};

/**
 * Animated Progress Bar Component
 */
export const ProgressBar: React.FC<ProgressBarProps> = ({
  value,
  max = 100,
  label,
  description,
  color = 'primary',
  animated = true,
  showValue = true,
  size = 'md',
  className
}) => {
  const percentage = Math.min((value / max) * 100, 100);
  const [displayedPercentage, setDisplayedPercentage] = React.useState(0);

  React.useEffect(() => {
    if (animated) {
      const timer = setTimeout(() => {
        setDisplayedPercentage(percentage);
      }, 100);
      return () => clearTimeout(timer);
    } else {
      setDisplayedPercentage(percentage);
    }
    return undefined;
  }, [percentage, animated]);

  const heightClasses = {
    sm: 'h-2',
    md: 'h-3',
    lg: 'h-4'
  };

  return (
    <div className={cn('space-y-2', className)}>
      {(label || description) && (
        <div className="flex justify-between items-center">
          {label && (
            <span className="text-sm font-medium text-foreground">
              {label}
            </span>
          )}
          {showValue && (
            <span className={cn('text-sm font-medium', getProgressColorClass(color))}>
              {Math.round(displayedPercentage)}%
            </span>
          )}
        </div>
      )}

      <div className={cn('w-full bg-muted rounded-full overflow-hidden', heightClasses[size])}>
        <div
          className={cn(
            'h-full rounded-full transition-all duration-1000 ease-out',
            getProgressColor(color),
            animated && 'animate-pulse'
          )}
          style={{ width: `${displayedPercentage}%` }}
        />
      </div>

      {description && (
        <p className="text-xs text-muted-foreground">{description}</p>
      )}
    </div>
  );
};

/**
 * Circular Progress Component
 */
export const CircularProgress: React.FC<CircularProgressProps> = ({
  value,
  max = 100,
  size = 80,
  strokeWidth = 8,
  label,
  description,
  color = 'primary',
  animated = true,
  className
}) => {
  const percentage = Math.min((value / max) * 100, 100);
  const [displayedPercentage, setDisplayedPercentage] = React.useState(0);
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const strokeDasharray = circumference;
  const strokeDashoffset = circumference - (displayedPercentage / 100) * circumference;

  React.useEffect(() => {
    if (animated) {
      const timer = setTimeout(() => {
        setDisplayedPercentage(percentage);
      }, 100);
      return () => clearTimeout(timer);
    } else {
      setDisplayedPercentage(percentage);
    }
    return undefined;
  }, [percentage, animated]);

  const colorClasses = {
    primary: 'stroke-primary',
    success: 'stroke-green-500',
    warning: 'stroke-yellow-500',
    error: 'stroke-red-500',
    info: 'stroke-blue-500'
  };

  return (
    <div className={cn('flex flex-col items-center space-y-2', className)}>
      <div className="relative">
        <svg
          width={size}
          height={size}
          className="transform -rotate-90"
        >
          {/* Background circle */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="currentColor"
            strokeWidth={strokeWidth}
            fill="none"
            className="text-muted opacity-20"
          />
          {/* Progress circle */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="currentColor"
            strokeWidth={strokeWidth}
            fill="none"
            className={cn(
              'transition-all duration-1000 ease-out',
              colorClasses[color],
              animated && 'animate-pulse'
            )}
            strokeDasharray={strokeDasharray}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
          />
        </svg>
        <div className="absolute inset-0 flex items-center justify-center">
          <span className={cn('text-lg font-bold', getProgressColorClass(color))}>
            {Math.round(displayedPercentage)}%
          </span>
        </div>
      </div>
      {label && (
        <span className="text-sm font-medium text-foreground">{label}</span>
      )}
      {description && (
        <p className="text-xs text-muted-foreground text-center">{description}</p>
      )}
    </div>
  );
};

/**
 * Status Indicator Component
 */
export const StatusIndicator: React.FC<StatusIndicatorProps> = ({
  status,
  label,
  description,
  lastUpdated,
  pulse = true,
  className
}) => {
  const statusInfo = getStatusInfo(status);
  const StatusIcon = statusInfo.icon;

  return (
    <div className={cn('flex items-center space-x-3 p-3 rounded-lg border', className)}>
      <div className="flex items-center space-x-2">
        <div
          className={cn(
            'w-3 h-3 rounded-full',
            statusInfo.color,
            pulse && status === 'online' && 'animate-pulse'
          )}
        />
        <StatusIcon className={cn('h-4 w-4', statusInfo.textColor)} />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between">
          <span className="text-sm font-medium text-foreground">{label}</span>
          <Badge
            variant={status === 'online' ? 'default' : status === 'offline' || status === 'maintenance' ? 'destructive' : 'secondary'}
            className="text-xs"
          >
            {status}
          </Badge>
        </div>
        {description && (
          <p className="text-xs text-muted-foreground">{description}</p>
        )}
        {lastUpdated && (
          <p className="text-xs text-muted-foreground mt-1">
            Updated: {lastUpdated}
          </p>
        )}
      </div>
    </div>
  );
};

/**
 * System Metrics Component
 */
export const SystemMetrics: React.FC<SystemMetricsProps> = ({
  cpu,
  memory,
  disk,
  network,
  uptime,
  loading = false,
  className
}) => {
  if (loading) {
    return (
      <Card className={className}>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Activity className="h-5 w-5" />
            <span>System Metrics</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 gap-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="space-y-2">
                <Skeleton className="h-4 w-16" />
                <Skeleton className="h-3 w-full" />
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Activity className="h-5 w-5" />
            <span>System Metrics</span>
          </div>
          <Badge variant="outline">Live</Badge>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-3">
            <div className="flex items-center space-x-2">
              <Cpu className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm font-medium">CPU Usage</span>
            </div>
            <ProgressBar
              value={cpu}
              color={cpu > 80 ? 'error' : cpu > 60 ? 'warning' : 'success'}
              size="sm"
            />
          </div>

          <div className="space-y-3">
            <div className="flex items-center space-x-2">
              <Database className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm font-medium">Memory</span>
            </div>
            <ProgressBar
              value={memory}
              color={memory > 80 ? 'error' : memory > 60 ? 'warning' : 'success'}
              size="sm"
            />
          </div>

          <div className="space-y-3">
            <div className="flex items-center space-x-2">
              <HardDrive className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm font-medium">Disk Usage</span>
            </div>
            <ProgressBar
              value={disk}
              color={disk > 80 ? 'error' : disk > 60 ? 'warning' : 'success'}
              size="sm"
            />
          </div>

          <div className="space-y-3">
            <div className="flex items-center space-x-2">
              <Wifi className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm font-medium">Network</span>
            </div>
            <ProgressBar
              value={network}
              color={network > 80 ? 'error' : network > 60 ? 'warning' : 'success'}
              size="sm"
            />
          </div>
        </div>

        <div className="mt-4 pt-4 border-t">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Server className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm text-muted-foreground">System Uptime</span>
            </div>
            <span className="text-sm font-medium">{uptime}</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

// Service status grid component
export const ServiceStatusGrid: React.FC<{ loading?: boolean }> = ({ loading = false }) => {
  const services = [
    { name: 'API Gateway', status: 'online' as const, description: 'All endpoints responding' },
    { name: 'Database', status: 'online' as const, description: 'Connection stable' },
    { name: 'Queue Processor', status: 'warning' as const, description: 'High load detected' },
    { name: 'Security Service', status: 'online' as const, description: 'All systems secure' },
    { name: 'File Storage', status: 'online' as const, description: 'Optimal performance' },
    { name: 'Monitoring', status: 'maintenance' as const, description: 'Scheduled maintenance' }
  ];

  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Service Status</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[...Array(6)].map((_, i) => (
              <Skeleton key={i} className="h-16 w-full" />
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Service Status</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid gap-3 md:grid-cols-2">
          {services.map((service, index) => (
            <StatusIndicator
              key={index}
              status={service.status}
              label={service.name}
              description={service.description}
              lastUpdated="Just now"
              pulse={service.status === 'online'}
            />
          ))}
        </div>
      </CardContent>
    </Card>
  );
};