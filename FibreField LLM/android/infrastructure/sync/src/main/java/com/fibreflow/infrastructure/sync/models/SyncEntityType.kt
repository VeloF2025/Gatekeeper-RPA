package com.fibreflow.infrastructure.sync.models

/**
 * Types of entities that can be synchronized
 */
enum class SyncEntityType {
    DROP,           // Drop entities
    INSTALLATION,   // Installation entities
    PHOTO,          // Photo entities
    PROJECT,        // Project entities
    CONFIGURATION   // Configuration entities
}