package com.fibreflow.infrastructure.offline

import android.content.Context
import com.fibreflow.infrastructure.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt Module for Offline Infrastructure Components
 *
 * Provides singleton instances for offline operation management,
 * network state monitoring, and offline-to-online synchronization.
 */
@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {

    @Provides
    @Singleton
    fun provideOfflineManager(
        @ApplicationContext context: Context,
        syncManager: SyncManager,
        offlineToOnlineSync: com.fibreflow.infrastructure.sync.OfflineToOnlineSync
    ): OfflineManager {
        return OfflineManager(context, syncManager, offlineToOnlineSync)
    }

    @Provides
    @Singleton
    fun provideOfflineToOnlineSync(): com.fibreflow.infrastructure.offline.OfflineToOnlineSync {
        return com.fibreflow.infrastructure.offline.OfflineToOnlineSync()
    }
}