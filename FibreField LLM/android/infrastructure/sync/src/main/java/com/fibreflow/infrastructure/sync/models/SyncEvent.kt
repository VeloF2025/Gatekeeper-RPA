package com.fibreflow.infrastructure.sync.models

/**
 * Sealed class for synchronization events
 */
sealed class SyncEvent {
    data class StatusUpdate(val status: SyncStatus) : SyncEvent()
    data class ConflictDetected(val conflict: SyncConflict) : SyncEvent()
    data class SyncCompleted(val result: DeviceSyncResult) : SyncEvent()
    data class Error(val message: String) : SyncEvent()
}