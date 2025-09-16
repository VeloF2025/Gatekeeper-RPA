package com.fibreflow.infrastructure.sync.models

/**
 * Synchronization statistics
 */
data class SyncStatistics(
    val lastSyncTime: Long,
    val totalItemsSynced: Int,
    val pendingItems: Int,
    val failedItems: Int,
    val averageSyncTimeMs: Long
)