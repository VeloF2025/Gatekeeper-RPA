package com.fibreflow.infrastructure.sync.models

/**
 * Result of fetching data from server
 */
data class FetchResult(
    val success: Boolean,
    val dataFetched: Boolean,
    val itemsCount: Int,
    val lastSyncTimestamp: Long?,
    val errorMessage: String? = null
)