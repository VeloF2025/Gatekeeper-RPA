package com.fibreflow.core.common

import android.graphics.Bitmap
import android.graphics.Color
import com.fibreflow.core.database.entities.DropEntity
import com.fibreflow.core.database.entities.DropStatus
import com.fibreflow.core.network.models.request.LoginRequest
import com.fibreflow.core.network.models.response.AuthResponse
import com.fibreflow.core.network.models.response.UserInfo
import java.util.Date

/**
 * Test utilities for creating test data and common test operations
 */
object TestUtils {

    // Test data generators
    fun createTestDrop(
        dropNumber: String = "DROP-001",
        projectId: Int = 1,
        customerName: String = "Test Customer",
        address: String = "123 Test Street, Cape Town",
        latitude: Double = -33.9249,
        longitude: Double = 18.4241,
        status: DropStatus = DropStatus.AVAILABLE,
        notes: String = "Test drop for unit testing",
        needsSync: Boolean = false,
        createdAt: Date = Date(),
        updatedAt: Date = Date()
    ): DropEntity {
        return DropEntity(
            dropNumber = dropNumber,
            projectId = projectId,
            latitude = latitude,
            longitude = longitude,
            address = address,
            status = status,
            customerName = customerName,
            notes = notes,
            needsSync = needsSync,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun createTestAuthResponse(
        accessToken: String = "test-access-token-123",
        refreshToken: String = "test-refresh-token-456",
        expiresIn: Long = 3600L,
        tokenType: String = "Bearer",
        user: UserInfo = createTestUserInfo()
    ): AuthResponse {
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            tokenType = tokenType,
            user = user
        )
    }

    fun createTestUserInfo(
        id: String = "user-123",
        username: String = "testuser",
        email: String = "test@example.com",
        fullName: String = "Test User",
        role: String = "technician",
        isActive: Boolean = true
    ): UserInfo {
        return UserInfo(
            id = id,
            username = username,
            email = email,
            fullName = fullName,
            role = role,
            isActive = isActive
        )
    }

    fun createTestLoginRequest(
        username: String = "testuser",
        password: String = "testpass123"
    ): LoginRequest {
        return LoginRequest(username, password)
    }

    fun createTestBitmap(
        width: Int = 100,
        height: Int = 100,
        color: Int = Color.BLUE
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(color)
        return bitmap
    }

    // Test constants
    const val TEST_USERNAME = "test_technician"
    const val TEST_PASSWORD = "secure_password_123"
    const val TEST_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlc3QgVXNlciIsImlhdCI6MTUxNjIzOTAyMn0"
    const val TEST_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlc3QgUmVmcmVzaCIsImlhdCI6MTUxNjIzOTAyMn0"

    // Test error messages
    const val ERROR_NETWORK = "Network error occurred"
    const val ERROR_AUTH = "Authentication failed"
    const val ERROR_DATABASE = "Database operation failed"
    const val ERROR_VALIDATION = "Validation failed"

    // Test performance metrics
    fun createTestPerformanceMetrics(): Map<String, Any> {
        return mapOf(
            "inference_count" to 25,
            "average_latency_ms" to 150.5,
            "success_rate" to 0.92,
            "memory_usage_mb" to 45.2,
            "cpu_usage_percent" to 12.8
        )
    }
}