package com.fibreflow.core.performance

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt Module for Performance Monitoring Components
 *
 * Provides singleton instances for performance monitoring, battery optimization,
 * memory management, and other performance-related services.
 */
@Module
@InstallIn(SingletonComponent::class)
object PerformanceModule {

    @Provides
    @Singleton
    fun providePerformanceMonitor(
        @ApplicationContext context: Context
    ): PerformanceMonitor {
        return PerformanceMonitor(context)
    }

    @Provides
    @Singleton
    fun provideBatteryOptimizer(
        @ApplicationContext context: Context
    ): BatteryOptimizer {
        return BatteryOptimizer(context)
    }

    @Provides
    @Singleton
    fun provideMemoryManager(
        @ApplicationContext context: Context
    ): MemoryManager {
        return MemoryManager(context)
    }

    @Provides
    @Singleton
    fun providePerformanceTracker(): PerformanceTracker {
        return PerformanceTracker()
    }

    @Provides
    @Singleton
    fun provideOperationalMetrics(): OperationalMetrics {
        return OperationalMetrics()
    }

    @Provides
    @Singleton
    fun provideCameraPerformance(): CameraPerformance {
        return CameraPerformance()
    }

    @Provides
    @Singleton
    fun provideUIPerformanceMonitor(): UIPerformanceMonitor {
        return UIPerformanceMonitor()
    }
}