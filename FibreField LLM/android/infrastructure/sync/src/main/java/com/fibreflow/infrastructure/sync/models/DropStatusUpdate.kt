package com.fibreflow.infrastructure.sync.models

/**
 * Request model for updating drop status
 */
data class DropStatusUpdate(
    val status: String,
    val notes: String? = null,
    val technicianId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)