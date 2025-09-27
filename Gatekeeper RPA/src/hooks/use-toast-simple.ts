'use client';

/**
 * Simple Toast Hook for Gatekeeper RPA System
 *
 * Simplified toast notification system to avoid module resolution issues.
 * Following Zero Trust security principles with proper event handling.
 */

import { useState, useEffect } from 'react';

// Toast interface
export interface ToastProps {
  id: string;
  title?: React.ReactNode;
  description?: React.ReactNode;
  variant?: 'default' | 'destructive';
}

// Toast state interface
interface ToastState {
  toasts: ToastProps[];
}

// Global toast state
let globalToasts: ToastProps[] = [];
let listeners: Array<(toasts: ToastProps[]) => void> = [];

// Dispatch function
const dispatch = (action: { type: string; toast?: ToastProps; toastId?: string }) => {
  switch (action.type) {
    case 'ADD_TOAST':
      if (action.toast) {
        globalToasts = [action.toast, ...globalToasts].slice(0, 10);
      }
      break;
    case 'REMOVE_TOAST':
      if (action.toastId) {
        globalToasts = globalToasts.filter(t => t.id !== action.toastId);
      } else {
        globalToasts = [];
      }
      break;
  }

  // Notify listeners
  listeners.forEach(listener => listener(globalToasts));
};

// Hook for using toast
export function useToast() {
  const [state, setState] = useState<ToastState>({ toasts: globalToasts });

  useEffect(() => {
    listeners.push(setState);
    return () => {
      const index = listeners.indexOf(setState);
      if (index > -1) {
        listeners.splice(index, 1);
      }
    };
  }, []);

  const toast = ({ title, description, variant = 'default' }: Omit<ToastProps, 'id'>) => {
    const id = Math.random().toString(36).substr(2, 9);

    dispatch({
      type: 'ADD_TOAST',
      toast: {
        id,
        title,
        description,
        variant,
      },
    });

    return {
      id,
      dismiss: () => dispatch({ type: 'REMOVE_TOAST', toastId: id }),
    };
  };

  const dismiss = (toastId?: string) => {
    dispatch({ type: 'REMOVE_TOAST', toastId });
  };

  return {
    toasts: state.toasts,
    toast,
    dismiss,
    dismissAll: () => dispatch({ type: 'REMOVE_TOAST' }),
  };
}

// Export as default for direct import
export default useToast;