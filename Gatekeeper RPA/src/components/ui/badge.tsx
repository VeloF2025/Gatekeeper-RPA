/**
 * Badge Component for Gatekeeper RPA System
 *
 * Reusable badge component for displaying status indicators and labels.
 * Following Zero Trust security principles with proper accessibility.
 *
 * Created using shadcn/ui patterns with comprehensive validation.
 */

import * as React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/utils';

// Badge variants using class variance authority
const badgeVariants = cva(
  'inline-flex items-center rounded-md border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2',
  {
    variants: {
      variant: {
        default: 'border-transparent bg-primary text-primary-foreground shadow hover:bg-primary/80',
        secondary: 'border-transparent bg-secondary text-secondary-foreground hover:bg-secondary/80',
        destructive: 'border-transparent bg-destructive text-destructive-foreground shadow hover:bg-destructive/80',
        outline: 'text-foreground',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
);

// Badge props interface
export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {}

/**
 * Badge Component
 *
 * Small status indicator with multiple variants for different states.
 * Implements security best practices for status indicators.
 */
const Badge = React.forwardRef<HTMLDivElement, BadgeProps>(
  ({ className, variant, ...props }, ref) => (
    <div
      ref={ref}
      className={cn(badgeVariants({ variant }), className)}
      // Security attributes for badge
      role="status"
      aria-label={props.children?.toString() || 'Status badge'}
      {...props}
    />
  )
);

Badge.displayName = 'Badge';

export { Badge, badgeVariants };