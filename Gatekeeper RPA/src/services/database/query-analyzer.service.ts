/**
 * Query Performance Analyzer Service
 * Analyzes and optimizes database query performance
 */

import { db } from '@/database/database';
import { logger } from '@/lib/logger';
import { performanceMonitor } from '@/services/monitoring/performance-monitor.service';
import { sql } from 'drizzle-orm';

export interface QueryAnalysis {
  query: string;
  duration: number;
  executionPlan?: any;
  recommendations: string[];
  cost?: number;
  rowsAffected?: number;
  indexesUsed?: string[];
  indexesMissing?: string[];
}

export interface IndexRecommendation {
  tableName: string;
  columnName: string;
  indexType: 'btree' | 'hash' | 'gin' | 'gist';
  estimatedBenefit: number;
  reason: string;
}

export interface DatabaseStats {
  totalSize: string;
  tableSizes: Array<{
    tableName: string;
    size: string;
    rows: number;
  }>;
  indexSizes: Array<{
    indexName: string;
    tableName: string;
    size: string;
  }>;
  cacheHitRate: number;
  slowQueries: number;
}

class QueryAnalyzer {
  private queryCache = new Map<string, QueryAnalysis>();
  private slowQueryThreshold = 100; // ms

  /**
   * Analyze a query execution
   */
  async analyzeQuery(query: string, params?: any[]): Promise<QueryAnalysis> {
    const startTime = performance.now();

    try {
      // Execute EXPLAIN ANALYZE
      const explainResult = await db.execute(
        sql`EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) ${query}`,
        params
      );

      const duration = performance.now() - startTime;
      const plan = explainResult[0]?.['QUERY PLAN']?.[0];

      const analysis: QueryAnalysis = {
        query,
        duration,
        executionPlan: plan,
        recommendations: this.generateRecommendations(plan),
        cost: plan?.['Total Cost'] || 0,
        rowsAffected: plan?.['Actual Rows'] || 0,
        indexesUsed: this.extractIndexesUsed(plan),
        indexesMissing: this.extractMissingIndexes(plan)
      };

      // Cache the analysis
      this.queryCache.set(query, analysis);

      // Record metrics
      performanceMonitor.recordDatabaseQuery(
        query,
        this.extractTableName(query),
        this.extractOperationType(query),
        duration
      );

      // Log slow queries
      if (duration > this.slowQueryThreshold) {
        logger.warn('Slow query detected', {
          query: query.substring(0, 100) + '...',
          duration,
          recommendations: analysis.recommendations
        });
      }

      return analysis;

    } catch (error) {
      const duration = performance.now() - startTime;
      logger.error('Query analysis failed', {
        query: query.substring(0, 100) + '...',
        error: error instanceof Error ? error.message : error,
        duration
      });

      throw error;
    }
  }

  /**
   * Get database statistics
   */
  async getDatabaseStats(): Promise<DatabaseStats> {
    try {
      // Get database size
      const dbSize = await db.execute(sql`
        SELECT pg_size_pretty(pg_database_size(current_database())) as size
      `);

      // Get table sizes
      const tableSizes = await db.execute(sql`
        SELECT
          schemaname,
          tablename,
          pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size,
          n_tup_ins + n_tup_upd + n_tup_del as row_activity
        FROM pg_stat_user_tables
        ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
      `);

      // Get index sizes
      const indexSizes = await db.execute(sql`
        SELECT
          indexname,
          tablename,
          pg_size_pretty(pg_relation_size(schemaname||'.'||indexname)) as size
        FROM pg_indexes
        JOIN pg_stat_user_tables ON pg_indexes.schemaname = pg_stat_user_tables.schemaname AND pg_indexes.tablename = pg_stat_user_tables.tablename
        ORDER BY pg_relation_size(schemaname||'.'||indexname) DESC
      `);

      // Get cache hit rate
      const cacheStats = await db.execute(sql`
        SELECT
          heap_blks_hit * 100.0 / (heap_blks_hit + heap_blks_read) as cache_hit_rate
        FROM pg_statio_user_tables
        WHERE schemaname = 'public'
        LIMIT 1
      `);

      // Get slow query count
      const slowQueryCount = await db.execute(sql`
        SELECT COUNT(*) as count
        FROM pg_stat_statements
        WHERE mean_exec_time > ${this.slowQueryThreshold}
      `);

      return {
        totalSize: dbSize[0]?.size || 'Unknown',
        tableSizes: tableSizes.map(t => ({
          tableName: t.tablename,
          size: t.size || '0 bytes',
          rows: t.row_activity || 0
        })),
        indexSizes: indexSizes.map(i => ({
          indexName: i.indexname,
          tableName: i.tablename,
          size: i.size || '0 bytes'
        })),
        cacheHitRate: cacheStats[0]?.cache_hit_rate || 0,
        slowQueries: parseInt(slowQueryCount[0]?.count || '0')
      };

    } catch (error) {
      logger.error('Failed to get database stats', {
        error: error instanceof Error ? error.message : error
      });
      throw error;
    }
  }

  /**
   * Generate index recommendations
   */
  async generateIndexRecommendations(): Promise<IndexRecommendation[]> {
    try {
      // Find columns that are frequently used in WHERE clauses but not indexed
      const recommendations = await db.execute(sql`
        SELECT
          schemaname,
          tablename,
          attname,
          n_distinct,
          correlation
        FROM pg_stats
        WHERE schemaname = 'public'
          AND attname NOT IN (
            SELECT indexname
            FROM pg_indexes
            WHERE schemaname = 'public'
          )
        ORDER BY n_distinct DESC
        LIMIT 10
      `);

      return recommendations.map((rec: any) => ({
        tableName: rec.tablename,
        columnName: rec.attname,
        indexType: 'btree',
        estimatedBenefit: this.calculateIndexBenefit(rec.n_distinct, rec.correlation),
        reason: `Column has ${rec.n_distinct} distinct values with ${rec.correlation} correlation`
      }));

    } catch (error) {
      logger.error('Failed to generate index recommendations', {
        error: error instanceof Error ? error.message : error
      });
      return [];
    }
  }

  /**
   * Get slow queries report
   */
  async getSlowQueries(limit: number = 20): Promise<QueryAnalysis[]> {
    try {
      const slowQueries = await db.execute(sql`
        SELECT
          query,
          mean_exec_time as duration,
          calls,
          rows,
          100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) as hit_percent
        FROM pg_stat_statements
        WHERE mean_exec_time > ${this.slowQueryThreshold}
        ORDER BY mean_exec_time DESC
        LIMIT ${limit}
      `);

      return slowQueries.map((sq: any) => ({
        query: sq.query,
        duration: sq.duration,
        recommendations: this.generateSlowQueryRecommendations(sq.query, sq.hit_percent)
      }));

    } catch (error) {
      logger.error('Failed to get slow queries', {
        error: error instanceof Error ? error.message : error
      });
      return [];
    }
  }

  /**
   * Generate recommendations based on query plan
   */
  private generateRecommendations(plan: any): string[] {
    const recommendations: string[] = [];

    if (!plan) return recommendations;

    // Check for sequential scans on large tables
    if (plan['Node Type'] === 'Seq Scan' && plan['Actual Rows'] > 10000) {
      recommendations.push('Consider adding an index to avoid sequential scan');
    }

    // Check for high cost queries
    if (plan['Total Cost'] > 10000) {
      recommendations.push('Query cost is high, consider optimization');
    }

    // Check for sort operations
    if (plan['Node Type']?.includes('Sort')) {
      recommendations.push('Consider adding an index to avoid sort operation');
    }

    // Check for nested loops
    if (plan['Node Type'] === 'Nested Loop' && plan['Actual Rows'] > 1000) {
      recommendations.push('Consider query join optimization');
    }

    // Check for hash joins without work_mem
    if (plan['Node Type'] === 'Hash Join' && plan['Hash Buckets'] > 100000) {
      recommendations.push('Consider increasing work_mem for hash operations');
    }

    return recommendations;
  }

  /**
   * Extract table name from query
   */
  private extractTableName(query: string): string {
    const match = query.match(/FROM\s+(\w+)/i);
    return match ? match[1] : 'unknown';
  }

  /**
   * Extract operation type from query
   */
  private extractOperationType(query: string): string {
    if (query.toUpperCase().startsWith('SELECT')) return 'select';
    if (query.toUpperCase().startsWith('INSERT')) return 'insert';
    if (query.toUpperCase().startsWith('UPDATE')) return 'update';
    if (query.toUpperCase().startsWith('DELETE')) return 'delete';
    return 'unknown';
  }

  /**
   * Extract indexes used from plan
   */
  private extractIndexesUsed(plan: any): string[] {
    if (!plan) return [];
    // This would parse the plan to extract index usage
    return [];
  }

  /**
   * Extract missing indexes from plan
   */
  private extractMissingIndexes(plan: any): string[] {
    if (!plan) return [];
    // This would parse the plan to identify missing indexes
    return [];
  }

  /**
   * Calculate index benefit score
   */
  private calculateIndexBenefit(nDistinct: number, correlation: number): number {
    // Simple heuristic based on distinctness and correlation
    return Math.min(100, (nDistinct / 1000) * 100 * (1 - Math.abs(correlation)));
  }

  /**
   * Generate recommendations for slow queries
   */
  private generateSlowQueryRecommendations(query: string, hitPercent: number): string[] {
    const recommendations: string[] = [];

    if (hitPercent < 90) {
      recommendations.push('Low cache hit rate - consider optimizing queries or increasing memory');
    }

    if (query.includes('SELECT *')) {
      recommendations.push('Avoid SELECT * - specify only needed columns');
    }

    if (query.toUpperCase().includes(' WHERE ') && !query.toUpperCase().includes(' INDEX ')) {
      recommendations.push('Ensure proper indexes exist for WHERE clause conditions');
    }

    if (query.toUpperCase().includes(' ORDER BY ') && !query.toUpperCase().includes(' LIMIT ')) {
      recommendations.push('Consider adding LIMIT clause for ORDER BY queries');
    }

    return recommendations;
  }

  /**
   * Clear query cache
   */
  clearCache(): void {
    this.queryCache.clear();
    logger.info('Query analyzer cache cleared');
  }

  /**
   * Get cache statistics
   */
  getCacheStats() {
    return {
      size: this.queryCache.size,
      hitRate: 0 // Would need to track hits/misses
    };
  }
}

// Export singleton instance
export const queryAnalyzer = new QueryAnalyzer();

export default queryAnalyzer;