package com.fibreflow.core.database.entities

/**
 * Remediation type enumeration
 * Defines the types of issues that require remediation
 */
enum class RemediationType {
    HARDWARE_ISSUE,           // Hardware-related issues
    CONFIGURATION_ERROR,      // Configuration problems
    NETWORK_PROBLEM,          // Network connectivity issues
    PHOTO_VALIDATION_FAILED,  // Photo validation failures
    GPS_ACCURACY_ISSUE,      // GPS accuracy problems
    MANUAL_OVERRIDE_NEEDED,   // Requires manual intervention
    OTHER                    // Other types of issues
}