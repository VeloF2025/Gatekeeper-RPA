package com.fibreflow.core.database.entities

/**
 * Remediation severity enumeration
 * Defines the severity levels of remediation issues
 */
enum class RemediationSeverity {
    LOW,      // Low priority issues
    MEDIUM,   // Medium priority issues
    HIGH,     // High priority issues
    CRITICAL  // Critical issues requiring immediate attention
}