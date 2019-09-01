package com.ninjarific.whereiroam

import com.ninjarific.whereiroam.database.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val roamModule = module {
    single { Repository(this.androidContext()) }
}