package com.fibreflow.infrastructure.sync.models

/**
 * Synchronization state enumeration
 */
enum class SyncState {
    IDLE,       // Not syncing
    SYNCING,    // Currently syncing
    ERROR,      // Sync error occurred
    COMPLETED   // Sync completed successfully
}