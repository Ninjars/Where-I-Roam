package com.ninjarific.whereiroam

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class RoamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@RoamApplication)
            modules(roamModule)
        }

        Timber.i("app created")
    }
}