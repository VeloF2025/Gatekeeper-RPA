package com.fibreflow.infrastructure.sync.models

import com.fibreflow.core.database.entities.SyncOperation

/**
 * Result of a sync operation
 * Contains information about the success and outcome of the sync
 */
data class SyncOperationResult(
    val success: Boolean,
    val entityId: String,
    val operation: SyncOperation,
    val serverVersion: Long?,
    val errorMessage: String? = null,
    val conflictResolved: Boolean = false
)