package com.fibreflow.tech

import android.app.Application

class FibreFieldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Simple application class without Hilt dependencies
        android.util.Log.d("FibreFieldApp", "Application started successfully")
    }
}