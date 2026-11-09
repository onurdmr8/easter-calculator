package com.eastercalculator.app

import android.app.Application
import android.util.Log
import com.eastercalculator.app.ads.AdManager

class EasterCalculatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("EasterCalculator", "Kritik Hata: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // AdMob SDK Başlatma
        AdManager.initialize(this)
    }
}
