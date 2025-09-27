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
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-indigo-950">
      <div className="container-safe">
        {/* Header section */}
        <section className="mb-16 text-center pt-8">
          <div className="mb-6">
            <Badge variant="secondary" className="mb-4 px-4 py-2 text-sm font-semibold bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200 border-blue-200 dark:border-blue-700">
              🏢 Enterprise RPA System
            </Badge>
          </div>
          <h1 className="text-5xl md:text-6xl lg:text-7xl font-bold tracking-tight bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent mb-6">
            Gatekeeper RPA
          </h1>
          <p className="text-lg md:text-xl text-slate-600 dark:text-slate-300 max-w-3xl mx-auto leading-relaxed">
            Automated WhatsApp ticketing and audit system with Zero Trust security for fiber network installations
          </p>
          <div className="mt-10 flex flex-col sm:flex-row justify-center items-center gap-4">
            <Button asChild size="lg" className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-8 rounded-lg shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105">
              <Link href="/dashboard">
                <span className="mr-2">📊</span>
                Dashboard
              </Link>
            </Button>
            <Button variant="outline" asChild size="lg" className="border-blue-200 dark:border-blue-700 text-blue-700 dark:text-blue-300 hover:bg-blue-50 dark:hover:bg-blue-900/20 font-semibold py-3 px-8 rounded-lg">
              <Link href="/tickets">
                <span className="mr-2">🎫</span>
                View Tickets
              </Link>
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
      <section className="mb-16">
        <h2 className="text-3xl font-bold text-center mb-12 text-slate-800 dark:text-slate-200">
          🚀 Key Features
        </h2>
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          <Card className="group hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border-l-4 border-l-green-500 bg-white dark:bg-slate-800/50 backdrop-blur-sm">
            <CardHeader className="pb-4">
              <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                <span className="text-2xl">📱</span>
              </div>
              <CardTitle className="text-xl font-bold text-slate-800 dark:text-slate-200 group-hover:text-green-600 dark:group-hover:text-green-400 transition-colors">
                WhatsApp Integration
              </CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Enterprise WhatsApp ticketing with WhatiTicket platform
              </CardDescription>
            </CardHeader>
            <CardContent className="pt-0">
              <ul className="space-y-3">
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Multi-account management</span>
                </li>
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Automated ticket creation</span>
                </li>
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Real-time message processing</span>
                </li>
              </ul>
            </CardContent>
          </Card>

          <Card className="group hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border-l-4 border-l-blue-500 bg-white dark:bg-slate-800/50 backdrop-blur-sm">
            <CardHeader className="pb-4">
              <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                <span className="text-2xl">🤖</span>
              </div>
              <CardTitle className="text-xl font-bold text-slate-800 dark:text-slate-200 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                RPA Automation
              </CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Automated 1Map integration and photo extraction
              </CardDescription>
            </CardHeader>
            <CardContent className="pt-0">
              <ul className="space-y-3">
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Playwright-based automation</span>
                </li>
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Photo metadata extraction</span>
                </li>
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Compliance validation</span>
                </li>
              </ul>
            </CardContent>
          </Card>

          <Card className="group hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border-l-4 border-l-purple-500 bg-white dark:bg-slate-800/50 backdrop-blur-sm">
            <CardHeader className="pb-4">
              <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                <span className="text-2xl">🔒</span>
              </div>
              <CardTitle className="text-xl font-bold text-slate-800 dark:text-slate-200 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors">
                Zero Trust Security
              </CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Enterprise-grade security with least privilege access
              </CardDescription>
            </CardHeader>
            <CardContent className="pt-0">
              <ul className="space-y-3">
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-purple-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Multi-factor authentication</span>
                </li>
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-purple-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Role-based access control</span>
                </li>
                <li className="flex items-center text-slate-700 dark:text-slate-300">
                  <span className="w-2 h-2 bg-purple-500 rounded-full mr-3 flex-shrink-0"></span>
                  <span className="text-sm">Audit logging & monitoring</span>
                </li>
              </ul>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Quick stats */}
      <section className="mb-16">
        <h2 className="text-3xl font-bold text-center mb-12 text-slate-800 dark:text-slate-200">
          📊 System Overview
        </h2>
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          <Card className="group hover:shadow-xl transition-all duration-300 hover:-translate-y-1 bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border-blue-200 dark:border-blue-800">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-sm font-semibold text-blue-700 dark:text-blue-300">Active Tickets</CardTitle>
                <div className="w-8 h-8 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                  <span className="text-blue-600 dark:text-blue-400">🎫</span>
                </div>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <div className="text-3xl font-bold text-blue-900 dark:text-blue-100 group-hover:scale-105 transition-transform">0</div>
              <p className="text-xs text-blue-600 dark:text-blue-400 mt-1">Real-time count</p>
              <div className="mt-3 h-1 bg-blue-200 dark:bg-blue-800 rounded-full overflow-hidden">
                <div className="h-full bg-blue-500 rounded-full w-0 group-hover:w-1/3 transition-all duration-500"></div>
              </div>
            </CardContent>
          </Card>

          <Card className="group hover:shadow-xl transition-all duration-300 hover:-translate-y-1 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-green-900/20 dark:to-emerald-900/20 border-green-200 dark:border-green-800">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-sm font-semibold text-green-700 dark:text-green-300">Processing Rate</CardTitle>
                <div className="w-8 h-8 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                  <span className="text-green-600 dark:text-green-400">⚡</span>
                </div>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <div className="text-3xl font-bold text-green-900 dark:text-green-100 group-hover:scale-105 transition-transform">0</div>
              <p className="text-xs text-green-600 dark:text-green-400 mt-1">Tickets/hour</p>
              <div className="mt-3 h-1 bg-green-200 dark:bg-green-800 rounded-full overflow-hidden">
                <div className="h-full bg-green-500 rounded-full w-0 group-hover:w-2/3 transition-all duration-500"></div>
              </div>
            </CardContent>
          </Card>

          <Card className="group hover:shadow-xl transition-all duration-300 hover:-translate-y-1 bg-gradient-to-br from-purple-50 to-violet-50 dark:from-purple-900/20 dark:to-violet-900/20 border-purple-200 dark:border-purple-800">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-sm font-semibold text-purple-700 dark:text-purple-300">Compliance Rate</CardTitle>
                <div className="w-8 h-8 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                  <span className="text-purple-600 dark:text-purple-400">✅</span>
                </div>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <div className="text-3xl font-bold text-purple-900 dark:text-purple-100 group-hover:scale-105 transition-transform">0%</div>
              <p className="text-xs text-purple-600 dark:text-purple-400 mt-1">Last 24 hours</p>
              <div className="mt-3 h-1 bg-purple-200 dark:bg-purple-800 rounded-full overflow-hidden">
                <div className="h-full bg-purple-500 rounded-full w-0 group-hover:w-full transition-all duration-700"></div>
              </div>
            </CardContent>
          </Card>

          <Card className="group hover:shadow-xl transition-all duration-300 hover:-translate-y-1 bg-gradient-to-br from-emerald-50 to-teal-50 dark:from-emerald-900/20 dark:to-teal-900/20 border-emerald-200 dark:border-emerald-800">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-sm font-semibold text-emerald-700 dark:text-emerald-300">System Health</CardTitle>
                <div className="w-8 h-8 bg-emerald-100 dark:bg-emerald-900/30 rounded-lg flex items-center justify-center">
                  <span className="text-emerald-600 dark:text-emerald-400">💚</span>
                </div>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <div className="text-3xl font-bold text-emerald-900 dark:text-emerald-100 group-hover:scale-105 transition-transform">100%</div>
              <p className="text-xs text-emerald-600 dark:text-emerald-400 mt-1">All systems operational</p>
              <div className="mt-3 h-1 bg-emerald-200 dark:bg-emerald-800 rounded-full overflow-hidden">
                <div className="h-full bg-emerald-500 rounded-full w-full animate-pulse"></div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Alert for development status */}
      <Alert className="mb-12 bg-gradient-to-r from-amber-50 to-yellow-50 dark:from-amber-900/20 dark:to-yellow-900/20 border-amber-200 dark:border-amber-800">
        <div className="flex items-start gap-3">
          <div className="w-10 h-10 bg-amber-100 dark:bg-amber-900/30 rounded-lg flex items-center justify-center flex-shrink-0 mt-0.5">
            <span className="text-amber-600 dark:text-amber-400 text-lg">🚧</span>
          </div>
          <div>
            <AlertDescription className="text-amber-800 dark:text-amber-200 font-medium">
              <strong>Development Status:</strong> This system is currently in active development.
              Features are being implemented following DGTS (Documentation-Driven Test Development)
              principles with Zero Trust security architecture.
            </AlertDescription>
          </div>
        </div>
      </Alert>

      {/* Documentation links */}
      <section className="text-center mb-12">
        <h3 className="text-2xl font-bold mb-8 text-slate-800 dark:text-slate-200">
          📚 Documentation & Resources
        </h3>
        <div className="flex flex-col sm:flex-row justify-center items-center gap-4 flex-wrap">
          <Button variant="outline" asChild className="group border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800/50 font-medium py-3 px-6 rounded-lg">
            <Link href="/docs/prd">
              <span className="mr-2 group-hover:scale-110 transition-transform">📋</span>
              Product Requirements
            </Link>
          </Button>
          <Button variant="outline" asChild className="group border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800/50 font-medium py-3 px-6 rounded-lg">
            <Link href="/docs/api">
              <span className="mr-2 group-hover:scale-110 transition-transform">🔧</span>
              API Documentation
            </Link>
          </Button>
          <Button variant="outline" asChild className="group border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800/50 font-medium py-3 px-6 rounded-lg">
            <Link href="/docs/deployment">
              <span className="mr-2 group-hover:scale-110 transition-transform">🚀</span>
              Deployment Guide
            </Link>
          </Button>
        </div>
      </section>
    </div>
    </div>
  );
}