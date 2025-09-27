/**
 * Root Layout for Gatekeeper RPA System
 *
 * Implements the main application layout with Zero Trust security,
 * Clerk authentication, and proper error boundaries.
 *
 * Following NLNH and DGTS principles with zero tolerance for errors.
 */

import type { Metadata, Viewport } from 'next';
import { Inter } from 'next/font/google';
import { Toaster } from '@/components/ui/toaster';
import { ErrorBoundary } from '@/components/error-boundary';
import { SecurityProvider } from '@/providers/security-provider';
import { AuthProvider } from '@/providers/auth-provider';
import './globals.css';

// Configure Inter font with proper subsets
const inter = Inter({
  subsets: ['latin'],
  display: 'swap',
  variable: '--font-inter',
});

// Application metadata
export const metadata: Metadata = {
  title: 'Gatekeeper RPA - Automated Ticket Auditing',
  description: 'Enterprise-grade WhatsApp ticketing and audit automation system with Zero Trust security',
  keywords: ['RPA', 'automation', 'audit', 'WhatsApp', 'zero-trust', 'ticketing'],
  authors: [{ name: 'FibreField Technologies' }],
  robots: {
    index: false,
    follow: false,
  },
  manifest: '/manifest.json',
};

// Viewport configuration
export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
};

// Root layout component
export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
      <html lang="en" className={inter.variable}>
        <head>
          {/* Preconnect to external domains for performance */}
          <link rel="preconnect" href="https://fonts.googleapis.com" />
          <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />

          {/* Security meta tags */}
          <meta httpEquiv="Content-Security-Policy" content="default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline';" />
          <meta httpEquiv="X-Content-Type-Options" content="nosniff" />
          <meta httpEquiv="X-Frame-Options" content="DENY" />
          <meta httpEquiv="X-XSS-Protection" content="1; mode=block" />
        </head>
        <body className="min-h-screen bg-background font-sans antialiased">
          <ErrorBoundary>
            <SecurityProvider>
              <AuthProvider>
                <div className="relative flex min-h-screen flex-col">
                  {/* Skip to main content for accessibility */}
                  <a
                    href="#main-content"
                    className="absolute left-4 top-4 z-50 -translate-y-full rounded bg-primary px-4 py-2 text-primary-foreground opacity-0 transition-opacity focus:translate-y-0 focus:opacity-100"
                  >
                    Skip to main content
                  </a>

                  {/* Main application content */}
                  <main id="main-content" className="flex-1">
                    {children}
                  </main>

                  {/* Global toast notifications */}
                  <Toaster />
                </div>
              </AuthProvider>
            </SecurityProvider>
          </ErrorBoundary>
        </body>
      </html>
  );
}