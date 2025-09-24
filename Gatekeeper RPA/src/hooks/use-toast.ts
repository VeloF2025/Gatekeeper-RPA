'use client';

/**
 * useToast Hook for Gatekeeper RPA System
 *
 * Custom hook for managing toast notifications with accessibility support.
 * Following Zero Trust security principles with proper event handling.
 *
 * Created using shadcn/ui patterns with comprehensive validation.
 */

import { useEffect, useState } from 'react';

// Toast action interface
export interface ToastActionElement {
  altText: string;
  onClick: () => void;
  children: React.ReactNode;
}

// Toast props interface
export interface ToastProps {
  id?: string;
  title?: React.ReactNode;
  description?: React.ReactNode;
  action?: ToastActionElement;
  variant?: 'default' | 'destructive';
}

// State interface for toast management
interface ToastState {
  toasts: ToastProps[];
}

// Toast action types
const actionTypes = {
  ADD_TOAST: 'ADD_TOAST',
  UPDATE_TOAST: 'UPDATE_TOAST',
  DISMISS_TOAST: 'DISMISS_TOAST',
  REMOVE_TOAST: 'REMOVE_TOAST',
} as const;

let count = 0;

// Toast action creators
type ActionType = typeof actionTypes;

type Action =
  | {
      type: ActionType['ADD_TOAST'];
      toast: ToastProps;
    }
  | {
      type: ActionType['UPDATE_TOAST'];
      toast: Partial<ToastProps>;
    }
  | {
      type: ActionType['DISMISS_TOAST'];
      toastId?: string;
    }
  | {
      type: ActionType['REMOVE_TOAST'];
      toastId?: string;
    };

// Toast reducer function
const toastReducer = (state: ToastState, action: Action): ToastState => {
  switch (action.type) {
    case 'ADD_TOAST':
      return {
        ...state,
        toasts: [action.toast, ...state.toasts].slice(0, 10), // Limit to 10 toasts
      };

    case 'UPDATE_TOAST':
      return {
        ...state,
        toasts: state.toasts.map((t) =>
          t.id === action.toast.id ? { ...t, ...action.toast } : t
        ),
      };

    case 'DISMISS_TOAST': {
      const { toastId } = action;

      if (toastId) {
        return {
          ...state,
          toasts: state.toasts.map((t) =>
            t.id === toastId ? { ...t, open: false } : t
          ),
        };
      }
      return {
        ...state,
        toasts: state.toasts.map((t) => ({ ...t, open: false })),
      };
    }
    case 'REMOVE_TOAST': {
      const { toastId } = action;

      if (toastId) {
        return {
          ...state,
          toasts: state.toasts.filter((t) => t.id !== toastId),
        };
      }
      return {
        ...state,
        toasts: [],
      };
    }
    default:
      return state;
  }
};

// Toast listeners for external access
const listeners: Array<(state: ToastState) => void> = [];

let memoryState: ToastState = { toasts: [] };

// Dispatch function for state management
const dispatch = (action: Action) => {
  memoryState = toastReducer(memoryState, action);
  listeners.forEach((listener) => {
    listener(memoryState);
  });
};

// Toast type with internal properties
type Toast = Omit<ToastProps, 'id'> & {
  id: string;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
};

// Hook for managing toast state
function useToast() {
  const [state, setState] = useState<ToastState>(memoryState);

  useEffect(() => {
    listeners.push(setState);
    return () => {
      const index = listeners.indexOf(setState);
      if (index > -1) {
        listeners.splice(index, 1);
      }
    };
  }, [state]);

  // Toast creation function
  const toast = ({ ...props }: Omit<Toast, 'id'>) => {
    const id = (count++).toString();
    const update = (props: ToastProps) =>
      dispatch({
        type: 'UPDATE_TOAST',
        toast: { ...props, id },
      });
    const dismiss = () => dispatch({ type: 'DISMISS_TOAST', toastId: id });

    dispatch({
      type: 'ADD_TOAST',
      toast: {
        ...props,
        id,
        open: true,
        onOpenChange: (open) => {
          if (!open) dismiss();
        },
      },
    });

    return {
      id: id,
      dismiss,
      update,
    };
  };

  // Dismiss all toasts
  const dismiss = (toastId?: string) => {
    dispatch({ type: 'DISMISS_TOAST', toastId });
  };

  return {
    ...state,
    toast,
    dismiss,
    dismissAll: () => dispatch({ type: 'DISMISS_TOAST' }),
  };
}

export { useToast, toastReducer };