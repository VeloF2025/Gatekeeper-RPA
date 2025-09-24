/**
 * Utility Functions for Gatekeeper RPA System
 *
 * Common utility functions with security validation and error handling.
 * Following Zero Trust principles with comprehensive input validation.
 *
 * Created with strict TypeScript typing and security best practices.
 */

import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

// Class name utility function for combining Tailwind classes
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

// Security utility functions
export const securityUtils = {
  /**
   * Sanitize string input to prevent XSS attacks
   */
  sanitizeInput: (input: string): string => {
    if (typeof input !== 'string') return '';
    return input
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#x27;')
      .replace(/\//g, '&#x2F;');
  },

  /**
   * Validate email format with strict regex
   */
  validateEmail: (email: string): boolean => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
  },

  /**
   * Generate secure random string
   */
  generateSecureToken: (length: number = 32): string => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const array = new Uint32Array(length);
    crypto.getRandomValues(array);
    for (let i = 0; i < length; i++) {
      result += chars[array[i] % chars.length];
    }
    return result;
  },

  /**
   * Validate URL format
   */
  validateUrl: (url: string): boolean => {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  },
};

// Date utility functions
export const dateUtils = {
  /**
   * Format date with consistent format
   */
  formatDate: (date: Date | string, format: string = 'YYYY-MM-DD'): string => {
    const d = new Date(date);
    if (isNaN(d.getTime())) return '';

    // Simple implementation - in production use a proper date library
    return d.toISOString().split('T')[0];
  },

  /**
   * Get relative time string
   */
  getRelativeTime: (date: Date | string): string => {
    const d = new Date(date);
    const now = new Date();
    const diffMs = now.getTime() - d.getTime();

    const diffMinutes = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffMinutes < 1) return 'just now';
    if (diffMinutes < 60) return `${diffMinutes} minute${diffMinutes > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;

    return dateUtils.formatDate(date);
  },
};

// Validation utility functions
export const validationUtils = {
  /**
   * Validate required fields
   */
  validateRequired: (value: any, fieldName: string): string | null => {
    if (value === null || value === undefined || value === '') {
      return `${fieldName} is required`;
    }
    return null;
  },

  /**
   * Validate string length
   */
  validateLength: (value: string, min: number, max: number, fieldName: string): string | null => {
    if (value.length < min) {
      return `${fieldName} must be at least ${min} characters`;
    }
    if (value.length > max) {
      return `${fieldName} must be no more than ${max} characters`;
    }
    return null;
  },

  /**
   * Validate numeric range
   */
  validateRange: (value: number, min: number, max: number, fieldName: string): string | null => {
    if (value < min) {
      return `${fieldName} must be at least ${min}`;
    }
    if (value > max) {
      return `${fieldName} must be no more than ${max}`;
    }
    return null;
  },
};

// Error handling utilities
export const errorUtils = {
  /**
   * Safe error parsing with proper typing
   */
  parseError: (error: unknown): { message: string; code?: string; details?: any } => {
    if (error instanceof Error) {
      return {
        message: error.message,
        code: (error as any).code,
        details: error,
      };
    }

    if (typeof error === 'string') {
      return {
        message: error,
      };
    }

    return {
      message: 'An unknown error occurred',
    };
  },

  /**
   * Retry function with exponential backoff
   */
  retry: async <T>(
    fn: () => Promise<T>,
    maxRetries: number = 3,
    delayMs: number = 1000
  ): Promise<T> => {
    let lastError: Error;

    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        return await fn();
      } catch (error) {
        lastError = error instanceof Error ? error : new Error(String(error));

        if (attempt === maxRetries) {
          throw lastError;
        }

        // Exponential backoff
        const delay = delayMs * Math.pow(2, attempt - 1);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }

    throw lastError!;
  },
};

// Performance utilities
export const performanceUtils = {
  /**
   * Debounce function to limit execution rate
   */
  debounce: <T extends (...args: any[]) => any>(
    func: T,
    wait: number
  ): (...args: Parameters<T>) => void => {
    let timeout: NodeJS.Timeout;
    return (...args: Parameters<T>) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => func(...args), wait);
    };
  },

  /**
   * Throttle function to limit execution rate
   */
  throttle: <T extends (...args: any[]) => any>(
    func: T,
    limit: number
  ): (...args: Parameters<T>) => void => {
    let inThrottle: boolean;
    return (...args: Parameters<T>) => {
      if (!inThrottle) {
        func(...args);
        inThrottle = true;
        setTimeout(() => (inThrottle = false), limit);
      }
    };
  },
};

export {
  securityUtils as security,
  dateUtils as date,
  validationUtils as validation,
  errorUtils as error,
  performanceUtils as performance,
};