package com.fibreflow.infrastructure.sync.models

/**
 * Represents a conflict between local and remote data
 * Used for conflict resolution during synchronization
 */
data class SyncConflict(
    val entityType: SyncEntityType,
    val entityId: String,
    val localData: Any,
    val remoteData: Any,
    val conflictType: SyncConflictType,
    val timestamp: Long
)