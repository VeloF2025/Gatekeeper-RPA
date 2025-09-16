package com.fibreflow.infrastructure.sync.models

/**
 * Result of device synchronization operation
 */
data class DeviceSyncResult(
    val appliedDrops: Int,
    val appliedInstallations: Int,
    val appliedPhotos: Int,
    val timestamp: Long
)