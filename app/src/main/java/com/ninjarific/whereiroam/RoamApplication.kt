package com.ninjarific.whereiroam

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RoamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RoamApplication)
            modules(roamModule)
        }
    }
}