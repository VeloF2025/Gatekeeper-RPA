package com.fibreflow.core.authentication.repositories

import com.fibreflow.core.common.result.Result
import com.fibreflow.core.database.dao.SessionDao
import com.fibreflow.core.database.dao.TechnicianDao
import com.fibreflow.core.network.api.AuthenticationAPI
import com.fibreflow.domain.authentication.entities.AuthToken
import com.fibreflow.domain.authentication.entities.BiometricCredentials
import com.fibreflow.domain.authentication.entities.Technician
import com.fibreflow.domain.authentication.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository that handles authentication data operations
 * using network API, local database, and token management
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authAPI: AuthenticationAPI,
    private val technicianDao: TechnicianDao,
    private val sessionDao: SessionDao,
    private val tokenManager: com.fibreflow.domain.authentication.TokenManager,
    private val biometricManager: com.fibreflow.domain.authentication.BiometricManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthToken> {
        return try {
            val response = authAPI.login(com.fibreflow.core.network.models.request.LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    val tokens = AuthToken(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken,
                        expiresAt = System.currentTimeMillis() + (authResponse.expiresIn * 1000)
                    )
                    Result.Success(tokens)
                } ?: Result.Error(RuntimeException("Invalid response from server"))
            } else {
                Result.Error(RuntimeException("Login failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshToken(): Result<AuthToken> {
        return try {
            val currentTokens = tokenManager.getStoredTokens()
            if (currentTokens == null) {
                return Result.Error(RuntimeException("No tokens to refresh"))
            }

            val response = authAPI.refreshToken(currentTokens.refreshToken)
            if (response.isSuccessful) {
                response.body()?.let { refreshResponse ->
                    val newTokens = AuthToken(
                        accessToken = refreshResponse.accessToken,
                        refreshToken = refreshResponse.refreshToken ?: currentTokens.refreshToken,
                        expiresAt = System.currentTimeMillis() + (refreshResponse.expiresIn * 1000)
                    )
                    Result.Success(newTokens)
                } ?: Result.Error(RuntimeException("Invalid refresh response"))
            } else {
                Result.Error(RuntimeException("Token refresh failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            tokenManager.clearTokens()
            biometricManager.clearStoredCredentials()
            sessionDao.deactivateAllSessions()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun biometricLogin(biometricToken: String): Result<AuthToken> {
        // Biometric login typically uses a different flow, but for now, we'll handle it via token validation
        val tokens = tokenManager.getStoredTokens()
        return if (tokens != null && tokenManager.isTokenValid(tokens)) {
            Result.Success(tokens)
        } else {
            Result.Error(RuntimeException("Biometric login failed - tokens invalid or expired"))
        }
    }

    override suspend fun getCurrentUser(): Result<Technician> {
        return try {
            val tokens = tokenManager.getStoredTokens()
            if (tokens == null) {
                return Result.Error(RuntimeException("No authenticated user"))
            }

            val technicianId = tokenManager.getTechnicianIdFromToken(tokens.accessToken)
            if (technicianId == null) {
                return Result.Error(RuntimeException("Unable to extract technician ID from token"))
            }

            val technician = technicianDao.getTechnicianById(technicianId)
            technician?.let { Result.Success(it) } ?: Result.Error(RuntimeException("Technician not found"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveTechnician(technician: Technician): Result<Unit> {
        return try {
            technicianDao.insertTechnician(technician)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTechnicianById(technicianId: String): Result<Technician> {
        return try {
            val technician = technicianDao.getTechnicianById(technicianId)
            technician?.let { Result.Success(it) } ?: Result.Error(RuntimeException("Technician not found"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveTokens(token: AuthToken): Result<Unit> {
        return try {
            tokenManager.storeTokens(token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAccessToken(): Result<String> {
        return try {
            val tokens = tokenManager.getStoredTokens()
            tokens?.accessToken?.let { Result.Success(it) } ?: Result.Error(RuntimeException("No access token"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getRefreshToken(): Result<String> {
        return try {
            val tokens = tokenManager.getStoredTokens()
            tokens?.refreshToken?.let { Result.Success(it) } ?: Result.Error(RuntimeException("No refresh token"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearTokens(): Result<Unit> {
        return try {
            tokenManager.clearTokens()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        val tokens = tokenManager.getStoredTokens()
        return tokens != null && tokenManager.isTokenValid(tokens)
    }

    override fun getAuthState(): Flow<Boolean> {
        return sessionDao.getActiveSessionsFlow().map { it.isNotEmpty() }
    }

    override suspend fun saveBiometricCredentials(credentials: BiometricCredentials): Result<Unit> {
        return try {
            biometricManager.storeCredentials(credentials)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBiometricCredentials(userId: String): Result<BiometricCredentials> {
        return try {
            val credentials = biometricManager.getStoredCredentials()
            credentials?.let { Result.Success(it) } ?: Result.Error(RuntimeException("No biometric credentials"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun isBiometricAvailable(): Boolean {
        return biometricManager.isBiometricAvailable()
    }

    override suspend fun enableBiometric(): Result<Unit> {
        // This would typically involve additional setup, but for now, we'll just check availability
        return if (isBiometricAvailable()) {
            Result.Success(Unit)
        } else {
            Result.Error(RuntimeException("Biometric authentication not available"))
        }
    }

    override suspend fun disableBiometric(): Result<Unit> {
        return try {
            biometricManager.clearStoredCredentials()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun validateTokens(): Boolean {
        val tokens = tokenManager.getStoredTokens()
        return tokens != null && tokenManager.isTokenValid(tokens)
    }
}