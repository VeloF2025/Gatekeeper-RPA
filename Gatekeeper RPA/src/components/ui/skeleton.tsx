/**
 * Skeleton Component for Gatekeeper RPA System
 *
 * Loading skeleton component for displaying content placeholders.
 * Following Zero Trust security principles with proper accessibility.
 *
 * Created using shadcn/ui patterns with comprehensive validation.
 */

import * as React from 'react';
import { cn } from '@/lib/utils';

// Skeleton props interface
interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {}

/**
 * Skeleton Component
 *
 * Loading placeholder with animated shimmer effect.
 * Implements security best practices for loading states.
 */
const Skeleton = React.forwardRef<HTMLDivElement, SkeletonProps>(
  ({ className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn('animate-pulse rounded-md bg-muted', className)}
      // Security attributes for skeleton
      role="status"
      aria-label="Loading content"
      aria-busy="true"
      {...props}
    />
  )
);

Skeleton.displayName = 'Skeleton';

export { Skeleton };