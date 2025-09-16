package com.fibreflow.infrastructure.sync.models

/**
 * Represents a change that occurred on a device
 * Used for synchronizing changes between multiple devices
 */
data class SyncChange(
    val id: String,
    val type: SyncChangeType,
    val entityType: SyncEntityType,
    val entityId: String,
    val data: Any,
    val timestamp: Long,
    val deviceId: String
)