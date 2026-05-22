package com.gamingcontroller.app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GamingControllerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("GamingControllerApp", "Application created")
        } catch (e: Exception) {
            Log.e("GamingControllerApp", "Crash: ${e.message}", e)
        }
    }
}