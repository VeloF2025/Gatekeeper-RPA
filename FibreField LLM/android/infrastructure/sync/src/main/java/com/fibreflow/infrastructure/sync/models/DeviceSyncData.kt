package com.fibreflow.infrastructure.sync.models

import com.fibreflow.core.database.entities.DropEntity
import com.fibreflow.core.database.entities.InstallationEntity
import com.fibreflow.core.database.entities.PhotoEntity
import com.fibreflow.core.database.entities.SyncQueueEntity

/**
 * Data structure for device synchronization data
 * Contains all the data that needs to be synchronized between devices
 */
data class DeviceSyncData(
    val deviceId: String,
    val timestamp: Long,
    val drops: List<DropEntity>,
    val installations: List<InstallationEntity>,
    val photos: List<PhotoEntity>,
    val syncQueue: List<SyncQueueEntity>
)