package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.RemediationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Remediation operations
 * Handles all database operations related to installation remediation tasks
 */
@Dao
interface RemediationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemediation(remediation: RemediationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemediations(remediations: List<RemediationEntity>): List<Long>

    @Update
    suspend fun updateRemediation(remediation: RemediationEntity)

    @Delete
    suspend fun deleteRemediation(remediation: RemediationEntity)

    @Query("SELECT * FROM remediations WHERE remediation_id = :remediationId")
    suspend fun getRemediationById(remediationId: Long): RemediationEntity?

    @Query("SELECT * FROM remediations WHERE remediation_id = :remediationId")
    fun getRemediationByIdFlow(remediationId: Long): Flow<RemediationEntity?>

    @Query("SELECT * FROM remediations WHERE installation_id = :installationId ORDER BY created_at DESC")
    suspend fun getRemediationsByInstallation(installationId: Long): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE installation_id = :installationId ORDER BY created_at DESC")
    fun getRemediationsByInstallationFlow(installationId: Long): Flow<List<RemediationEntity>>

    @Query("SELECT * FROM remediations WHERE status = :status ORDER BY created_at DESC")
    suspend fun getRemediationsByStatus(status: String): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE status = :status ORDER BY created_at DESC")
    fun getRemediationsByStatusFlow(status: String): Flow<List<RemediationEntity>>

    @Query("SELECT * FROM remediations WHERE assigned_to = :technicianId ORDER BY created_at DESC")
    suspend fun getRemediationsByTechnician(technicianId: String): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE assigned_to = :technicianId ORDER BY created_at DESC")
    fun getRemediationsByTechnicianFlow(technicianId: String): Flow<List<RemediationEntity>>

    @Query("SELECT * FROM remediations WHERE severity = :severity ORDER BY created_at DESC")
    suspend fun getRemediationsBySeverity(severity: String): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE issue_type = :issueType ORDER BY created_at DESC")
    suspend fun getRemediationsByType(issueType: String): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE status = 'PENDING' ORDER BY severity DESC, created_at ASC")
    suspend fun getPendingRemediations(): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE status = 'PENDING' ORDER BY severity DESC, created_at ASC")
    fun getPendingRemediationsFlow(): Flow<List<RemediationEntity>>

    @Query("SELECT * FROM remediations WHERE status = 'IN_PROGRESS' ORDER BY created_at ASC")
    suspend fun getInProgressRemediations(): List<RemediationEntity>

    @Query("SELECT * FROM remediations WHERE status = 'IN_PROGRESS' ORDER BY created_at ASC")
    fun getInProgressRemediationsFlow(): Flow<List<RemediationEntity>>

    @Query("UPDATE remediations SET status = :status, updated_at = :timestamp WHERE remediation_id = :remediationId")
    suspend fun updateRemediationStatus(remediationId: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE remediations SET assigned_to = :technicianId, updated_at = :timestamp WHERE remediation_id = :remediationId")
    suspend fun assignRemediation(remediationId: Long, technicianId: String?, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE remediations SET status = 'RESOLVED', resolution_notes = :notes, resolved_at = :timestamp, updated_at = :timestamp WHERE remediation_id = :remediationId")
    suspend fun resolveRemediation(
        remediationId: Long,
        notes: String?,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM remediations WHERE status = :status")
    suspend fun getRemediationCountByStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM remediations WHERE severity = :severity")
    suspend fun getRemediationCountBySeverity(severity: String): Int

    @Query("SELECT COUNT(*) FROM remediations")
    suspend fun getTotalRemediationCount(): Int

    @Query("SELECT * FROM remediations WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    suspend fun getRemediationsInDateRange(startDate: Long, endDate: Long): List<RemediationEntity>

    @Query("SELECT AVG(resolved_at - created_at) FROM remediations WHERE status = 'RESOLVED' AND resolved_at IS NOT NULL")
    suspend fun getAverageResolutionTime(): Long?

    @Query("SELECT * FROM remediations WHERE description LIKE '%' || :query || '%' OR resolution_notes LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchRemediations(query: String): List<RemediationEntity>

    @Query("DELETE FROM remediations WHERE status = 'RESOLVED' AND resolved_at < :cutoffDate")
    suspend fun deleteOldResolvedRemediations(cutoffDate: Long): Int
}