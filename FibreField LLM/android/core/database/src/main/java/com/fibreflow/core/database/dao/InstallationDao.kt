package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.InstallationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Installation operations
 * Handles all database operations related to fibre optic installations
 */
@Dao
interface InstallationDao {

    /**
     * Insert a new installation
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallation(installation: InstallationEntity): Long

    /**
     * Insert multiple installations
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallations(installations: List<InstallationEntity>): List<Long>

    /**
     * Update an existing installation
     */
    @Update
    suspend fun updateInstallation(installation: InstallationEntity)

    /**
     * Update multiple installations
     */
    @Update
    suspend fun updateInstallations(installations: List<InstallationEntity>)

    /**
     * Delete an installation
     */
    @Delete
    suspend fun deleteInstallation(installation: InstallationEntity)

    /**
     * Delete multiple installations
     */
    @Delete
    suspend fun deleteInstallations(installations: List<InstallationEntity>)

    /**
     * Get installation by ID
     */
    @Query("SELECT * FROM installations WHERE installation_id = :installationId")
    suspend fun getInstallationById(installationId: Long): InstallationEntity?

    /**
     * Get installation by ID as Flow for reactive updates
     */
    @Query("SELECT * FROM installations WHERE installation_id = :installationId")
    fun getInstallationByIdFlow(installationId: Long): Flow<InstallationEntity?>

    /**
     * Get all installations for a technician
     */
    @Query("SELECT * FROM installations WHERE technician_id = :technicianId ORDER BY created_at DESC")
    suspend fun getInstallationsByTechnician(technicianId: String): List<InstallationEntity>

    /**
     * Get all installations for a technician as Flow
     */
    @Query("SELECT * FROM installations WHERE technician_id = :technicianId ORDER BY created_at DESC")
    fun getInstallationsByTechnicianFlow(technicianId: String): Flow<List<InstallationEntity>>

    /**
     * Get installations by drop ID
     */
    @Query("SELECT * FROM installations WHERE drop_number = :dropNumber ORDER BY created_at DESC")
    suspend fun getInstallationsByDrop(dropNumber: String): List<InstallationEntity>

    /**
     * Get installations by status
     */
    @Query("SELECT * FROM installations WHERE status = :status ORDER BY updated_at DESC")
    suspend fun getInstallationsByStatus(status: String): List<InstallationEntity>

    /**
     * Get installations by status as Flow
     */
    @Query("SELECT * FROM installations WHERE status = :status ORDER BY updated_at DESC")
    fun getInstallationsByStatusFlow(status: String): Flow<List<InstallationEntity>>

    /**
     * Get pending installations (not completed)
     */
    @Query("SELECT * FROM installations WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY created_at ASC")
    suspend fun getPendingInstallations(): List<InstallationEntity>

    /**
     * Get pending installations as Flow
     */
    @Query("SELECT * FROM installations WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY created_at ASC")
    fun getPendingInstallationsFlow(): Flow<List<InstallationEntity>>

    /**
     * Get completed installations within date range
     */
    @Query("SELECT * FROM installations WHERE status = 'COMPLETED' AND end_time BETWEEN :startDate AND :endDate ORDER BY end_time DESC")
    suspend fun getCompletedInstallationsInRange(startDate: Long, endDate: Long): List<InstallationEntity>

    /**
     * Get installations requiring sync
     */
    @Query("SELECT * FROM installations WHERE ai_guidance_used = 1 ORDER BY updated_at ASC")
    suspend fun getInstallationsNeedingSync(): List<InstallationEntity>

    /**
     * Mark installation as needing sync
     */
    @Query("UPDATE installations SET ai_guidance_used = 1, updated_at = :timestamp WHERE installation_id = :installationId")
    suspend fun markInstallationForSync(installationId: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Mark installation as synced
     */
    @Query("UPDATE installations SET ai_guidance_used = 0, updated_at = :timestamp WHERE installation_id = :installationId")
    suspend fun markInstallationSynced(installationId: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Update installation status
     */
    @Query("UPDATE installations SET status = :status, updated_at = :timestamp WHERE installation_id = :installationId")
    suspend fun updateInstallationStatus(installationId: Long, status: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Update installation progress
     */
    @Query("UPDATE installations SET current_step = :currentStep, updated_at = :timestamp WHERE installation_id = :installationId")
    suspend fun updateInstallationProgress(installationId: Long, currentStep: Int, timestamp: Long = System.currentTimeMillis())

    /**
     * Mark installation as completed
     */
    @Query("UPDATE installations SET status = 'COMPLETED', end_time = :timestamp, updated_at = :timestamp WHERE installation_id = :installationId")
    suspend fun markInstallationCompleted(installationId: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Get installation count by status
     */
    @Query("SELECT COUNT(*) FROM installations WHERE status = :status")
    suspend fun getInstallationCountByStatus(status: String): Int

    /**
     * Get total installation count
     */
    @Query("SELECT COUNT(*) FROM installations")
    suspend fun getTotalInstallationCount(): Int

    /**
     * Get installations created within date range
     */
    @Query("SELECT * FROM installations WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    suspend fun getInstallationsInDateRange(startDate: Long, endDate: Long): List<InstallationEntity>

    /**
     * Delete old completed installations (cleanup)
     */
    @Query("DELETE FROM installations WHERE status = 'COMPLETED' AND end_time < :cutoffDate")
    suspend fun deleteOldCompletedInstallations(cutoffDate: Long): Int

    /**
     * Search installations by equipment type
     */
    @Query("SELECT * FROM installations WHERE ont_serial LIKE '%' || :ontSerial || '%' ORDER BY created_at DESC")
    suspend fun searchInstallationsByEquipment(ontSerial: String): List<InstallationEntity>

    /**
     * Get installations with validation issues
     */
    @Query("SELECT * FROM installations WHERE validation_errors IS NOT NULL ORDER BY updated_at DESC")
    suspend fun getInstallationsWithValidationIssues(): List<InstallationEntity>

    /**
     * Update validation issues flag
     */
    @Query("UPDATE installations SET validation_errors = :validationErrors, updated_at = :timestamp WHERE installation_id = :installationId")
    suspend fun updateValidationIssuesFlag(installationId: Long, validationErrors: String?, timestamp: Long = System.currentTimeMillis())

    /**
     * Get average installation time by equipment type
     */
    @Query("SELECT AVG(end_time - start_time) FROM installations WHERE status = 'COMPLETED' AND end_time IS NOT NULL")
    suspend fun getAverageInstallationTime(): Long?

    /**
     * Bulk update sync status
     */
    @Query("UPDATE installations SET ai_guidance_used = 0, updated_at = :timestamp WHERE installation_id IN (:installationIds)")
    suspend fun markInstallationsSynced(installationIds: List<Long>, timestamp: Long = System.currentTimeMillis())

    /**
     * Get orphaned installations (installations referencing non-existent drops)
     */
    @Query("SELECT i.* FROM installations i LEFT JOIN drops d ON i.drop_number = d.drop_number WHERE d.drop_number IS NULL")
    suspend fun getOrphanedInstallations(): List<InstallationEntity>

    /**
     * Get records with future timestamps
     */
    @Query("SELECT * FROM installations WHERE start_time > :currentTime OR end_time > :currentTime")
    suspend fun getRecordsWithFutureTimestamps(currentTime: Long = System.currentTimeMillis()): List<InstallationEntity>

    /**
     * Check if installation exists
     */
    @Query("SELECT COUNT(*) FROM installations WHERE installation_id = :installationId")
    suspend fun installationExists(installationId: Long): Boolean

    /**
     * Delete installation by drop number
     */
    @Query("DELETE FROM installations WHERE drop_number = :dropNumber")
    suspend fun deleteInstallationByDropNumber(dropNumber: String): Int

    /**
     * Get all installations
     */
    @Query("SELECT * FROM installations ORDER BY created_at DESC")
    suspend fun getAllInstallations(): List<InstallationEntity>
}