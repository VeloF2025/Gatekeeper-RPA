package com.fibreflow.infrastructure.sync.models

/**
 * Current synchronization status
 */
data class SyncStatus(
    val deviceId: String,
    val lastSyncTime: Long,
    val pendingChanges: Int,
    val activeConflicts: Int,
    val connectedDevices: Int
)