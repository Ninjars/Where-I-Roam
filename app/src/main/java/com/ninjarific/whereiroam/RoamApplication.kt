package com.ninjarific.whereiroam

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class RoamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        AndroidThreeTen.init(this)

        startKoin {
            androidContext(this@RoamApplication)
            modules(applicationModule)
        }

        Timber.i("app created")
    }
}