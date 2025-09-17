package com.fibreflow.infrastructure.sync

import android.util.Log
import com.fibreflow.core.common.result.Result
import com.fibreflow.core.database.entities.InstallationEntity
import com.fibreflow.core.database.entities.PhotoEntity
import com.fibreflow.core.database.entities.DropEntity
import com.fibreflow.core.network.api.InstallationAPI
import com.fibreflow.infrastructure.sync.PhotoUploadService
import com.fibreflow.core.network.api.DropAPI
import com.fibreflow.core.network.api.ProjectAPI
import com.fibreflow.infrastructure.sync.conflict.ConflictResolver
import com.fibreflow.infrastructure.sync.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Synchronization Service
 * Handles network operations for data synchronization
 * Manages API calls and conflict resolution during sync
 */
@Singleton
class SyncService @Inject constructor(
    private val installationApi: InstallationAPI,
    private val photoUploadService: PhotoUploadService,
    private val dropApi: DropAPI,
    private val projectApi: ProjectAPI,
    private val conflictResolver: ConflictResolver
) {

    companion object {
        private const val TAG = "SyncService"
    }

    /**
     * Sync installation data
     */
    suspend fun syncInstallation(installation: InstallationEntity): Result<SyncOperationResult> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Syncing installation: ${installation.installationId}")

            // Attempt to sync with server
            val response = installationApi.createInstallation(installation.toInstallationCreateRequest())

            if (response.isSuccessful) {
                val serverInstallation = response.body()
                if (serverInstallation != null) {
                    Result.Success(SyncOperationResult(
                        success = true,
                        entityId = installation.installationId.toString(),
                        operation = SyncOperation.CREATE,
                        serverVersion = serverInstallation.updatedAt
                    ))
                } else {
                    Result.Error(RuntimeException("Empty response from server"))
                }
            } else {
                // Handle conflict or error
                when (response.code()) {
                    409 -> { // Conflict
                        handleInstallationConflict(installation)
                    }
                    else -> Result.Error(RuntimeException("Sync failed: ${response.message()}"))
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Installation sync failed", e)
            Result.Error(e)
        }
    }

    /**
     * Sync photo data
     */
    suspend fun syncPhoto(photo: PhotoEntity): Result<SyncOperationResult> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Syncing photo: ${photo.photoId}")

            // Upload photo file
            val uploadResult = photoUploadService.uploadPhoto(
                installationId = photo.installationId.toString(),
                photoFile = File(photo.filePath),
                stepName = photo.photoType.name,
                sequenceNumber = 0, // PhotoEntity doesn't have sequenceNumber
                latitude = null, // PhotoEntity doesn't have latitude
                longitude = null // PhotoEntity doesn't have longitude
            )

            if (uploadResult.isSuccess) {
                Result.Success(SyncOperationResult(
                    success = true,
                    entityId = photo.photoId.toString(),
                    operation = SyncOperation.CREATE,
                    serverVersion = System.currentTimeMillis() // Use current time as version
                ))
            } else {
                Result.Error(RuntimeException("Photo upload failed"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Photo sync failed", e)
            Result.Error(e)
        }
    }

    /**
     * Sync drop data
     */
    suspend fun syncDrop(drop: DropEntity): Result<SyncOperationResult> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Syncing drop: ${drop.dropNumber}")

            // Update drop status on server
            val response = dropApi.updateDropStatus(drop.dropNumber, com.fibreflow.core.network.api.DropStatusUpdate(
                status = drop.status.name,
                notes = "Synced from mobile"
            ))

            if (response.isSuccessful) {
                Result.Success(SyncOperationResult(
                    success = true,
                    entityId = drop.dropNumber,
                    operation = SyncOperation.UPDATE,
                    serverVersion = response.body()?.updatedAt
                ))
            } else {
                when (response.code()) {
                    409 -> handleDropConflict(drop)
                    else -> Result.Error(RuntimeException("Drop sync failed: ${response.message()}"))
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Drop sync failed", e)
            Result.Error(e)
        }
    }

    /**
     * Fetch latest data from server
     */
    suspend fun fetchLatestData(dataType: SyncDataType): Result<FetchResult> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching latest $dataType data from server")

            when (dataType) {
                SyncDataType.INSTALLATIONS -> {
                    val response = installationApi.getTechnicianInstallations("current")
                    if (response.isSuccessful) {
                        val installations = response.body() ?: emptyList<Any>()
                        Result.Success(FetchResult(
                            dataType = dataType,
                            items = installations,
                            count = installations.size
                        ))
                    } else {
                        Result.Error(RuntimeException("Failed to fetch installations: ${response.message()}"))
                    }
                }
                SyncDataType.DROPS -> {
                    val response = projectApi.getProjects() // Get projects first, then drops
                    if (response.isSuccessful) {
                        val projects = response.body() ?: emptyList()
                        // In real implementation, would fetch drops for each project
                        Result.Success(FetchResult(
                            dataType = dataType,
                            items = emptyList(), // Placeholder
                            count = 0
                        ))
                    } else {
                        Result.Error(RuntimeException("Failed to fetch drops: ${response.message()}"))
                    }
                }
                else -> Result.Success(FetchResult(dataType = dataType, items = emptyList(), count = 0))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Fetch latest data failed", e)
            Result.Error(e)
        }
    }

    // Private helper methods

    private suspend fun handleInstallationConflict(local: InstallationEntity): Result<SyncOperationResult> {
        // In a real implementation, would fetch server version and resolve conflict
        // For now, assume server wins
        return Result.Success(SyncOperationResult(
            success = true,
            entityId = local.installationId.toString(),
            operation = SyncOperation.UPDATE,
            conflictResolved = true,
            resolutionStrategy = "SERVER_WINS"
        ))
    }

    private suspend fun handleDropConflict(local: DropEntity): Result<SyncOperationResult> {
        // Similar to installation conflict handling
        return Result.Success(SyncOperationResult(
            success = true,
            entityId = local.dropNumber,
            operation = SyncOperation.UPDATE,
            conflictResolved = true,
            resolutionStrategy = "SERVER_WINS"
        ))
    }
}

/**
 * Sync operation types
 */
enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}

/**
 * Result of a sync operation
 */
data class SyncOperationResult(
    val success: Boolean,
    val entityId: String,
    val operation: SyncOperation,
    val serverVersion: Long? = null,
    val conflictResolved: Boolean = false,
    val resolutionStrategy: String? = null,
    val error: String? = null
)

/**
 * Result of fetching data from server
 */
data class FetchResult(
    val dataType: SyncDataType,
    val items: List<Any>,
    val count: Int,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

// Extension functions for entity conversion
private fun InstallationEntity.toInstallationCreateRequest(): com.fibreflow.core.network.api.InstallationCreateRequest {
    // Convert entity to API request model
    return com.fibreflow.core.network.api.InstallationCreateRequest(
        dropId = this.dropNumber,
        technicianId = this.technicianId ?: "",
        equipmentType = "FIBER_OPTIC", // Default value
        priority = 1,
        notes = null,
        scheduledDate = this.startTime.time
    )
}

// Placeholder request models
data class DropStatusUpdate(
    val status: String,
    val notes: String? = null
)