/**
 * Home Page - Gatekeeper RPA System
 *
 * Landing page for the automated ticket auditing system.
 * Provides system overview, quick stats, and navigation.
 *
 * Created from PRD requirements following DGTS principles.
 */

import { Suspense } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { SecurityIndicator } from '@/components/security-indicator';
import { SystemStatus } from '@/components/system-status';

// Loading components
function LoadingCard() {
  return (
    <Card className="h-32">
      <CardHeader>
        <Skeleton className="h-4 w-3/4" />
        <Skeleton className="h-3 w-1/2" />
      </CardHeader>
      <CardContent>
        <Skeleton className="h-8 w-1/3" />
      </CardContent>
    </Card>
  );
}

// Main page component
export default function HomePage() {
  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header section */}
      <section className="mb-12 text-center">
        <div className="mb-4">
          <Badge variant="outline" className="mb-4">
            Enterprise RPA System
          </Badge>
        </div>
        <h1 className="text-4xl font-bold tracking-tight lg:text-6xl">
          Gatekeeper RPA
        </h1>
        <p className="mt-4 text-xl text-muted-foreground max-w-2xl mx-auto">
          Automated WhatsApp ticketing and audit system with Zero Trust security for fiber network installations
        </p>
        <div className="mt-8 flex flex-wrap justify-center gap-4">
          <Button asChild size="lg">
            <Link href="/dashboard">Dashboard</Link>
          </Button>
          <Button variant="outline" asChild size="lg">
            <Link href="/tickets">View Tickets</Link>
          </Button>
        </div>
      </section>

      {/* Security status indicator */}
      <div className="mb-8">
        <SecurityIndicator />
      </div>

      {/* System status */}
      <div className="mb-12">
        <h2 className="text-2xl font-semibold mb-6 text-center">System Status</h2>
        <Suspense fallback={<LoadingCard />}>
          <SystemStatus />
        </Suspense>
      </div>

      {/* Feature cards */}
      <section className="mb-12">
        <h2 className="text-2xl font-semibold mb-6 text-center">Key Features</h2>
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                📱 WhatsApp Integration
              </CardTitle>
              <CardDescription>
                Enterprise WhatsApp ticketing with WhatiTicket platform
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="text-sm space-y-1 text-muted-foreground">
                <li>• Multi-account management</li>
                <li>• Automated ticket creation</li>
                <li>• Real-time message processing</li>
              </ul>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                🤖 RPA Automation
              </CardTitle>
              <CardDescription>
                Automated 1Map integration and photo extraction
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="text-sm space-y-1 text-muted-foreground">
                <li>• Playwright-based automation</li>
                <li>• Photo metadata extraction</li>
                <li>• Compliance validation</li>
              </ul>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                🔒 Zero Trust Security
              </CardTitle>
              <CardDescription>
                Enterprise-grade security with least privilege access
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="text-sm space-y-1 text-muted-foreground">
                <li>• Multi-factor authentication</li>
                <li>• Role-based access control</li>
                <li>• Audit logging & monitoring</li>
              </ul>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Quick stats */}
      <section className="mb-12">
        <h2 className="text-2xl font-semibold mb-6 text-center">System Overview</h2>
        <div className="grid gap-4 md:grid-cols-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Active Tickets</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">0</div>
              <p className="text-xs text-muted-foreground">Real-time count</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Processing Rate</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">0</div>
              <p className="text-xs text-muted-foreground">Tickets/hour</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Compliance Rate</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">0%</div>
              <p className="text-xs text-muted-foreground">Last 24 hours</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">System Health</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">100%</div>
              <p className="text-xs text-muted-foreground">All systems operational</p>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Alert for development status */}
      <Alert className="mb-8">
        <AlertDescription>
          🚧 This system is currently in development. Features are being implemented following
          DGTS (Documentation-Driven Test Development) principles with Zero Trust security architecture.
        </AlertDescription>
      </Alert>

      {/* Documentation links */}
      <section className="text-center">
        <h3 className="text-lg font-semibold mb-4">Documentation</h3>
        <div className="flex flex-wrap justify-center gap-4">
          <Button variant="outline" asChild>
            <Link href="/docs/prd">Product Requirements</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/docs/api">API Documentation</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/docs/deployment">Deployment Guide</Link>
          </Button>
        </div>
      </section>
    </div>
  );
}