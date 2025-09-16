package com.fibreflow.tech

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fibreflow.tech.core.ai.ml.PhiModelManager
import com.fibreflow.tech.core.common.CrashHandler
import com.fibreflow.tech.core.common.PerformanceMonitor
import com.fibreflow.tech.core.database.AppDatabase
import com.fibreflow.tech.core.design.theme.ThemeManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * FibreField Tech Application
 *
 * Core application class that initializes:
 * - AI/ML models (Phi-3.5 Mini)
 * - Database and caching
 * - Theme system
 * - Performance monitoring
 * - Crash reporting
 * - Background work configuration
 */
@HiltAndroidApp
class FibreFieldApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    @Inject
    lateinit var phiModelManager: PhiModelManager

    @Inject
    lateinit var crashHandler: CrashHandler

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Initialize core systems
        initializeCoreSystems()

        // Initialize AI/ML systems
        initializeAiMlSystems()

        // Setup performance monitoring
        setupPerformanceMonitoring()

        // Initialize Firebase
        initializeFirebase()

        // Setup crash handling
        setupCrashHandling()

        // Preload critical resources
        preloadCriticalResources()
    }

    /**
     * Initialize core application systems
     */
    private fun initializeCoreSystems() {
        Timber.d("Initializing core systems...")

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        // Initialize theme system
        themeManager.initialize(this)

        // Set up WorkManager
        applicationScope.launch {
            try {
                // Initialize WorkManager with Hilt worker factory
                Configuration.Provider { getWorkManagerConfiguration() }
                Timber.i("WorkManager initialized successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize WorkManager")
            }
        }
    }

    /**
     * Initialize AI and ML systems
     */
    private fun initializeAiMlSystems() {
        Timber.d("Initializing AI/ML systems...")

        applicationScope.launch {
            try {
                // Initialize Phi-3.5 Mini model
                phiModelManager.initialize(this@FibreFieldApplication)
                Timber.i("AI model initialized successfully")

                // Pre-load AI model if memory permits
                if (performanceMonitor.hasSufficientMemory()) {
                    phiModelManager.preloadModel()
                    Timber.i("AI model preloaded successfully")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize AI/ML systems")
                // Continue without AI features if initialization fails
            }
        }
    }

    /**
     * Setup performance monitoring
     */
    private fun setupPerformanceMonitoring() {
        performanceMonitor.initialize(this)

        // Track application startup time
        performanceMonitor.trackStartupTime()

        // Monitor memory usage
        performanceMonitor.startMemoryMonitoring()

        // Monitor battery usage
        performanceMonitor.startBatteryMonitoring()
    }

    /**
     * Initialize Firebase services
     */
    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            Timber.i("Firebase initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Firebase")
        }
    }

    /**
     * Setup crash handling and reporting
     */
    private fun setupCrashHandling() {
        crashHandler.initialize(this)

        // Set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "Uncaught exception on thread ${thread.name}")
            crashHandler.handleUncaughtException(thread, throwable)
        }
    }

    /**
     * Preload critical resources for faster startup
     */
    private fun preloadCriticalResources() {
        applicationScope.launch {
            try {
                // Preload database
                database.openHelper.readableDatabase

                // Preload design system resources
                themeManager.preloadResources()

                Timber.i("Critical resources preloaded successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to preload critical resources")
            }
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }

    /**
     * Custom Timber tree for crash reporting
     */
    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                // Send to crash reporting service
                crashHandler.logError(priority, tag, message, t)
            }
        }
    }

    companion object {
        private const val TAG = "FibreFieldApplication"
    }
}