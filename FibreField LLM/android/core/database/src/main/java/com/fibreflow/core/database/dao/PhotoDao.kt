package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.PhotoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Photo operations
 * Handles all database operations related to installation photos and validation
 */
@Dao
interface PhotoDao {

    /**
     * Insert a new photo
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    /**
     * Insert multiple photos
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>): List<Long>

    /**
     * Update an existing photo
     */
    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    /**
     * Update multiple photos
     */
    @Update
    suspend fun updatePhotos(photos: List<PhotoEntity>)

    /**
     * Delete a photo
     */
    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    /**
     * Delete multiple photos
     */
    @Delete
    suspend fun deletePhotos(photos: List<PhotoEntity>)

    /**
     * Get photo by ID
     */
    @Query("SELECT * FROM photos WHERE photo_id = :photoId")
    suspend fun getPhotoById(photoId: Long): PhotoEntity?

    /**
     * Get photo by ID as Flow
     */
    @Query("SELECT * FROM photos WHERE photo_id = :photoId")
    fun getPhotoByIdFlow(photoId: Long): Flow<PhotoEntity?>

    /**
     * Get all photos for an installation
     */
    @Query("SELECT * FROM photos WHERE installation_id = :installationId ORDER BY created_at ASC")
    suspend fun getPhotosByInstallation(installationId: Long): List<PhotoEntity>

    /**
     * Get all photos for an installation as Flow
     */
    @Query("SELECT * FROM photos WHERE installation_id = :installationId ORDER BY created_at ASC")
    fun getPhotosByInstallationFlow(installationId: Long): Flow<List<PhotoEntity>>

    /**
     * Get photos by step
     */
    @Query("SELECT * FROM photos WHERE installation_id = :installationId AND photo_type = :photoType ORDER BY created_at ASC")
    suspend fun getPhotosByStep(installationId: Long, photoType: String): List<PhotoEntity>

    /**
     * Get photos requiring validation
     */
    @Query("SELECT * FROM photos WHERE validation_status = 'PENDING' ORDER BY created_at ASC")
    suspend fun getPhotosRequiringValidation(): List<PhotoEntity>

    /**
     * Get photos requiring validation as Flow
     */
    @Query("SELECT * FROM photos WHERE validation_status = 'PENDING' ORDER BY created_at ASC")
    fun getPhotosRequiringValidationFlow(): Flow<List<PhotoEntity>>

    /**
     * Get validated photos
     */
    @Query("SELECT * FROM photos WHERE validation_status = 'PASSED' ORDER BY updated_at DESC")
    suspend fun getValidatedPhotos(): List<PhotoEntity>

    /**
     * Get failed validation photos
     */
    @Query("SELECT * FROM photos WHERE validation_status = 'FAILED' ORDER BY updated_at DESC")
    suspend fun getFailedValidationPhotos(): List<PhotoEntity>

    /**
     * Update photo validation status
     */
    @Query("UPDATE photos SET validation_status = :status, updated_at = :timestamp, override_reason = :notes WHERE photo_id = :photoId")
    suspend fun updatePhotoValidationStatus(photoId: Long, status: String, notes: String?, timestamp: Long = System.currentTimeMillis())

    /**
     * Update photo validation results
     */
    @Query("UPDATE photos SET validation_status = :status, updated_at = :timestamp, validation_confidence = :confidence, ai_metadata = :objects, override_reason = :notes WHERE photo_id = :photoId")
    suspend fun updatePhotoValidationResults(
        photoId: Long,
        status: String,
        confidence: Float,
        objects: String?, // JSON string of detected objects
        notes: String?,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Mark photo for sync
     */
    @Query("UPDATE photos SET upload_status = 'PENDING' WHERE photo_id = :photoId")
    suspend fun markPhotoForSync(photoId: Long)

    /**
     * Mark photo as synced
     */
    @Query("UPDATE photos SET upload_status = 'COMPLETED', updated_at = :timestamp WHERE photo_id = :photoId")
    suspend fun markPhotoSynced(photoId: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Get photos needing sync
     */
    @Query("SELECT * FROM photos WHERE upload_status = 'PENDING' ORDER BY created_at ASC")
    suspend fun getPhotosNeedingSync(): List<PhotoEntity>

    /**
     * Update photo file path
     */
    @Query("UPDATE photos SET file_path = :filePath, file_size_bytes = :fileSize WHERE photo_id = :photoId")
    suspend fun updatePhotoFilePath(photoId: Long, filePath: String, fileSize: Long)

    /**
     * Update photo metadata
     */
    @Query("UPDATE photos SET ai_metadata = :locationData WHERE photo_id = :photoId")
    suspend fun updatePhotoLocation(photoId: Long, locationData: String?)

    /**
     * Update photo quality metrics
     */
    @Query("UPDATE photos SET validation_confidence = :qualityScore WHERE photo_id = :photoId")
    suspend fun updatePhotoQualityMetrics(photoId: Long, qualityScore: Float?)

    /**
     * Get photos by quality score range
     */
    @Query("SELECT * FROM photos WHERE validation_confidence BETWEEN :minScore AND :maxScore ORDER BY validation_confidence DESC")
    suspend fun getPhotosByQualityRange(minScore: Float, maxScore: Float): List<PhotoEntity>

    /**
     * Get low quality photos
     */
    @Query("SELECT * FROM photos WHERE validation_confidence < 0.6 ORDER BY validation_confidence ASC")
    suspend fun getLowQualityPhotos(): List<PhotoEntity>

    /**
     * Get photo count by validation status
     */
    @Query("SELECT COUNT(*) FROM photos WHERE validation_status = :status")
    suspend fun getPhotoCountByValidationStatus(status: String): Int

    /**
     * Get total photo count
     */
    @Query("SELECT COUNT(*) FROM photos")
    suspend fun getTotalPhotoCount(): Int

    /**
     * Get photos captured within date range
     */
    @Query("SELECT * FROM photos WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    suspend fun getPhotosInDateRange(startDate: Long, endDate: Long): List<PhotoEntity>

    /**
     * Delete photos older than cutoff date
     */
    @Query("DELETE FROM photos WHERE created_at < :cutoffDate")
    suspend fun deleteOldPhotos(cutoffDate: Long): Int

    /**
     * Get photos by technician
     */
    @Query("SELECT p.* FROM photos p INNER JOIN installations i ON p.installation_id = i.installation_id WHERE i.technician_id = :technicianId ORDER BY p.created_at DESC")
    suspend fun getPhotosByTechnician(technicianId: String): List<PhotoEntity>

    /**
     * Get average quality score by step
     */
    @Query("""
        SELECT photo_type, AVG(validation_confidence) as avgQuality, COUNT(*) as photoCount
        FROM photos
        WHERE validation_confidence IS NOT NULL
        GROUP BY photo_type
        ORDER BY avgQuality DESC
    """)
    suspend fun getAverageQualityByStep(): Map<String, Pair<Float, Int>>

    /**
     * Get validation accuracy statistics
     */
    @Query("""
        SELECT validation_status, COUNT(*) as count,
               AVG(validation_confidence) as avgConfidence
        FROM photos
        WHERE validation_status IS NOT NULL
        GROUP BY validation_status
    """)
    suspend fun getValidationStatistics(): Map<String, Pair<Int, Float>>

    /**
     * Bulk update sync status
     */
    @Query("UPDATE photos SET upload_status = 'COMPLETED', updated_at = :timestamp WHERE photo_id IN (:photoIds)")
    suspend fun markPhotosSynced(photoIds: List<Long>, timestamp: Long = System.currentTimeMillis())

    /**
     * Get photos with detected objects containing specific text
     */
    @Query("SELECT * FROM photos WHERE ai_metadata LIKE '%' || :objectName || '%' ORDER BY created_at DESC")
    suspend fun searchPhotosByDetectedObject(objectName: String): List<PhotoEntity>

    /**
     * Get photos with extracted text containing specific text
     */
    @Query("SELECT * FROM photos WHERE ai_metadata LIKE '%' || :searchText || '%' ORDER BY created_at DESC")
    suspend fun searchPhotosByExtractedText(searchText: String): List<PhotoEntity>
}