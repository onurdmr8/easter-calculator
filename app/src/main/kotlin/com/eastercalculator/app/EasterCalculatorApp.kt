package com.eastercalculator.app

import android.app.Application
import android.util.Log
import com.eastercalculator.app.ads.AdManager
import com.google.firebase.FirebaseApp

class EasterCalculatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("EasterCalculator", "Kritik Hata: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // Firebase Başlatma
        try {
            FirebaseApp.initializeApp(this)
            Log.d("EasterCalculator", "Firebase SDK başarıyla başlatıldı.")
        } catch (e: Exception) {
            Log.w("EasterCalculator", "Firebase başlatma notu: ${e.message}")
        }

        // AdMob SDK Başlatma
        AdManager.initialize(this)
    }
}
