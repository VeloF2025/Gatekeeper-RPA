package com.fibreflow.core.authentication

import com.fibreflow.core.authentication.repositories.AuthRepositoryImpl
import com.fibreflow.core.database.dao.SessionDao
import com.fibreflow.core.database.dao.TechnicianDao
import com.fibreflow.core.network.api.AuthenticationAPI
import com.fibreflow.domain.authentication.BiometricManager
import com.fibreflow.domain.authentication.TokenManager
import com.fibreflow.domain.authentication.repositories.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for authentication dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authAPI: AuthenticationAPI,
        technicianDao: TechnicianDao,
        sessionDao: SessionDao,
        tokenManager: TokenManager,
        biometricManager: BiometricManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            authAPI = authAPI,
            technicianDao = technicianDao,
            sessionDao = sessionDao,
            tokenManager = tokenManager,
            biometricManager = biometricManager
        )
    }
}