import { Redis } from 'redis';
import { logger } from '@/utils/logger';
import config from '@/config';

export interface RateLimitOptions {
  points: number;
  duration: number;
}

export interface RateLimitResult {
  success: boolean;
  remaining: number;
  resetTime: number;
  totalHits: number;
}

export class RateLimiterService {
  private redis: Redis;
  private prefix: string;

  constructor(redis?: Redis) {
    this.redis = redis || new Redis(config.redis.url);
    this.prefix = 'rate_limit:';
  }

  /**
   * Consume a point from the rate limit
   */
  async consume(key: string, options?: Partial<RateLimitOptions>): Promise<RateLimitResult> {
    const opts: RateLimitOptions = {
      points: config.rateLimit.points,
      duration: config.rateLimit.duration,
      ...options
    };

    const fullKey = `${this.prefix}${key}`;
    const now = Math.floor(Date.now() / 1000);
    const windowStart = now - Math.floor(opts.duration / 1000);

    try {
      // Use Redis pipeline for atomic operations
      const pipeline = this.redis.pipeline();

      // Remove old entries
      pipeline.zremrangebyscore(fullKey, 0, windowStart);

      // Add new entry
      pipeline.zadd(fullKey, now, `${now}-${Math.random()}`);

      // Set expiration
      pipeline.expire(fullKey, Math.floor(opts.duration / 1000));

      // Get count
      pipeline.zcard(fullKey);

      // Get TTL
      pipeline.ttl(fullKey);

      const results = await pipeline.exec();

      if (!results || results.length === 0) {
        throw new Error('Redis pipeline failed');
      }

      const count = results[3][1] as number;
      const ttl = results[4][1] as number;

      const remaining = Math.max(0, opts.points - count);
      const resetTime = now + ttl;

      const result: RateLimitResult = {
        success: count <= opts.points,
        remaining,
        resetTime,
        totalHits: count
      };

      logger.debug('Rate limit check', {
        key,
        count,
        remaining,
        success: result.success
      });

      return result;

    } catch (error) {
      logger.error('Rate limiter error', { key, error: error instanceof Error ? error.message : error });

      // Fail open if Redis is unavailable
      return {
        success: true,
        remaining: opts.points,
        resetTime: 0,
        totalHits: 0
      };
    }
  }

  /**
   * Check remaining points without consuming
   */
  async check(key: string, options?: Partial<RateLimitOptions>): Promise<RateLimitResult> {
    const opts: RateLimitOptions = {
      points: config.rateLimit.points,
      duration: config.rateLimit.duration,
      ...options
    };

    const fullKey = `${this.prefix}${key}`;
    const now = Math.floor(Date.now() / 1000);
    const windowStart = now - Math.floor(opts.duration / 1000);

    try {
      // Get count without adding new entry
      const count = await this.redis.zcount(fullKey, windowStart, now);

      const remaining = Math.max(0, opts.points - count);
      const ttl = await this.redis.ttl(fullKey);
      const resetTime = ttl > 0 ? now + ttl : 0;

      return {
        success: count < opts.points,
        remaining,
        resetTime,
        totalHits: count
      };

    } catch (error) {
      logger.error('Rate limiter check error', { key, error: error instanceof Error ? error.message : error });

      // Fail open if Redis is unavailable
      return {
        success: true,
        remaining: opts.points,
        resetTime: 0,
        totalHits: 0
      };
    }
  }

  /**
   * Reset rate limit for a key
   */
  async reset(key: string): Promise<boolean> {
    const fullKey = `${this.prefix}${key}`;

    try {
      await this.redis.del(fullKey);
      logger.info('Rate limit reset', { key });
      return true;
    } catch (error) {
      logger.error('Rate limiter reset error', { key, error: error instanceof Error ? error.message : error });
      return false;
    }
  }

  /**
   * Get rate limit status
   */
  async getStatus(key: string): Promise<{
    key: string;
    remaining: number;
    resetTime: number;
    totalHits: number;
    isLimited: boolean;
  }> {
    const result = await this.check(key);

    return {
      key,
      remaining: result.remaining,
      resetTime: result.resetTime,
      totalHits: result.totalHits,
      isLimited: !result.success
    };
  }

  /**
   * Clean up expired rate limit keys
   */
  async cleanup(): Promise<number> {
    try {
      const keys = await this.redis.keys(`${this.prefix}*`);

      if (keys.length === 0) {
        return 0;
      }

      const pipeline = this.redis.pipeline();
      let deletedCount = 0;

      for (const key of keys) {
        pipeline.ttl(key);
      }

      const results = await pipeline.exec();

      if (!results) {
        return 0;
      }

      const expiredKeys: string[] = [];
      for (let i = 0; i < results.length; i++) {
        const ttl = results[i][1] as number;
        if (ttl === -1) { // Key exists but has no expiration
          expiredKeys.push(keys[i]);
        }
      }

      if (expiredKeys.length > 0) {
        const deleteResult = await this.redis.del(...expiredKeys);
        deletedCount = deleteResult || 0;
      }

      logger.info('Rate limiter cleanup completed', { deletedCount });
      return deletedCount;

    } catch (error) {
      logger.error('Rate limiter cleanup error', { error: error instanceof Error ? error.message : error });
      return 0;
    }
  }

  /**
   * Close Redis connection
   */
  async close(): Promise<void> {
    try {
      await this.redis.quit();
      logger.info('Rate limiter Redis connection closed');
    } catch (error) {
      logger.error('Error closing rate limiter Redis connection', { error: error instanceof Error ? error.message : error });
    }
  }
}