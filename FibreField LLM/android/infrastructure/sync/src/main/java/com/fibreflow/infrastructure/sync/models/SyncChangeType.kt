package com.fibreflow.infrastructure.sync.models

/**
 * Types of changes that can occur during synchronization
 */
enum class SyncChangeType {
    INSERT,    // New data added
    UPDATE,    // Existing data modified
    DELETE     // Data removed
}