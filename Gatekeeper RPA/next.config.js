/**
 * Next.js Configuration for Gatekeeper RPA System
 *
 * Configures Next.js 14+ with App Router, security optimizations,
 * and performance settings following Zero Trust principles.
 */

const nextConfig = {
  // App Router configuration
  experimental: {
    // Enable server actions for better performance
    serverActions: {
      bodySizeLimit: '1mb',
      allowedOrigins: ['localhost:3000'],
    },

    // Optimize bundle size
    optimizePackageImports: ['react', 'react-dom', 'axios'],
  },

  // Security headers configuration (Zero Trust principles)
  async headers() {
    return [
      {
        source: '/(.*)',
        headers: [
          {
            key: 'X-Frame-Options',
            value: 'DENY',
          },
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff',
          },
          {
            key: 'Referrer-Policy',
            value: 'strict-origin-when-cross-origin',
          },
          {
            key: 'Content-Security-Policy',
            value: [
              "default-src 'self'",
              "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
              "style-src 'self' 'unsafe-inline'",
              "img-src 'self' data: https:",
              "font-src 'self'",
              "connect-src 'self' https: wss:",
              "frame-src 'self'",
              "object-src 'none'",
              "base-uri 'self'",
              "form-action 'self'",
            ].join('; '),
          },
          {
            key: 'Permissions-Policy',
            value: 'camera=(), microphone=(), geolocation=()',
          },
          {
            key: 'Strict-Transport-Security',
            value: 'max-age=31536000; includeSubDomains; preload',
          },
        ],
      },
      {
        source: '/api/:path*',
        headers: [
          {
            key: 'Cache-Control',
            value: 'no-store, max-age=0',
          },
        ],
      },
    ];
  },

  // Performance optimizations
  compress: true,
  poweredByHeader: false,

  // Bundle analyzer conditionally loaded
  ...(process.env.ANALYZE === 'true' && {
    bundleAnalyzer: {
      enabled: true,
    },
  }),

  // Environment variables that should be available to the client
  env: {
    CUSTOM_KEY: process.env.CUSTOM_KEY,
  },

  // Webpack configuration for additional optimizations
  webpack: (config, { isServer, dev }) => {
    // Optimize bundle size
    if (!dev && !isServer) {
      Object.assign(config.resolve.alias, {
        'react/jsx-runtime.js': 'react/jsx-runtime',
        'react/jsx-dev-runtime.js': 'react/jsx-dev-runtime',
      });
    }

    // Add custom plugins or loaders if needed
    config.module.rules.push({
      test: /\.(svg|png|jpg|jpeg|gif|webp)$/i,
      type: 'asset/resource',
      generator: {
        filename: 'static/images/[hash][ext]',
      },
    });

    return config;
  },

  // Static file optimizations
  staticPageGenerationTimeout: 60,

  // Image optimization
  images: {
    formats: ['image/webp', 'image/avif'],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920, 2048, 3840],
    imageSizes: [16, 32, 48, 64, 96, 128, 256, 384],
    remotePatterns: [
      {
        protocol: 'https',
        hostname: '**',
      },
    ],
  },

  // Redirects configuration if needed
  async redirects() {
    return [];
  },

  // Rewrites configuration if needed
  async rewrites() {
    return [];
  },
};

module.exports = nextConfig;