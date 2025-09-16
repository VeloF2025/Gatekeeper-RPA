// 🟢 WORKING: Hilt module for drop dependencies
package com.fibreflow.domain.drops

import com.fibreflow.core.database.dao.DropDao
import com.fibreflow.core.location.ProximityDetector
import com.fibreflow.domain.drops.repositories.DropRepository
import com.fibreflow.domain.drops.repositories.DropRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for drop management dependencies
 * Provides repository and service instances for drop operations
 */
@Module
@InstallIn(SingletonComponent::class)
object DropsModule {

    @Provides
    @Singleton
    fun provideDropRepository(
        dropDao: DropDao,
        proximityDetector: ProximityDetector
    ): DropRepository {
        return DropRepositoryImpl(
            dropDao = dropDao,
            proximityDetector = proximityDetector
        )
    }
}