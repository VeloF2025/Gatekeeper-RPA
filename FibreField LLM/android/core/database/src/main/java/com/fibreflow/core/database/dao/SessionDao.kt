package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Session operations
 * Handles all database operations related to user sessions
 */
@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SessionEntity>): List<Long>

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Delete
    suspend fun deleteSession(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE session_id = :sessionId")
    suspend fun getSessionById(sessionId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE technician_id = :technicianId AND is_active = 1 ORDER BY created_at DESC LIMIT 1")
    suspend fun getActiveSessionForTechnician(technicianId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE technician_id = :technicianId ORDER BY created_at DESC")
    suspend fun getSessionsForTechnician(technicianId: String): List<SessionEntity>

    @Query("SELECT * FROM sessions WHERE is_active = 1 ORDER BY created_at DESC")
    suspend fun getActiveSessions(): List<SessionEntity>

    @Query("SELECT * FROM sessions WHERE is_active = 1 ORDER BY created_at DESC")
    fun getActiveSessionsFlow(): Flow<List<SessionEntity>>

    @Query("UPDATE sessions SET is_active = 0, last_activity = :logoutTime WHERE technician_id = :technicianId AND is_active = 1")
    suspend fun deactivateSession(technicianId: String, logoutTime: Long = System.currentTimeMillis())

    @Query("UPDATE sessions SET is_active = 0, last_activity = :logoutTime")
    suspend fun deactivateAllSessions(logoutTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM sessions WHERE last_activity < :cutoffDate")
    suspend fun deleteOldSessions(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM sessions WHERE is_active = 1")
    suspend fun getActiveSessionCount(): Int

    @Query("SELECT COUNT(*) FROM sessions WHERE technician_id = :technicianId AND is_active = 1")
    suspend fun isTechnicianActive(technicianId: String): Int

    @Query("SELECT * FROM sessions WHERE created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    suspend fun getSessionsInTimeRange(startTime: Long, endTime: Long): List<SessionEntity>

    @Query("SELECT AVG(expires_at - created_at) FROM sessions WHERE is_active = 0")
    suspend fun getAverageSessionDuration(): Long?

    @Query("SELECT technician_id, COUNT(*) as sessionCount FROM sessions GROUP BY technician_id ORDER BY sessionCount DESC")
    suspend fun getSessionCountByTechnician(): Map<String, Int>
}