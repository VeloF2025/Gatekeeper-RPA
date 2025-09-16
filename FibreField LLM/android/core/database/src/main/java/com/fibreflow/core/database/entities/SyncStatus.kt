package com.fibreflow.core.database.entities

/**
 * Synchronization status enumeration
 * Shared across all entities that require sync status tracking
 */
enum class SyncStatus {
    PENDING,        // Waiting to be synced
    IN_PROGRESS,    // Currently syncing
    COMPLETED,      // Successfully synced (alias: SYNCED)
    FAILED,         // Sync failed
    CONFLICT        // Sync conflict detected
}