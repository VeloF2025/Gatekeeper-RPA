package com.fibreflow.core.database.entities

/**
 * Drop status enumeration used in database entities
 * Mirrors the domain DropStatus enum to avoid circular dependencies
 */
enum class DropStatus {
    AVAILABLE,
    ASSIGNED,
    IN_PROGRESS,
    PENDING_VALIDATION,
    COMPLETED,
    FAILED,
    CANCELLED
}