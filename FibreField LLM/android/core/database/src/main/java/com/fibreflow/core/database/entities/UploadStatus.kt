package com.fibreflow.core.database.entities

/**
 * Upload status enumeration
 * Tracks the upload state of photos and files
 */
enum class UploadStatus {
    PENDING,        // Waiting for upload
    UPLOADING,      // Currently uploading
    UPLOADED,       // Successfully uploaded
    FAILED,         // Upload failed
    RETRYING,       // Retrying upload
    CANCELLED       // Upload cancelled
}