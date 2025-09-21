package com.fibreflow.tech

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
// import com.google.firebase.FirebaseApp // TEMPORARILY DISABLED
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
 * - Dependency injection with Hilt
 * - Basic logging
 * - Firebase services
 * - WorkManager configuration
 */
@HiltAndroidApp
class FibreFieldApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Initialize basic systems
        initializeLogging()
        // initializeFirebase() // TEMPORARILY DISABLED
        setupCrashHandling()
    }

    /**
     * Initialize Timber for logging
     */
    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        Timber.d("FibreField Application initialized")
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
            // Continue without Firebase if initialization fails
        }
    }

    /**
     * Setup basic crash handling
     */
    private fun setupCrashHandling() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "Uncaught exception on thread ${thread.name}")
            // Basic crash handling - can be extended later
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }

    /**
     * Custom Timber tree for basic error reporting
     */
    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                // Basic error logging - can be extended with crash reporting service later
                Log.e(tag ?: "FibreField", message, t)
            }
        }
    }

    companion object {
        private const val TAG = "FibreFieldApplication"
    }
}