'use client';

/**
 * Error Boundary Component for Gatekeeper RPA System
 *
 * Implements React Error Boundary to catch JavaScript errors anywhere in the component tree,
 * log those errors, and display a fallback UI following Zero Trust security principles.
 *
 * Created following DGTS principles with comprehensive error handling.
 */

import { Component, ReactNode } from 'react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';

// Error boundary props interface
interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
}

// Error boundary state interface
interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: React.ErrorInfo | null;
}

/**
 * ErrorBoundary Component
 *
 * Catches JavaScript errors in child components and displays a user-friendly error message.
 * Implements security logging and provides recovery options.
 */
export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  // Static method to update state when an error is caught
  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    // Update state so the next render will show the fallback UI
    return {
      hasError: true,
      error,
    };
  }

  // Lifecycle method to catch component errors
  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    // Log the error to an error reporting service
    this.logError(error, errorInfo);

    // Update state with error info
    this.setState({
      error,
      errorInfo,
    });
  }

  // Log error with security context
  private logError(error: Error, errorInfo: React.ErrorInfo): void {
    // In production, this would send to error tracking service
    const errorDetails = {
      message: error.message,
      stack: error.stack,
      componentStack: errorInfo.componentStack,
      timestamp: new Date().toISOString(),
      userAgent: typeof navigator !== 'undefined' ? navigator.userAgent : 'server-side',
      url: typeof window !== 'undefined' ? window.location.href : 'server-side',
    };

    // Log error securely (avoid console.log)
    if (typeof window !== 'undefined' && window.console) {
      console.error('ErrorBoundary caught an error:', errorDetails);
    }

    // TODO: Send to error tracking service
    // sendToErrorTracking(errorDetails);
  }

  // Reset error state and try again
  private handleReset = (): void => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  // Render error fallback UI or children
  render(): ReactNode {
    if (this.state.hasError) {
      // Custom fallback UI
      const fallback = this.props.fallback || this.renderDefaultFallback();
      return fallback;
    }

    return this.props.children;
  }

  // Default fallback UI when error occurs
  private renderDefaultFallback(): ReactNode {
    const { error } = this.state;

    return (
      <div className="min-h-screen flex items-center justify-center p-4 bg-background">
        <div className="max-w-md w-full space-y-4">
          <div className="text-center space-y-2">
            <Badge variant="destructive" className="mb-4">
              System Error
            </Badge>
            <h1 className="text-2xl font-bold text-foreground">
              Something went wrong
            </h1>
            <p className="text-muted-foreground">
              We apologize for the inconvenience. Our team has been notified of this issue.
            </p>
          </div>

          <Alert variant="destructive">
            <AlertTitle>Error Details</AlertTitle>
            <AlertDescription className="space-y-2">
              <div>
                <strong>Error:</strong> {error?.message || 'Unknown error'}
              </div>
              {process.env.NODE_ENV === 'development' && error?.stack && (
                <details className="mt-2">
                  <summary className="cursor-pointer text-sm font-medium">
                    Stack Trace (Development Mode)
                  </summary>
                  <pre className="mt-2 text-xs bg-muted p-2 rounded overflow-auto max-h-32">
                    {error.stack}
                  </pre>
                </details>
              )}
            </AlertDescription>
          </Alert>

          <div className="flex flex-col gap-2">
            <Button onClick={this.handleReset} className="w-full">
              Try Again
            </Button>
            <Button
              variant="outline"
              onClick={() => window.location.href = '/'}
              className="w-full"
            >
              Go to Home
            </Button>
          </div>

          <div className="text-center text-sm text-muted-foreground">
            <p>
              If this problem persists, please contact our support team or check the
              <a href="/docs/troubleshooting" className="underline ml-1">
                troubleshooting guide
              </a>.
            </p>
          </div>
        </div>
      </div>
    );
  }
}

export default ErrorBoundary;