package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.TechnicianEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Technician operations
 * Handles all database operations related to technicians
 */
@Dao
interface TechnicianDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnician(technician: TechnicianEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnicians(technicians: List<TechnicianEntity>): List<Long>

    @Update
    suspend fun updateTechnician(technician: TechnicianEntity)

    @Delete
    suspend fun deleteTechnician(technician: TechnicianEntity)

    @Query("SELECT * FROM technicians WHERE technician_id = :technicianId")
    suspend fun getTechnicianById(technicianId: String): TechnicianEntity?

    @Query("SELECT * FROM technicians WHERE technician_id = :technicianId")
    fun getTechnicianByIdFlow(technicianId: String): Flow<TechnicianEntity?>

    @Query("SELECT * FROM technicians WHERE email = :email")
    suspend fun getTechnicianByEmail(email: String): TechnicianEntity?

    @Query("SELECT * FROM technicians ORDER BY name ASC")
    suspend fun getAllTechnicians(): List<TechnicianEntity>

    @Query("SELECT * FROM technicians ORDER BY name ASC")
    fun getAllTechniciansFlow(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE active = 1 ORDER BY name ASC")
    suspend fun getActiveTechnicians(): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE active = 1 ORDER BY name ASC")
    fun getActiveTechniciansFlow(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE role = :role ORDER BY name ASC")
    suspend fun getTechniciansByRole(role: String): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE role = :role ORDER BY name ASC")
    fun getTechniciansByRoleFlow(role: String): Flow<List<TechnicianEntity>>

    @Query("UPDATE technicians SET active = :active WHERE technician_id = :technicianId")
    suspend fun updateTechnicianActiveStatus(technicianId: String, active: Boolean)

    @Query("UPDATE technicians SET last_login = :timestamp WHERE technician_id = :technicianId")
    suspend fun updateLastLoginTime(technicianId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE technicians SET last_sync_at = :timestamp WHERE technician_id = :technicianId")
    suspend fun updateLastSyncTime(technicianId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM technicians WHERE active = 1")
    suspend fun getActiveTechnicianCount(): Int

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun getTotalTechnicianCount(): Int

    @Query("SELECT COUNT(*) FROM technicians WHERE role = :role")
    suspend fun getTechnicianCountByRole(role: String): Int

    @Query("DELETE FROM technicians WHERE active = 0 AND last_login < :cutoffDate")
    suspend fun deleteInactiveTechnicians(cutoffDate: Long): Int

    @Query("SELECT * FROM technicians WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchTechnicians(query: String): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE last_login BETWEEN :startTime AND :endTime ORDER BY last_login DESC")
    suspend fun getTechniciansByLastLoginRange(startTime: Long, endTime: Long): List<TechnicianEntity>

    @Query("SELECT AVG(System.currentTimeMillis() - last_login) FROM technicians WHERE active = 1 AND last_login IS NOT NULL")
    suspend fun getAverageTimeSinceLastLogin(): Long?

    @Query("SELECT role, COUNT(*) as count FROM technicians GROUP BY role ORDER BY count DESC")
    suspend fun getTechnicianCountByRole(): Map<String, Int>
}