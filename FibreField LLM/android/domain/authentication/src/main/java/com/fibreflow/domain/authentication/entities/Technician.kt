package com.fibreflow.domain.authentication.entities

import com.fibreflow.core.database.entities.TechnicianRole

/**
 * Domain entity representing a technician user
 */
data class Technician(
    val id: String,
    val username: String,
    val email: String? = null,
    val fullName: String,
    val role: TechnicianRole,
    val isActive: Boolean = true,
    val permissions: List<String> = emptyList()
)