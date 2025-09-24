import { Pool, PoolClient, PoolConfig } from 'pg';
import { drizzle } from 'drizzle-orm/node-postgres';
import { sql } from 'drizzle-orm';
import { logger, errorLogger } from '@/utils/logger';
import config from '@/config';
import * as schema from './schemas/index';

// Database connection pool
class Database {
  private pool: Pool;
  private _drizzle: ReturnType<typeof drizzle<typeof schema>>;

  constructor() {
    const poolConfig: PoolConfig = {
      connectionString: config.database.url,
      ssl: config.database.ssl ? { rejectUnauthorized: false } : false,
      max: config.database.pool.max,
      min: config.database.pool.min,
      idleTimeoutMillis: config.database.pool.idle,
      connectionTimeoutMillis: 10000,
    };

    this.pool = new Pool(poolConfig);
    this._drizzle = drizzle(this.pool, { schema });

    // Setup pool event listeners
    this.pool.on('connect', () => {
      logger.debug('Database connection established');
    });

    this.pool.on('error', (err) => {
      errorLogger.error('Database pool error', { error: err.message });
    });

    this.pool.on('remove', () => {
      logger.debug('Database connection removed');
    });
  }

  /**
   * Get a client from the pool
   */
  async getClient(): Promise<PoolClient> {
    return await this.pool.connect();
  }

  /**
   * Execute a query with raw SQL
   */
  async query(text: string, params?: unknown[]): Promise<any[]> {
    const start = Date.now();

    try {
      const result = await this.pool.query(text, params);
      const duration = Date.now() - start;

      logger.debug('Query executed', {
        query: text,
        duration,
        rows: result.rowCount
      });

      return result.rows;
    } catch (error) {
      const duration = Date.now() - start;
      errorLogger.error('Query failed', {
        query: text,
        params,
        duration,
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Execute a query using Drizzle ORM
   */
  async executeQuery<T>(query: ReturnType<typeof sql>): Promise<T[]> {
    try {
      const result = await this._drizzle.execute(query);
      return result as unknown as T[];
    } catch (error) {
      errorLogger.error('Drizzle query failed', {
        query: String(query),
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Get Drizzle instance with schema
   */
  get drizzle() {
    return this._drizzle;
  }

  /**
   * Begin a transaction
   */
  async beginTransaction(): Promise<PoolClient> {
    const client = await this.getClient();
    await client.query('BEGIN');
    return client;
  }

  /**
   * Commit a transaction
   */
  async commitTransaction(client: PoolClient): Promise<void> {
    await client.query('COMMIT');
    client.release();
  }

  /**
   * Rollback a transaction
   */
  async rollbackTransaction(client: PoolClient): Promise<void> {
    await client.query('ROLLBACK');
    client.release();
  }

  /**
   * Execute queries within a transaction
   */
  async transaction<T>(callback: (client: PoolClient) => Promise<T>): Promise<T> {
    const client = await this.getClient();

    try {
      await client.query('BEGIN');
      const result = await callback(client);
      await client.query('COMMIT');
      return result;
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }

  /**
   * Health check
   */
  async healthCheck(): Promise<boolean> {
    try {
      await this.query('SELECT 1');
      return true;
    } catch (error) {
      errorLogger.error('Database health check failed', { error: error instanceof Error ? error.message : error });
      return false;
    }
  }

  /**
   * Get pool statistics
   */
  getPoolStats() {
    return {
      totalCount: this.pool.totalCount,
      idleCount: this.pool.idleCount,
      waitingCount: this.pool.waitingCount,
    };
  }

  /**
   * Close all connections in the pool
   */
  async close(): Promise<void> {
    try {
      await this.pool.end();
      logger.info('Database pool closed');
    } catch (error) {
      errorLogger.error('Error closing database pool', { error: error instanceof Error ? error.message : error });
    }
  }
}

// Export singleton instance
export const database = new Database();
export const db = database; // Alias for backward compatibility

// Export drizzle instance directly (avoid circular dependency)
export const drizzleInstance = database.drizzle;

// Export for testing
export { Database };