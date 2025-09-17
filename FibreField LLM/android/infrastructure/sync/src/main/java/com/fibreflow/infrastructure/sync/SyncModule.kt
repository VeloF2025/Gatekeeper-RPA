package com.fibreflow.infrastructure.sync

import android.content.Context
import com.fibreflow.core.database.FibreFieldDatabase
import com.fibreflow.core.database.dao.*
import com.fibreflow.core.network.api.InstallationAPI
import com.fibreflow.infrastructure.sync.PhotoUploadService
import com.fibreflow.core.network.api.DropAPI
import com.fibreflow.core.network.api.ProjectAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt Module for Sync Infrastructure Components
 *
 * Provides singleton instances for offline synchronization, conflict resolution,
 * and multi-device sync capabilities.
 */
@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideOfflineToOnlineSync(
        syncService: SyncService,
        syncManager: SyncManager
    ): OfflineToOnlineSync {
        return OfflineToOnlineSync(syncService, syncManager)
    }

    @Provides
    @Singleton
    fun provideConflictResolver(): com.fibreflow.infrastructure.sync.conflict.ConflictResolver {
        return com.fibreflow.infrastructure.sync.conflict.ConflictResolver()
    }

    @Provides
    @Singleton
    fun provideMultiDeviceSync(
        @ApplicationContext context: Context,
        database: FibreFieldDatabase,
        conflictResolver: ConflictResolver
    ): MultiDeviceSync {
        return MultiDeviceSync(context, database, conflictResolver)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        installationDao: InstallationDao,
        photoDao: PhotoDao,
        dropDao: DropDao,
        syncQueueDao: SyncQueueDao,
        conflictResolver: com.fibreflow.infrastructure.sync.conflict.ConflictResolver
    ): SyncManager {
        return SyncManager(context, installationDao, photoDao, dropDao, syncQueueDao, conflictResolver)
    }

    @Provides
    @Singleton
    fun provideSyncService(
        installationApi: InstallationAPI,
        photoUploadService: PhotoUploadService,
        dropApi: DropAPI,
        projectApi: ProjectAPI,
        conflictResolver: com.fibreflow.infrastructure.sync.conflict.ConflictResolver
    ): SyncService {
        return SyncService(installationApi, photoUploadService, dropApi, projectApi, conflictResolver)
    }
}