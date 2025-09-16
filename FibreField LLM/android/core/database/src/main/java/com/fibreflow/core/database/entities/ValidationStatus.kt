package com.fibreflow.core.database.entities

/**
 * Validation status enumeration
 * Tracks the validation state of photos and other entities
 */
enum class ValidationStatus {
    PENDING,        // Waiting for validation
    IN_PROGRESS,    // Validation in progress
    PASSED,         // Validation passed
    FAILED,         // Validation failed
    MANUAL_REVIEW,   // Requires manual review
    APPROVED,       // Manually approved
    REJECTED        // Manually rejected
}