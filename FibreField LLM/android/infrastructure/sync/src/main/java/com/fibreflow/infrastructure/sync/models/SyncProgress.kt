package com.fibreflow.infrastructure.sync.models

/**
 * Synchronization progress tracking
 */
data class SyncProgress(
    val totalItems: Int = 0,
    val completedItems: Int = 0,
    val failedItems: Int = 0,
    val currentOperation: String = "",
    val estimatedTimeRemaining: Long = 0
) {
    val progress: Float
        get() = if (totalItems == 0) 0f else completedItems.toFloat() / totalItems.toFloat()
}