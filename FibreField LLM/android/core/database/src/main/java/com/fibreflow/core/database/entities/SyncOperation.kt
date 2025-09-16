package com.fibreflow.core.database.entities

/**
 * Sync operation enumeration
 * Defines the types of operations that can be performed during synchronization
 */
enum class SyncOperation {
    CREATE,     // Create new entity
    UPDATE,     // Update existing entity
    DELETE,     // Delete entity
    SYNC,       // Sync entity data
    BULK_CREATE,// Bulk create multiple entities
    BULK_UPDATE,// Bulk update multiple entities
    BULK_DELETE // Bulk delete multiple entities
}