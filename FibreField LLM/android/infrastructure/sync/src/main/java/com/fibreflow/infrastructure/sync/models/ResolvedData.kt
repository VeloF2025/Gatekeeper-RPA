package com.fibreflow.infrastructure.sync.models

import com.fibreflow.core.database.entities.DropEntity
import com.fibreflow.core.database.entities.InstallationEntity
import com.fibreflow.core.database.entities.PhotoEntity

/**
 * Data structure containing resolved data after conflict resolution
 */
data class ResolvedData(
    val drops: List<DropEntity>,
    val installations: List<InstallationEntity>,
    val photos: List<PhotoEntity>
)