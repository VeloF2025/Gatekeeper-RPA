import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { Pool, PoolClient } from 'pg';
import { drizzle } from 'drizzle-orm/node-postgres';
import { sql } from 'drizzle-orm';
import { database, Database } from '@/database/database';
import { logger, errorLogger } from '@/utils/logger';
import config from '@/config';

// Mock dependencies
vi.mock('pg');
vi.mock('drizzle-orm/node-postgres');
vi.mock('@/utils/logger');
vi.mock('@/config');

const mockPool = vi.mocked(Pool);
const mockDrizzle = vi.mocked(drizzle);
const mockLogger = vi.mocked(logger);
const mockErrorLogger = vi.mocked(errorLogger);

describe('Database Class', () => {
  let mockPoolInstance: any;
  let mockDrizzleInstance: any;

  beforeEach(() => {
    vi.clearAllMocks();

    // Mock config
    vi.mocked(config).database = {
      url: 'postgresql://test:test@localhost:5432/testdb',
      ssl: false,
      pool: {
        max: 20,
        min: 5,
        idle: 30000
      }
    };

    // Mock pool instance
    mockPoolInstance = {
      on: vi.fn(),
      connect: vi.fn(),
      query: vi.fn(),
      end: vi.fn(),
      totalCount: 10,
      idleCount: 5,
      waitingCount: 0
    };

    // Mock drizzle instance
    mockDrizzleInstance = {
      execute: vi.fn()
    };

    mockPool.mockImplementation(() => mockPoolInstance);
    mockDrizzle.mockReturnValue(mockDrizzleInstance);
  });

  describe('Constructor', () => {
    it('should create database instance with proper configuration', () => {
      const db = new Database();

      expect(mockPool).toHaveBeenCalledWith({
        connectionString: 'postgresql://test:test@localhost:5432/testdb',
        ssl: false,
        max: 20,
        min: 5,
        idleTimeoutMillis: 30000,
        connectionTimeoutMillis: 10000,
      });

      expect(mockDrizzle).toHaveBeenCalledWith(mockPoolInstance, expect.objectContaining({
        schema: expect.any(Object)
      }));

      expect(mockPoolInstance.on).toHaveBeenCalledWith('connect', expect.any(Function));
      expect(mockPoolInstance.on).toHaveBeenCalledWith('error', expect.any(Function));
      expect(mockPoolInstance.on).toHaveBeenCalledWith('remove', expect.any(Function));
    });

    it('should enable SSL when configured', () => {
      vi.mocked(config).database.ssl = true;

      const db = new Database();

      expect(mockPool).toHaveBeenCalledWith(
        expect.objectContaining({
          ssl: { rejectUnauthorized: false }
        })
      );
    });
  });

  describe('getClient', () => {
    it('should return a client from the pool', async () => {
      const mockClient = { query: vi.fn(), release: vi.fn() };
      mockPoolInstance.connect.mockResolvedValue(mockClient);

      const db = new Database();
      const client = await db.getClient();

      expect(client).toBe(mockClient);
      expect(mockPoolInstance.connect).toHaveBeenCalled();
    });

    it('should handle connection errors', async () => {
      const error = new Error('Connection failed');
      mockPoolInstance.connect.mockRejectedValue(error);

      const db = new Database();

      await expect(db.getClient()).rejects.toThrow('Connection failed');
    });
  });

  describe('query', () => {
    it('should execute query and return rows', async () => {
      const mockResult = {
        rows: [{ id: 1, name: 'test' }],
        rowCount: 1
      };
      mockPoolInstance.query.mockResolvedValue(mockResult);

      const db = new Database();
      const result = await db.query('SELECT * FROM test');

      expect(result).toEqual([{ id: 1, name: 'test' }]);
      expect(mockPoolInstance.query).toHaveBeenCalledWith('SELECT * FROM test', undefined);
      expect(mockLogger.debug).toHaveBeenCalledWith(
        'Query executed',
        expect.objectContaining({
          query: 'SELECT * FROM test',
          duration: expect.any(Number),
          rows: 1
        })
      );
    });

    it('should execute query with parameters', async () => {
      const mockResult = { rows: [{ id: 1 }], rowCount: 1 };
      mockPoolInstance.query.mockResolvedValue(mockResult);

      const db = new Database();
      const result = await db.query('SELECT * FROM test WHERE id = $1', [1]);

      expect(mockPoolInstance.query).toHaveBeenCalledWith('SELECT * FROM test WHERE id = $1', [1]);
    });

    it('should handle query errors', async () => {
      const error = new Error('Query failed');
      mockPoolInstance.query.mockRejectedValue(error);

      const db = new Database();

      await expect(db.query('SELECT * FROM test')).rejects.toThrow('Query failed');

      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Query failed',
        expect.objectContaining({
          query: 'SELECT * FROM test',
          params: undefined,
          duration: expect.any(Number),
          error: 'Query failed'
        })
      );
    });
  });

  describe('executeQuery', () => {
    it('should execute Drizzle query', async () => {
      const mockResult = [{ id: 1, name: 'test' }];
      mockDrizzleInstance.execute.mockResolvedValue(mockResult);

      const db = new Database();
      const query = sql`SELECT * FROM test`;
      const result = await db.executeQuery(query);

      expect(result).toEqual(mockResult);
      expect(mockDrizzleInstance.execute).toHaveBeenCalledWith(query);
    });

    it('should handle Drizzle query errors', async () => {
      const error = new Error('Drizzle query failed');
      mockDrizzleInstance.execute.mockRejectedValue(error);

      const db = new Database();
      const query = sql`SELECT * FROM test`;

      await expect(db.executeQuery(query)).rejects.toThrow('Drizzle query failed');

      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Drizzle query failed',
        expect.objectContaining({
          query: expect.any(Object),
          error: 'Drizzle query failed'
        })
      );
    });
  });

  describe('drizzle getter', () => {
    it('should return the Drizzle instance', () => {
      const db = new Database();
      const drizzleInstance = db.drizzle;

      expect(drizzleInstance).toBe(mockDrizzleInstance);
    });
  });

  describe('Transaction methods', () => {
    let mockClient: PoolClient;
    let db: Database;

    beforeEach(() => {
      mockClient = {
        query: vi.fn(),
        release: vi.fn()
      };

      mockPoolInstance.connect.mockResolvedValue(mockClient);
      db = new Database();
    });

    describe('beginTransaction', () => {
      it('should begin a transaction', async () => {
        const client = await db.beginTransaction();

        expect(client).toBe(mockClient);
        expect(mockClient.query).toHaveBeenCalledWith('BEGIN');
      });
    });

    describe('commitTransaction', () => {
      it('should commit transaction and release client', async () => {
        await db.commitTransaction(mockClient);

        expect(mockClient.query).toHaveBeenCalledWith('COMMIT');
        expect(mockClient.release).toHaveBeenCalled();
      });
    });

    describe('rollbackTransaction', () => {
      it('should rollback transaction and release client', async () => {
        await db.rollbackTransaction(mockClient);

        expect(mockClient.query).toHaveBeenCalledWith('ROLLBACK');
        expect(mockClient.release).toHaveBeenCalled();
      });
    });

    describe('transaction', () => {
      it('should execute callback within transaction and commit', async () => {
        const callback = vi.fn().mockResolvedValue('success');
        const result = await db.transaction(callback);

        expect(result).toBe('success');
        expect(callback).toHaveBeenCalledWith(mockClient);
        expect(mockClient.query).toHaveBeenCalledWith('BEGIN');
        expect(mockClient.query).toHaveBeenCalledWith('COMMIT');
        expect(mockClient.release).toHaveBeenCalled();
      });

      it('should rollback transaction on error', async () => {
        const error = new Error('Transaction failed');
        const callback = vi.fn().mockRejectedValue(error);

        await expect(db.transaction(callback)).rejects.toThrow('Transaction failed');

        expect(callback).toHaveBeenCalledWith(mockClient);
        expect(mockClient.query).toHaveBeenCalledWith('BEGIN');
        expect(mockClient.query).toHaveBeenCalledWith('ROLLBACK');
        expect(mockClient.release).toHaveBeenCalled();
      });

      it('should release client even if commit fails', async () => {
        const error = new Error('Commit failed');
        const callback = vi.fn().mockResolvedValue('success');
        mockClient.query.mockImplementation((query: string) => {
          if (query === 'COMMIT') throw error;
        });

        await expect(db.transaction(callback)).rejects.toThrow('Commit failed');

        expect(mockClient.release).toHaveBeenCalled();
      });
    });
  });

  describe('healthCheck', () => {
    it('should return true when database is healthy', async () => {
      mockPoolInstance.query.mockResolvedValue({ rows: [{ '?column?': 1 }] });

      const db = new Database();
      const result = await db.healthCheck();

      expect(result).toBe(true);
      expect(mockPoolInstance.query).toHaveBeenCalledWith('SELECT 1');
    });

    it('should return false when database is unhealthy', async () => {
      const error = new Error('Connection failed');
      mockPoolInstance.query.mockRejectedValue(error);

      const db = new Database();
      const result = await db.healthCheck();

      expect(result).toBe(false);
      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Database health check failed',
        { error: 'Connection failed' }
      );
    });
  });

  describe('getPoolStats', () => {
    it('should return pool statistics', () => {
      const db = new Database();
      const stats = db.getPoolStats();

      expect(stats).toEqual({
        totalCount: 10,
        idleCount: 5,
        waitingCount: 0
      });
    });
  });

  describe('close', () => {
    it('should close the pool', async () => {
      const db = new Database();
      await db.close();

      expect(mockPoolInstance.end).toHaveBeenCalled();
      expect(mockLogger.info).toHaveBeenCalledWith('Database pool closed');
    });

    it('should handle close errors', async () => {
      const error = new Error('Close failed');
      mockPoolInstance.end.mockRejectedValue(error);

      const db = new Database();
      await db.close();

      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Error closing database pool',
        { error: 'Close failed' }
      );
    });
  });

  describe('Event listeners', () => {
    it('should set up event listeners for pool events', () => {
      const db = new Database();

      expect(mockPoolInstance.on).toHaveBeenCalledWith('connect', expect.any(Function));
      expect(mockPoolInstance.on).toHaveBeenCalledWith('error', expect.any(Function));
      expect(mockPoolInstance.on).toHaveBeenCalledWith('remove', expect.any(Function));

      // Test event handlers
      const connectHandler = (mockPoolInstance.on as any).mock.calls.find(
        (call: any[]) => call[0] === 'connect'
      )?.[1];

      if (connectHandler) {
        connectHandler();
        expect(mockLogger.debug).toHaveBeenCalledWith('Database connection established');
      }

      const errorHandler = (mockPoolInstance.on as any).mock.calls.find(
        (call: any[]) => call[0] === 'error'
      )?.[1];

      if (errorHandler) {
        const testError = new Error('Pool error');
        errorHandler(testError);
        expect(mockErrorLogger.error).toHaveBeenCalledWith(
          'Database pool error',
          { error: testError.message }
        );
      }

      const removeHandler = (mockPoolInstance.on as any).mock.calls.find(
        (call: any[]) => call[0] === 'remove'
      )?.[1];

      if (removeHandler) {
        removeHandler();
        expect(mockLogger.debug).toHaveBeenCalledWith('Database connection removed');
      }
    });
  });

  describe('Error handling edge cases', () => {
    it('should handle errors in query timing', async () => {
      const error = new Error('Query timeout');
      mockPoolInstance.query.mockRejectedValue(error);

      const db = new Database();

      await expect(db.query('SELECT 1')).rejects.toThrow('Query timeout');

      // Ensure duration is still logged even when query fails
      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Query failed',
        expect.objectContaining({
          query: 'SELECT 1',
          duration: expect.any(Number)
        })
      );
    });

    it('should handle non-Error objects in error logging', async () => {
      const error = 'String error';
      mockPoolInstance.query.mockRejectedValue(error);

      const db = new Database();

      await expect(db.query('SELECT 1')).rejects.toThrow('String error');

      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Query failed',
        expect.objectContaining({
          error: 'String error'
        })
      );
    });

    it('should handle Drizzle query execution with complex results', async () => {
      const complexResult = [
        { id: 1, data: { nested: { value: 'test' } } },
        { id: 2, data: null }
      ];
      mockDrizzleInstance.execute.mockResolvedValue(complexResult);

      const db = new Database();
      const query = sql`SELECT * FROM complex_table`;
      const result = await db.executeQuery(query);

      expect(result).toEqual(complexResult);
    });
  });

  describe('Singleton instance', () => {
    it('should export singleton database instance', () => {
      expect(database).toBeInstanceOf(Database);
    });

    it('should export Database class for testing', () => {
      expect(Database).toBeDefined();
      expect(typeof Database).toBe('function');
    });
  });
});