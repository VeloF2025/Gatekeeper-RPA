package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Sync Queue operations
 * Handles all database operations related to offline data synchronization
 */
@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItems(items: List<SyncQueueEntity>): List<Long>

    @Update
    suspend fun updateSyncItem(item: SyncQueueEntity)

    @Delete
    suspend fun deleteSyncItem(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE sync_id = :itemId")
    suspend fun getSyncItemById(itemId: Long): SyncQueueEntity?

    @Query("SELECT * FROM sync_queue WHERE sync_status = 'PENDING' ORDER BY created_at ASC")
    suspend fun getPendingSyncItems(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue WHERE sync_status = 'PENDING' ORDER BY created_at ASC")
    fun getPendingSyncItemsFlow(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM sync_queue WHERE sync_status = 'FAILED' ORDER BY retry_count ASC, created_at ASC")
    suspend fun getFailedSyncItems(): List<SyncQueueEntity>

    @Query("UPDATE sync_queue SET sync_status = :status, updated_at = :timestamp WHERE sync_id = :itemId")
    suspend fun updateSyncStatus(itemId: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE sync_queue SET retry_count = retry_count + 1, last_attempt = :timestamp, updated_at = :timestamp WHERE sync_id = :itemId")
    suspend fun incrementRetryCount(itemId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM sync_queue WHERE sync_status = 'COMPLETED' AND updated_at < :cutoffDate")
    suspend fun deleteCompletedItems(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM sync_queue WHERE sync_status = :status")
    suspend fun getSyncItemCountByStatus(status: String): Int
}