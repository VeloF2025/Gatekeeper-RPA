/**
 * Rate Limiting Middleware for Gatekeeper RPA System
 *
 * Implements rate limiting for API endpoints to prevent abuse.
 * Following Zero Trust security principles with comprehensive monitoring.
 */

import { NextRequest, NextResponse } from 'next/server';
import { database } from '@/database/database';
import { logger } from '@/utils/logger';

export interface RateLimitOptions {
  requests: number;
  windowMs: number;
  keyGenerator?: (req: NextRequest) => string;
  skip?: (req: NextRequest) => boolean;
  errorMessage?: string;
}

interface RateLimitEntry {
  count: number;
  resetTime: number;
}

export function withRateLimit(
  handler: (req: NextRequest) => Promise<NextResponse>,
  options: RateLimitOptions
) {
  const {
    requests,
    windowMs,
    keyGenerator = (req) => {
      const ip = req.ip || req.headers.get('x-forwarded-for') || 'unknown';
      return ip;
    },
    skip = () => false,
    errorMessage = 'Too many requests'
  } = options;

  return async (req: NextRequest): Promise<NextResponse> => {
    try {
      // Skip rate limiting if conditions are met
      if (skip(req)) {
        return handler(req);
      }

      const key = keyGenerator(req);
      const now = Date.now();
      const resetTime = now + windowMs;

      // Try to get existing rate limit entry from database
      let entry: RateLimitEntry | null = null;
      try {
        const results = await database.query(
          'SELECT count, reset_time FROM rate_limits WHERE identifier = $1 AND window_end > $2',
          [key, now]
        );
        entry = results[0] ? { count: Number(results[0].count), resetTime: Number(results[0].reset_time) } : null;
      } catch (error) {
        // If table doesn't exist, create it or skip rate limiting
        logger.warn('Rate limit table not found, skipping rate limiting', { key, error });
        return handler(req);
      }

      // Reset counter if window has passed
      if (!entry || entry.resetTime < now) {
        const windowStart = now;
        const windowEnd = now + windowMs;

        await database.query(
          'INSERT INTO rate_limits (identifier, window_start, window_end, request_count, limit_type, metadata) VALUES ($1, $2, $3, $4, $5, $6) ON CONFLICT (identifier, window_start) DO UPDATE SET request_count = $4, window_end = $3',
          [key, windowStart, windowEnd, 1, 'api', {}]
        );
        return handler(req);
      }

      // Check if rate limit exceeded
      if (entry.count >= requests) {
        logger.warn('Rate limit exceeded', { key, count: entry.count, resetTime });
        return NextResponse.json(
          {
            success: false,
            error: errorMessage,
            message: 'Rate limit exceeded',
            retryAfter: Math.ceil((entry.resetTime - now) / 1000)
          },
          {
            status: 429,
            headers: {
              'X-RateLimit-Limit': requests.toString(),
              'X-RateLimit-Remaining': '0',
              'X-RateLimit-Reset': entry.resetTime.toString(),
              'Retry-After': Math.ceil((entry.resetTime - now) / 1000).toString()
            }
          }
        );
      }

      // Increment counter
      await database.query(
        'UPDATE rate_limits SET request_count = request_count + 1 WHERE identifier = $1 AND window_end > $2',
        [key, now]
      );

      // Add rate limit headers to response
      const response = await handler(req);
      response.headers.set('X-RateLimit-Limit', requests.toString());
      response.headers.set('X-RateLimit-Remaining', (requests - entry.count - 1).toString());
      response.headers.set('X-RateLimit-Reset', entry.resetTime.toString());

      return response;

    } catch (error) {
      logger.error('Rate limiting error:', error);
      // If rate limiting fails, allow the request but log the error
      return handler(req);
    }
  };
}

// Cleanup function to remove expired rate limit entries
export async function cleanupRateLimitEntries(): Promise<void> {
  try {
    const now = Date.now();

    await database.query(
      'DELETE FROM rate_limits WHERE window_end < $1',
      [now]
    );

    logger.info('Cleaned up expired rate limit entries');
  } catch (error) {
    logger.error('Failed to cleanup rate limit entries:', error);
  }
}

// Health check function to verify rate limiting is working
export async function checkRateLimiting(): Promise<boolean> {
  try {
    await database.query('SELECT 1 FROM rate_limits LIMIT 1');
    return true;
  } catch (error) {
    logger.warn('Rate limiting table not accessible', { error });
    return false;
  }
}