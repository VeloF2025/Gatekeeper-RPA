package com.fibreflow.infrastructure.sync.models

/**
 * Sync data type enumeration
 * Defines the types of data that can be synchronized
 */
enum class SyncDataType {
    INSTALLATIONS,  // Installation data
    PHOTOS,         // Photo data
    DROPS,          // Drop data
    PROJECTS,       // Project data
    CONFIGURATION,  // Configuration data
    ALL             // All data types
}