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

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE technicianId = :technicianId AND isActive = 1 ORDER BY loginTime DESC LIMIT 1")
    suspend fun getActiveSessionForTechnician(technicianId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE technicianId = :technicianId ORDER BY loginTime DESC")
    suspend fun getSessionsForTechnician(technicianId: String): List<SessionEntity>

    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY loginTime DESC")
    suspend fun getActiveSessions(): List<SessionEntity>

    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY loginTime DESC")
    fun getActiveSessionsFlow(): Flow<List<SessionEntity>>

    @Query("UPDATE sessions SET isActive = 0, logoutTime = :logoutTime WHERE technicianId = :technicianId AND isActive = 1")
    suspend fun deactivateSession(technicianId: String, logoutTime: Long = System.currentTimeMillis())

    @Query("UPDATE sessions SET isActive = 0, logoutTime = :logoutTime")
    suspend fun deactivateAllSessions(logoutTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM sessions WHERE logoutTime IS NOT NULL AND logoutTime < :cutoffDate")
    suspend fun deleteOldSessions(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM sessions WHERE isActive = 1")
    suspend fun getActiveSessionCount(): Int

    @Query("SELECT COUNT(*) FROM sessions WHERE technicianId = :technicianId AND isActive = 1")
    suspend fun isTechnicianActive(technicianId: String): Int

    @Query("SELECT * FROM sessions WHERE loginTime BETWEEN :startTime AND :endTime ORDER BY loginTime DESC")
    suspend fun getSessionsInTimeRange(startTime: Long, endTime: Long): List<SessionEntity>

    @Query("SELECT AVG(logoutTime - loginTime) FROM sessions WHERE logoutTime IS NOT NULL AND isActive = 0")
    suspend fun getAverageSessionDuration(): Long?

    @Query("SELECT technicianId, COUNT(*) as sessionCount FROM sessions GROUP BY technicianId ORDER BY sessionCount DESC")
    suspend fun getSessionCountByTechnician(): Map<String, Int>
}