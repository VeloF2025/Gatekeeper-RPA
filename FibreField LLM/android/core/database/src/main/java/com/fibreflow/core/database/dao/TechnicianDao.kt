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

    @Query("SELECT * FROM technicians WHERE id = :technicianId")
    suspend fun getTechnicianById(technicianId: String): TechnicianEntity?

    @Query("SELECT * FROM technicians WHERE id = :technicianId")
    fun getTechnicianByIdFlow(technicianId: String): Flow<TechnicianEntity?>

    @Query("SELECT * FROM technicians WHERE email = :email")
    suspend fun getTechnicianByEmail(email: String): TechnicianEntity?

    @Query("SELECT * FROM technicians ORDER BY name ASC")
    suspend fun getAllTechnicians(): List<TechnicianEntity>

    @Query("SELECT * FROM technicians ORDER BY name ASC")
    fun getAllTechniciansFlow(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getActiveTechnicians(): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveTechniciansFlow(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE role = :role ORDER BY name ASC")
    suspend fun getTechniciansByRole(role: String): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE role = :role ORDER BY name ASC")
    fun getTechniciansByRoleFlow(role: String): Flow<List<TechnicianEntity>>

    @Query("UPDATE technicians SET isActive = :isActive WHERE id = :technicianId")
    suspend fun updateTechnicianActiveStatus(technicianId: String, isActive: Boolean)

    @Query("UPDATE technicians SET lastLoginAt = :timestamp WHERE id = :technicianId")
    suspend fun updateLastLoginTime(technicianId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE technicians SET lastSyncAt = :timestamp WHERE id = :technicianId")
    suspend fun updateLastSyncTime(technicianId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM technicians WHERE isActive = 1")
    suspend fun getActiveTechnicianCount(): Int

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun getTotalTechnicianCount(): Int

    @Query("SELECT COUNT(*) FROM technicians WHERE role = :role")
    suspend fun getTechnicianCountByRole(role: String): Int

    @Query("DELETE FROM technicians WHERE isActive = 0 AND lastLoginAt < :cutoffDate")
    suspend fun deleteInactiveTechnicians(cutoffDate: Long): Int

    @Query("SELECT * FROM technicians WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchTechnicians(query: String): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE lastLoginAt BETWEEN :startTime AND :endTime ORDER BY lastLoginAt DESC")
    suspend fun getTechniciansByLastLoginRange(startTime: Long, endTime: Long): List<TechnicianEntity>

    @Query("SELECT AVG(System.currentTimeMillis() - lastLoginAt) FROM technicians WHERE isActive = 1 AND lastLoginAt IS NOT NULL")
    suspend fun getAverageTimeSinceLastLogin(): Long?

    @Query("SELECT role, COUNT(*) as count FROM technicians GROUP BY role ORDER BY count DESC")
    suspend fun getTechnicianCountByRole(): Map<String, Int>
}