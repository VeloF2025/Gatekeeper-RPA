package com.fibreflow.core.database.entities

/**
 * Installation status enumeration
 * Tracks the progress and state of installation workflows
 */
enum class InstallationStatus {
    NOT_STARTED,       // Installation not yet started
    IN_PROGRESS,       // Installation in progress
    AWAITING_APPROVAL, // Waiting for customer approval
    APPROVED,          // Customer approved
    COMPLETED,         // Installation completed successfully
    FAILED,            // Installation failed
    CANCELLED,         // Installation cancelled
    PAUSED,            // Installation paused
    REQUIRES_REWORK    // Installation requires rework
}