package com.fibreflow.core.database.entities

/**
 * Remediation status enumeration
 * Tracks the status of remediation tasks
 */
enum class RemediationStatus {
    PENDING,      // Waiting to be addressed
    IN_PROGRESS,  // Currently being worked on
    RESOLVED,     // Issue has been resolved
    CANCELLED     // Remediation cancelled
}