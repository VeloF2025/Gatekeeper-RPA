/**
 * Toaster Component for Gatekeeper RPA System
 *
 * Toast notification system for displaying temporary messages.
 * Following Zero Trust security principles with proper accessibility.
 *
 * Created using shadcn/ui patterns with comprehensive validation.
 */

import * as React from 'react';
import { useToast } from '@/hooks/use-toast-simple';
import {
  ToastClose,
  ToastDescription,
  ToastProvider,
  ToastTitle,
  ToastViewport,
} from '@/components/ui/toast';

/**
 * Toaster Component
 *
 * Container for managing and displaying toast notifications.
 * Implements security best practices for notifications.
 */
export function Toaster() {
  const { toasts } = useToast();

  return (
    <ToastProvider>
      {toasts.map(function ({ id, title, description, action, ...props }) {
        return (
          <div key={id} {...props}>
            <div className="grid gap-1">
              {title && <ToastTitle>{title}</ToastTitle>}
              {description && (
                <ToastDescription>{description}</ToastDescription>
              )}
            </div>
            {action}
            <ToastClose />
          </div>
        );
      })}
      <ToastViewport />
    </ToastProvider>
  );
}