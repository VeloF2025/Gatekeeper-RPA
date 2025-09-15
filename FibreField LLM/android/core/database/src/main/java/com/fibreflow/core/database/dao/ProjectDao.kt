package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Project operations
 * Handles all database operations related to fiber installation projects
 */
@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>): List<Long>

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT * FROM projects WHERE project_id = :projectId")
    suspend fun getProjectById(projectId: Int): ProjectEntity?

    @Query("SELECT * FROM projects WHERE project_id = :projectId")
    fun getProjectByIdFlow(projectId: Int): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects ORDER BY created_at DESC")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects ORDER BY created_at DESC")
    fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE active = 1 ORDER BY created_at DESC")
    suspend fun getActiveProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE active = 1 ORDER BY created_at DESC")
    fun getActiveProjectsFlow(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE project_name LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchProjects(query: String): List<ProjectEntity>

    @Query("UPDATE projects SET active = :active, updated_at = :timestamp WHERE project_id = :projectId")
    suspend fun updateProjectActiveStatus(projectId: Int, active: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET completed_drops = :completedDrops, active_drops = :activeDrops, failed_drops = :failedDrops, updated_at = :timestamp WHERE project_id = :projectId")
    suspend fun updateProjectStatistics(
        projectId: Int,
        completedDrops: Int,
        activeDrops: Int,
        failedDrops: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM projects WHERE active = 1")
    suspend fun getActiveProjectCount(): Int

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getTotalProjectCount(): Int

    @Query("SELECT SUM(total_drops) FROM projects WHERE active = 1")
    suspend fun getTotalActiveDrops(): Int

    @Query("SELECT SUM(completed_drops) FROM projects WHERE active = 1")
    suspend fun getTotalCompletedDrops(): Int

    @Query("SELECT * FROM projects WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    suspend fun getProjectsInDateRange(startDate: Long, endDate: Long): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE completed_drops * 100.0 / total_drops >= :minPercentage ORDER BY completed_drops * 100.0 / total_drops DESC")
    suspend fun getProjectsByCompletionPercentage(minPercentage: Float): List<ProjectEntity>

    @Query("SELECT AVG(completed_drops * 100.0 / total_drops) FROM projects WHERE total_drops > 0")
    suspend fun getAverageCompletionPercentage(): Float?

    @Query("UPDATE projects SET metadata = :metadata, updated_at = :timestamp WHERE project_id = :projectId")
    suspend fun updateProjectMetadata(projectId: Int, metadata: String?, timestamp: Long = System.currentTimeMillis())
}