/**
 * Enhanced Quick Action Cards Component for Dashboard
 *
 * Modern enterprise-grade action cards with:
 * - Enhanced hover effects and micro-interactions
 * - Clear CTAs with animated buttons
 * - Progress indicators and status badges
 * - Gradient backgrounds and modern styling
 *
 * Created with comprehensive accessibility and responsive design.
 */

import * as React from 'react';
import Link from 'next/link';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import {
  ArrowRight,
  BarChart3,
  Settings,
  Users,
  FileText,
  Download,
  Upload
} from 'lucide-react';

// Quick action card props
interface QuickActionCardProps {
  title: string;
  description: string;
  icon: React.ReactNode;
  href?: string;
  onClick?: () => void;
  badge?: string;
  badgeVariant?: 'default' | 'secondary' | 'destructive' | 'outline';
  status?: 'active' | 'pending' | 'completed' | 'warning';
  progress?: number;
  loading?: boolean;
  primary?: boolean;
  external?: boolean;
  className?: string;
}

// Loading skeleton component
function QuickActionSkeleton() {
  return (
    <Card className="relative overflow-hidden">
      <CardHeader>
        <div className="flex items-center space-x-3">
          <Skeleton className="h-8 w-8 rounded-lg" />
          <div className="space-y-2 flex-1">
            <Skeleton className="h-5 w-24" />
            <Skeleton className="h-3 w-32" />
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
          <Skeleton className="h-10 w-full rounded-md" />
        </div>
      </CardContent>
    </Card>
  );
}

/**
 * Enhanced Quick Action Card Component
 *
 * Features:
 * - Animated hover effects with scale and shadow
 * - Gradient backgrounds for primary actions
 * - Progress indicators for ongoing tasks
 * - Status badges and micro-interactions
 * - Loading states with skeleton
 */
export const QuickActionCard: React.FC<QuickActionCardProps> = ({
  title,
  description,
  icon,
  href,
  onClick,
  badge,
  badgeVariant = 'outline',
  status,
  progress,
  loading = false,
  primary = false,
  external = false,
  className
}) => {
  const [isHovered, setIsHovered] = React.useState(false);

  if (loading) {
    return <QuickActionSkeleton />;
  }

  const cardContent = (
    <>
      {/* Animated background effect */}
      <div
        className={cn(
          'absolute inset-0 bg-gradient-to-r opacity-0 transition-opacity duration-300',
          primary
            ? 'from-blue-500/10 via-purple-500/10 to-pink-500/10'
            : 'from-gray-500/5 via-transparent to-gray-500/5',
          isHovered && 'opacity-100'
        )}
      />

      {/* Status indicator */}
      {status && (
        <div className="absolute top-4 right-4">
          <div
            className={cn(
              'w-2 h-2 rounded-full animate-pulse',
              status === 'active' && 'bg-green-500',
              status === 'pending' && 'bg-yellow-500',
              status === 'completed' && 'bg-blue-500',
              status === 'warning' && 'bg-red-500'
            )}
          />
        </div>
      )}

      <CardHeader className="pb-3">
        <div className="flex items-center space-x-3">
          <div
            className={cn(
              'flex items-center justify-center w-10 h-10 rounded-lg transition-all duration-300',
              primary
                ? 'bg-gradient-to-br from-blue-500 to-purple-600 text-white'
                : 'bg-muted text-muted-foreground',
              isHovered && primary && 'scale-110 rotate-3'
            )}
          >
            {icon}
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <CardTitle className="text-lg font-semibold">{title}</CardTitle>
              {badge && (
                <Badge variant={badgeVariant} className="text-xs">
                  {badge}
                </Badge>
              )}
            </div>
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        <p className="text-sm text-muted-foreground leading-relaxed">
          {description}
        </p>

        {/* Progress bar */}
        {progress !== undefined && (
          <div className="space-y-2">
            <div className="flex justify-between text-xs text-muted-foreground">
              <span>Progress</span>
              <span>{progress}%</span>
            </div>
            <div className="w-full bg-muted rounded-full h-2 overflow-hidden">
              <div
                className={cn(
                  'h-full rounded-full transition-all duration-500 ease-out',
                  progress >= 80 ? 'bg-green-500' :
                  progress >= 50 ? 'bg-yellow-500' : 'bg-blue-500'
                )}
                style={{ width: `${progress}%` }}
              />
            </div>
          </div>
        )}

        {/* Action button */}
        <Button
          className={cn(
            'w-full transition-all duration-300 group',
            primary && 'bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700',
            isHovered && 'transform translate-x-1'
          )}
          variant={primary ? 'default' : 'outline'}
          onClick={onClick}
        >
          <span className="flex items-center justify-center space-x-2">
            <span>Get Started</span>
            <ArrowRight
              className={cn(
                'w-4 h-4 transition-transform duration-300',
                isHovered && 'translate-x-1'
              )}
            />
          </span>
        </Button>
      </CardContent>
    </>
  );

  const cardClasses = cn(
    'relative overflow-hidden cursor-pointer transition-all duration-300 hover:shadow-xl hover:scale-[1.02] border-0',
    primary && 'bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800',
    className
  );

  if (href) {
    const LinkComponent = external ? 'a' : Link;
    const linkProps = external
      ? { href, target: '_blank', rel: 'noopener noreferrer' }
      : { href };

    return (
      <LinkComponent
        {...linkProps}
        className={cardClasses}
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
      >
        <Card className="border-0 bg-transparent shadow-none">
          {cardContent}
        </Card>
      </LinkComponent>
    );
  }

  return (
    <div
      className={cardClasses}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={onClick}
    >
      <Card className="border-0 bg-transparent shadow-none">
        {cardContent}
      </Card>
    </div>
  );
};

// Preset quick action cards
export const TicketsQuickAction: React.FC = () => (
  <QuickActionCard
    title="View Tickets"
    description="Browse and manage all active tickets in the system"
    icon={<FileText className="h-5 w-5" />}
    href="/tickets"
    badge="34 Active"
    status="active"
    primary
  />
);

export const AnalyticsQuickAction: React.FC = () => (
  <QuickActionCard
    title="Analytics"
    description="View detailed performance metrics and insights"
    icon={<BarChart3 className="h-5 w-5" />}
    href="/analytics"
    badge="New"
    status="active"
  />
);

export const SettingsQuickAction: React.FC = () => (
  <QuickActionCard
    title="Settings"
    description="Configure system preferences and security settings"
    icon={<Settings className="h-5 w-5" />}
    href="/settings"
    badge="Config"
    status="active"
  />
);

export const UsersQuickAction: React.FC = () => (
  <QuickActionCard
    title="User Management"
    description="Manage user accounts and permissions"
    icon={<Users className="h-5 w-5" />}
    href="/users"
    badge="12 Users"
    status="active"
  />
);

export const ExportQuickAction: React.FC = () => (
  <QuickActionCard
    title="Export Data"
    description="Download reports and system data"
    icon={<Download className="h-5 w-5" />}
    onClick={() => {
      // Handle export functionality
    }}
    badge="CSV, PDF"
    status="pending"
  />
);

export const ImportQuickAction: React.FC = () => (
  <QuickActionCard
    title="Import Data"
    description="Upload and process new ticket data"
    icon={<Upload className="h-5 w-5" />}
    onClick={() => {
      // Handle import functionality
    }}
    badge="SOW, CSV"
    status="pending"
  />
);

// Quick actions grid component
export const QuickActionsGrid: React.FC = () => {
  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      <TicketsQuickAction />
      <AnalyticsQuickAction />
      <SettingsQuickAction />
      <UsersQuickAction />
      <ExportQuickAction />
      <ImportQuickAction />
    </div>
  );
};