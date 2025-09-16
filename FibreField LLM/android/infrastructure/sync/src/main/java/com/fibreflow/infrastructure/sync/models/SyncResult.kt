package com.fibreflow.infrastructure.sync.models

/**
 * Result of a sync operation
 */
data class SyncResult(
    val success: Boolean,
    val syncedItems: Int,
    val failedItems: Int,
    val conflicts: Int,
    val durationMs: Long,
    val errorMessage: String? = null
)