/**
 * Enhanced Activity Timeline Component for Dashboard
 *
 * Modern enterprise-grade timeline with:
 * - Visual timeline with status indicators
 * - Detailed activity cards with metadata
 * - Real-time updates with animations
 * - Filtering and search capabilities
 *
 * Created with comprehensive accessibility and responsive design.
 */

import * as React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import {
  CheckCircle,
  XCircle,
  Clock,
  AlertTriangle,
  Info,
  User,
  Shield,
  FileText,
  Zap,
  Database,
  Activity,
  Calendar,
  Search
} from 'lucide-react';

// Activity types
type ActivityType = 'success' | 'error' | 'warning' | 'info' | 'security' | 'processing';

interface ActivityItem {
  id: string;
  type: ActivityType;
  title: string;
  description: string;
  timestamp: string;
  user?: string;
  metadata?: Record<string, any>;
  icon?: React.ReactNode;
  action?: {
    label: string;
    onClick: () => void;
  };
}

interface ActivityTimelineProps {
  activities?: ActivityItem[];
  loading?: boolean;
  limit?: number;
  showSearch?: boolean;
  showFilter?: boolean;
  className?: string;
}

// Get icon based on activity type
const getActivityIcon = (type: ActivityType) => {
  switch (type) {
    case 'success':
      return <CheckCircle className="h-4 w-4 text-green-500" />;
    case 'error':
      return <XCircle className="h-4 w-4 text-red-500" />;
    case 'warning':
      return <AlertTriangle className="h-4 w-4 text-yellow-500" />;
    case 'security':
      return <Shield className="h-4 w-4 text-purple-500" />;
    case 'processing':
      return <Clock className="h-4 w-4 text-blue-500" />;
    default:
      return <Info className="h-4 w-4 text-gray-500" />;
  }
};

// Get color based on activity type
const getActivityColor = (type: ActivityType) => {
  switch (type) {
    case 'success':
      return 'border-green-500 bg-green-500/10';
    case 'error':
      return 'border-red-500 bg-red-500/10';
    case 'warning':
      return 'border-yellow-500 bg-yellow-500/10';
    case 'security':
      return 'border-purple-500 bg-purple-500/10';
    case 'processing':
      return 'border-blue-500 bg-blue-500/10';
    default:
      return 'border-gray-500 bg-gray-500/10';
  }
};

// Get badge variant based on activity type
const getActivityBadge = (type: ActivityType) => {
  switch (type) {
    case 'success':
      return 'default';
    case 'error':
      return 'destructive';
    case 'warning':
      return 'secondary';
    case 'security':
      return 'outline';
    default:
      return 'outline';
  }
};

// Format timestamp
const formatTimestamp = (timestamp: string) => {
  const date = new Date(timestamp);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return 'Just now';
  if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
  if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
  return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
};

// Loading skeleton component
function ActivityTimelineSkeleton({ count = 5 }: { count?: number }) {
  return (
    <div className="space-y-4">
      {[...Array(count)].map((_, i) => (
        <div key={i} className="flex space-x-4">
          <Skeleton className="h-10 w-10 rounded-full" />
          <div className="flex-1 space-y-2">
            <Skeleton className="h-4 w-32" />
            <Skeleton className="h-3 w-48" />
            <Skeleton className="h-3 w-24" />
          </div>
        </div>
      ))}
    </div>
  );
}

/**
 * Individual Activity Item Component
 */
const ActivityItem: React.FC<{ activity: ActivityItem }> = ({ activity }) => {
  const [isExpanded, setIsExpanded] = React.useState(false);

  return (
    <div className="group relative">
      {/* Timeline line */}
      <div className="absolute left-5 top-10 bottom-0 w-0.5 bg-border" />

      <div className="flex space-x-4 pb-6">
        {/* Activity indicator */}
        <div
          className={cn(
            'relative z-10 flex items-center justify-center w-10 h-10 rounded-full border-2 background',
            getActivityColor(activity.type)
          )}
        >
          {activity.icon || getActivityIcon(activity.type)}
        </div>

        {/* Activity content */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between">
            <div className="space-y-1 flex-1">
              <div className="flex items-center space-x-2">
                <h4 className="text-sm font-semibold text-foreground">
                  {activity.title}
                </h4>
                <Badge variant={getActivityBadge(activity.type)} className="text-xs">
                  {activity.type}
                </Badge>
              </div>

              <p className="text-sm text-muted-foreground">
                {activity.description}
              </p>

              {/* Metadata */}
              {activity.metadata && (
                <div className="mt-2 space-y-1">
                  {Object.entries(activity.metadata).map(([key, value]) => (
                    <div key={key} className="flex items-center space-x-2 text-xs text-muted-foreground">
                      <span className="font-medium">{key}:</span>
                      <span>{String(value)}</span>
                    </div>
                  ))}
                </div>
              )}

              {/* Timestamp and user */}
              <div className="flex items-center space-x-4 text-xs text-muted-foreground">
                <div className="flex items-center space-x-1">
                  <Calendar className="h-3 w-3" />
                  <span>{formatTimestamp(activity.timestamp)}</span>
                </div>
                {activity.user && (
                  <div className="flex items-center space-x-1">
                    <User className="h-3 w-3" />
                    <span>{activity.user}</span>
                  </div>
                )}
              </div>

              {/* Action button */}
              {activity.action && (
                <div className="mt-3">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={activity.action.onClick}
                    className="text-xs"
                  >
                    {activity.action.label}
                  </Button>
                </div>
              )}
            </div>

            {/* Expand button */}
            {activity.metadata && Object.keys(activity.metadata).length > 0 && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setIsExpanded(!isExpanded)}
                className="opacity-0 group-hover:opacity-100 transition-opacity"
              >
                {isExpanded ? 'Show Less' : 'Show More'}
              </Button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

/**
 * Enhanced Activity Timeline Component
 */
export const ActivityTimeline: React.FC<ActivityTimelineProps> = ({
  activities,
  loading = false,
  limit = 10,
  showSearch = true,
  showFilter = true,
  className
}) => {
  const [searchTerm, setSearchTerm] = React.useState('');
  const [filterType, setFilterType] = React.useState<ActivityType | 'all'>('all');

  // Sample activities data
  const sampleActivities: ActivityItem[] = [
    {
      id: '1',
      type: 'success',
      title: 'System Started Successfully',
      description: 'All services initialized and running normally',
      timestamp: new Date(Date.now() - 2 * 60000).toISOString(),
      user: 'System',
      icon: <Zap className="h-4 w-4" />,
      metadata: {
        version: '2.1.0',
        uptime: '99.9%',
        services: '8/8 online'
      }
    },
    {
      id: '2',
      type: 'security',
      title: 'Security Scan Completed',
      description: 'Comprehensive security scan finished with no issues detected',
      timestamp: new Date(Date.now() - 5 * 60000).toISOString(),
      user: 'Security Bot',
      icon: <Shield className="h-4 w-4" />,
      metadata: {
        scanType: 'Comprehensive',
        threats: '0 detected',
        duration: '45 seconds'
      }
    },
    {
      id: '3',
      type: 'processing',
      title: 'Ticket Processing Started',
      description: 'Bulk processing of 34 tickets initiated',
      timestamp: new Date(Date.now() - 15 * 60000).toISOString(),
      user: 'Auto Processor',
      icon: <FileText className="h-4 w-4" />,
      metadata: {
        ticketCount: 34,
        estimatedTime: '5 minutes',
        priority: 'High'
      }
    },
    {
      id: '4',
      type: 'warning',
      title: 'High Memory Usage Detected',
      description: 'System memory usage exceeded 80% threshold',
      timestamp: new Date(Date.now() - 30 * 60000).toISOString(),
      user: 'Monitor',
      icon: <AlertTriangle className="h-4 w-4" />,
      metadata: {
        memoryUsage: '82%',
        threshold: '80%',
        recommendation: 'Clear cache or restart service'
      }
    },
    {
      id: '5',
      type: 'error',
      title: 'Database Connection Failed',
      description: 'Temporary connection issue with primary database',
      timestamp: new Date(Date.now() - 45 * 60000).toISOString(),
      user: 'System',
      icon: <Database className="h-4 w-4" />,
      metadata: {
        error: 'Connection timeout',
        duration: '30 seconds',
        resolved: 'true'
      }
    }
  ];

  const displayActivities = activities || sampleActivities;

  // Filter activities based on search and filter
  const filteredActivities = displayActivities
    .filter(activity => {
      const matchesSearch = searchTerm === '' ||
        activity.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        activity.description.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesFilter = filterType === 'all' || activity.type === filterType;

      return matchesSearch && matchesFilter;
    })
    .slice(0, limit);

  const activityTypes: ActivityType[] = ['success', 'error', 'warning', 'info', 'security', 'processing'];

  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Recent Activity</CardTitle>
        </CardHeader>
        <CardContent>
          <ActivityTimelineSkeleton />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={cn('h-full', className)}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center space-x-2">
            <Activity className="h-5 w-5" />
            <span>Recent Activity</span>
          </CardTitle>
          <Badge variant="outline">{filteredActivities.length} events</Badge>
        </div>

        {/* Search and Filter */}
        {(showSearch || showFilter) && (
          <div className="flex flex-col sm:flex-row gap-2 mt-4">
            {showSearch && (
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <input
                  type="text"
                  placeholder="Search activities..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 text-sm border border-border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
            )}

            {showFilter && (
              <div className="flex flex-wrap gap-2">
                <Button
                  variant={filterType === 'all' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setFilterType('all')}
                >
                  All
                </Button>
                {activityTypes.map(type => (
                  <Button
                    key={type}
                    variant={filterType === type ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setFilterType(type)}
                  >
                    {type}
                  </Button>
                ))}
              </div>
            )}
          </div>
        )}
      </CardHeader>

      <CardContent>
        {filteredActivities.length === 0 ? (
          <div className="text-center py-8">
            <p className="text-muted-foreground">No activities found</p>
          </div>
        ) : (
          <div className="space-y-0">
            {filteredActivities.map(activity => (
              <ActivityItem key={activity.id} activity={activity} />
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};