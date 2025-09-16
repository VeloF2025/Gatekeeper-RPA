package com.fibreflow.infrastructure.sync.models

/**
 * Types of conflicts that can occur during synchronization
 */
enum class SyncConflictType {
    DATA_MISMATCH,     // Data differs between devices
    CHANGE_CONFLICT,   // Both devices modified same data
    DELETION_CONFLICT  // One device deleted what another modified
}