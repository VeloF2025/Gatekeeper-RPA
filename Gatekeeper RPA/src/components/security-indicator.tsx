'use client';

/**
 * Security Indicator Component for Gatekeeper RPA System
 *
 * Displays real-time security status and Zero Trust compliance information.
 * Following Zero Trust security principles with comprehensive monitoring.
 *
 * Created with security monitoring and compliance validation.
 */

import { useState, useEffect } from 'react';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';

// Security status interface
interface SecurityStatus {
  authentication: 'secure' | 'warning' | 'error';
  encryption: 'secure' | 'warning' | 'error';
  accessControl: 'secure' | 'warning' | 'error';
  auditLogging: 'secure' | 'warning' | 'error';
  overall: 'secure' | 'warning' | 'error';
  lastUpdated: Date;
  issues: SecurityIssue[];
}

// Security issue interface
interface SecurityIssue {
  id: string;
  type: 'authentication' | 'encryption' | 'access_control' | 'audit_logging';
  severity: 'low' | 'medium' | 'high' | 'critical';
  message: string;
  timestamp: Date;
}

/**
 * SecurityIndicator Component
 *
 * Real-time security monitoring dashboard with Zero Trust compliance indicators.
 * Implements security best practices for status monitoring.
 */
export function SecurityIndicator() {
  const [securityStatus, setSecurityStatus] = useState<SecurityStatus>({
    authentication: 'secure',
    encryption: 'secure',
    accessControl: 'secure',
    auditLogging: 'secure',
    overall: 'secure',
    lastUpdated: new Date(),
    issues: [],
  });

  const [isLoading, setIsLoading] = useState(true);

  // Simulate security status monitoring
  useEffect(() => {
    const checkSecurityStatus = async () => {
      try {
        // In a real implementation, this would fetch from security monitoring API
        const mockSecurityData: SecurityStatus = {
          authentication: 'secure',
          encryption: 'secure',
          accessControl: 'warning',
          auditLogging: 'secure',
          overall: 'warning',
          lastUpdated: new Date(),
          issues: [
            {
              id: 'access-001',
              type: 'access_control',
              severity: 'medium',
              message: 'Some user permissions need review',
              timestamp: new Date(),
            },
          ],
        };

        setSecurityStatus(mockSecurityData);
      } catch (error) {
        // In production, log this error securely
        console.error('Failed to fetch security status:', error);
      } finally {
        setIsLoading(false);
      }
    };

    checkSecurityStatus();

    // Set up periodic security checks
    const interval = setInterval(checkSecurityStatus, 30000); // Check every 30 seconds

    return () => clearInterval(interval);
  }, []);

  // Get badge variant based on security status
  const getStatusBadge = (status: 'secure' | 'warning' | 'error') => {
    switch (status) {
      case 'secure':
        return <Badge variant="default">✓ Secure</Badge>;
      case 'warning':
        return <Badge variant="outline">⚠ Warning</Badge>;
      case 'error':
        return <Badge variant="destructive">✗ Error</Badge>;
      default:
        return <Badge variant="outline">Unknown</Badge>;
    }
  };

  
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            🔒 Security Status
          </CardTitle>
          <CardDescription>
            Zero Trust security compliance monitoring
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="text-center text-muted-foreground">
            Loading security status...
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            🔒 Security Status
            {getStatusBadge(securityStatus.overall)}
          </CardTitle>
          <CardDescription>
            Zero Trust security compliance monitoring
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Authentication</span>
                {getStatusBadge(securityStatus.authentication)}
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Encryption</span>
                {getStatusBadge(securityStatus.encryption)}
              </div>
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Access Control</span>
                {getStatusBadge(securityStatus.accessControl)}
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Audit Logging</span>
                {getStatusBadge(securityStatus.auditLogging)}
              </div>
            </div>
          </div>
          <div className="mt-4 text-xs text-muted-foreground">
            Last updated: {securityStatus.lastUpdated.toLocaleTimeString()}
          </div>
        </CardContent>
      </Card>

      {securityStatus.issues.length > 0 && (
        <Alert>
          <AlertDescription>
            <div className="space-y-2">
              <div className="font-medium">Security Issues Detected</div>
              {securityStatus.issues.map((issue) => (
                <div key={issue.id} className="flex items-center gap-2 text-sm">
                  <Badge
                    variant={
                      issue.severity === 'critical' || issue.severity === 'high'
                        ? 'destructive'
                        : 'outline'
                    }
                    className="text-xs"
                  >
                    {issue.severity.toUpperCase()}
                  </Badge>
                  <span>{issue.message}</span>
                </div>
              ))}
            </div>
          </AlertDescription>
        </Alert>
      )}

      <div className="text-xs text-muted-foreground">
        <p>
          Zero Trust Architecture: All access requests are continuously validated
          regardless of location or network.
        </p>
      </div>
    </div>
  );
}