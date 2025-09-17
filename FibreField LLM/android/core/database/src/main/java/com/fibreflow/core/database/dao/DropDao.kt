package com.fibreflow.core.database.dao

import androidx.room.*
import androidx.room.Transaction
import com.fibreflow.core.database.entities.DropEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Drop operations
 * Handles all database operations related to fibre optic drops
 */
@Dao
interface DropDao {

    /**
     * Insert a new drop
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrop(drop: DropEntity): Long

    /**
     * Insert multiple drops
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrops(drops: List<DropEntity>): List<Long>

    /**
     * Update an existing drop
     */
    @Update
    suspend fun updateDrop(drop: DropEntity)

    /**
     * Update multiple drops
     */
    @Update
    suspend fun updateDrops(drops: List<DropEntity>)

    /**
     * Delete a drop
     */
    @Delete
    suspend fun deleteDrop(drop: DropEntity)

    /**
     * Delete multiple drops
     */
    @Delete
    suspend fun deleteDrops(drops: List<DropEntity>)

    /**
     * Get drop by ID
     */
    @Query("SELECT * FROM drops WHERE drop_number = :dropId")
    suspend fun getDropById(dropId: String): DropEntity?

    /**
     * Get drop by ID as Flow
     */
    @Query("SELECT * FROM drops WHERE drop_number = :dropId")
    fun getDropByIdFlow(dropId: String): Flow<DropEntity?>

    /**
     * Get all drops
     */
    @Query("SELECT * FROM drops ORDER BY created_at DESC")
    suspend fun getAllDrops(): List<DropEntity>

    /**
     * Get all drops as Flow
     */
    @Query("SELECT * FROM drops ORDER BY created_at DESC")
    fun getAllDropsFlow(): Flow<List<DropEntity>>

    /**
     * Get drops by status
     */
    @Query("SELECT * FROM drops WHERE status = :status ORDER BY updated_at DESC")
    suspend fun getDropsByStatus(status: String): List<DropEntity>

    /**
     * Get drops by status as Flow
     */
    @Query("SELECT * FROM drops WHERE status = :status ORDER BY updated_at DESC")
    fun getDropsByStatusFlow(status: String): Flow<List<DropEntity>>

    /**
     * Get available drops (not assigned or in progress)
     */
    @Query("SELECT * FROM drops WHERE status IN ('AVAILABLE', 'PENDING') ORDER BY priority DESC, created_at ASC")
    suspend fun getAvailableDrops(): List<DropEntity>

    /**
     * Get available drops as Flow
     */
    @Query("SELECT * FROM drops WHERE status IN ('AVAILABLE', 'PENDING') ORDER BY priority DESC, created_at ASC")
    fun getAvailableDropsFlow(): Flow<List<DropEntity>>

    /**
     * Get drops within proximity (simplified - would use spatial queries in real implementation)
     */
    @Query("SELECT * FROM drops WHERE status IN ('AVAILABLE', 'PENDING') AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng ORDER BY priority DESC")
    suspend fun getDropsInProximity(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<DropEntity>

    /**
     * Update drop status
     */
    @Query("UPDATE drops SET status = :status, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun updateDropStatus(dropId: String, status: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Assign drop to technician
     */
    @Query("UPDATE drops SET assigned_technician_id = :technicianId, status = 'IN_PROGRESS', assigned_at = :timestamp, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun assignDropToTechnician(dropId: String, technicianId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Unassign drop from technician
     */
    @Query("UPDATE drops SET assigned_technician_id = NULL, status = 'AVAILABLE', assigned_at = NULL, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun unassignDrop(dropId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Mark drop as completed
     */
    @Query("UPDATE drops SET status = 'COMPLETED', completed_at = :timestamp, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun markDropCompleted(dropId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Update drop location
     */
    @Query("UPDATE drops SET latitude = :latitude, longitude = :longitude, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun updateDropLocation(dropId: String, latitude: Double, longitude: Double, timestamp: Long = System.currentTimeMillis())

    /**
     * Update drop priority
     */
    @Query("UPDATE drops SET priority = :priority, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun updateDropPriority(dropId: String, priority: Int, timestamp: Long = System.currentTimeMillis())

    /**
     * Get drops assigned to technician
     */
    @Query("SELECT * FROM drops WHERE assigned_technician_id = :technicianId ORDER BY priority DESC, assigned_at ASC")
    suspend fun getDropsAssignedToTechnician(technicianId: String): List<DropEntity>

    /**
     * Get drops assigned to technician as Flow
     */
    @Query("SELECT * FROM drops WHERE assigned_technician_id = :technicianId ORDER BY priority DESC, assigned_at ASC")
    fun getDropsAssignedToTechnicianFlow(technicianId: String): Flow<List<DropEntity>>

    /**
     * Get drop count by status
     */
    @Query("SELECT COUNT(*) FROM drops WHERE status = :status")
    suspend fun getDropCountByStatus(status: String): Int

    /**
     * Get total drop count
     */
    @Query("SELECT COUNT(*) FROM drops")
    suspend fun getTotalDropCount(): Int

    /**
     * Mark drop for sync
     */
    @Query("UPDATE drops SET needs_sync = 1, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun markDropForSync(dropId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Mark drop as synced
     */
    @Query("UPDATE drops SET needs_sync = 0, last_sync_attempt = :timestamp WHERE drop_number = :dropId")
    suspend fun markDropSynced(dropId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Get drops needing sync
     */
    @Query("SELECT * FROM drops WHERE needs_sync = 1 ORDER BY updated_at ASC")
    suspend fun getDropsNeedingSync(): List<DropEntity>

    /**
     * Search drops by address
     */
    @Query("SELECT * FROM drops WHERE address LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchDropsByAddress(query: String): List<DropEntity>

    /**
     * Get drops by priority level
     */
    @Query("SELECT * FROM drops WHERE priority >= :minPriority ORDER BY priority DESC, created_at ASC")
    suspend fun getDropsByMinPriority(minPriority: Int): List<DropEntity>

    /**
     * Get overdue drops (assigned but not completed within expected time)
     */
    @Query("SELECT * FROM drops WHERE status = 'IN_PROGRESS' AND assigned_at < :cutoffTime ORDER BY assigned_at ASC")
    suspend fun getOverdueDrops(cutoffTime: Long): List<DropEntity>

    /**
     * Update drop notes
     */
    @Query("UPDATE drops SET notes = :notes, updated_at = :timestamp WHERE drop_number = :dropId")
    suspend fun updateDropNotes(dropId: String, notes: String?, timestamp: Long = System.currentTimeMillis())

    /**
     * Get drops created within date range
     */
    @Query("SELECT * FROM drops WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    suspend fun getDropsInDateRange(startDate: Long, endDate: Long): List<DropEntity>

    /**
     * Bulk update sync status
     */
    @Query("UPDATE drops SET needs_sync = 0, last_sync_attempt = :timestamp WHERE drop_number IN (:dropIds)")
    suspend fun markDropsSynced(dropIds: List<String>, timestamp: Long = System.currentTimeMillis())

    /**
     * Get drop statistics
     */
    @Query("SELECT COUNT(*) FROM drops WHERE status = 'AVAILABLE'")
    suspend fun getAvailableDropCount(): Int

    /**
     * Get average completion time by priority
     */
    @Query("SELECT AVG(completed_at - assigned_at) FROM drops WHERE status = 'COMPLETED' AND assigned_at IS NOT NULL AND completed_at IS NOT NULL")
    suspend fun getAverageCompletionTime(): Long?

    /**
     * Get drops with invalid status values
     */
    @Query("SELECT * FROM drops WHERE status NOT IN ('AVAILABLE', 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'FAILED', 'REMEDIATION_REQUIRED', 'BLOCKED')")
    suspend fun getDropsWithInvalidStatus(): List<DropEntity>

    /**
     * Check if drop exists
     */
    @Query("SELECT COUNT(*) FROM drops WHERE drop_number = :dropNumber")
    suspend fun dropExists(dropNumber: String): Boolean

    /**
     * Delete drop by number
     */
    @Query("DELETE FROM drops WHERE drop_number = :dropNumber")
    suspend fun deleteDropByNumber(dropNumber: String): Int

    /**
     * Insert or update drop (convenience method)
     */
    @Transaction
    suspend fun insertOrUpdateDrop(drop: DropEntity) {
        val existing = getDropById(drop.dropNumber)
        if (existing != null) {
            updateDrop(drop)
        } else {
            insertDrop(drop)
        }
    }

    /**
     * Get duplicate drops (same drop number)
     */
    @Query("SELECT drop_number, COUNT(*) as count, GROUP_CONCAT(drop_number) as ids FROM drops GROUP BY drop_number HAVING COUNT(*) > 1")
    suspend fun getDuplicateDrops(): List<DropDuplicateResult>
}

/**
 * Result class for duplicate drop query
 */
data class DropDuplicateResult(
    @ColumnInfo(name = "drop_number")
    val dropNumber: String,
    val count: Int,
    val ids: String
)