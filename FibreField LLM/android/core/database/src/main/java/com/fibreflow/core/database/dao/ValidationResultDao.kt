package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.ValidationResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Validation Result operations
 * Handles all database operations related to AI validation results
 */
@Dao
interface ValidationResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValidationResult(result: ValidationResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValidationResults(results: List<ValidationResultEntity>): List<Long>

    @Update
    suspend fun updateValidationResult(result: ValidationResultEntity)

    @Delete
    suspend fun deleteValidationResult(result: ValidationResultEntity)

    @Query("SELECT * FROM validation_results WHERE validation_id = :resultId")
    suspend fun getValidationResultById(resultId: Long): ValidationResultEntity?

    @Query("SELECT * FROM validation_results WHERE photo_id = :photoId ORDER BY created_at DESC")
    suspend fun getValidationResultsByPhoto(photoId: Long): List<ValidationResultEntity>

    @Query("SELECT * FROM validation_results WHERE photo_id IN (SELECT photo_id FROM photos WHERE installation_id = :installationId) ORDER BY created_at DESC")
    suspend fun getValidationResultsByInstallation(installationId: Long): List<ValidationResultEntity>

    @Query("SELECT * FROM validation_results WHERE is_valid = :isValid ORDER BY created_at DESC")
    suspend fun getValidationResultsByStatus(isValid: Boolean): List<ValidationResultEntity>

    @Query("SELECT COUNT(*) FROM validation_results WHERE is_valid = :isValid")
    suspend fun getValidationResultCountByStatus(isValid: Boolean): Int

    @Query("DELETE FROM validation_results WHERE created_at < :cutoffDate")
    suspend fun deleteOldValidationResults(cutoffDate: Long): Int
}