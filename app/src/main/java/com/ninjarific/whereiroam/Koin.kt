package com.ninjarific.whereiroam

import com.ninjarific.whereiroam.database.Repository
import com.ninjarific.whereiroam.features.newtrip.TripFlowViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    single { Repository(this.androidContext()) }
    viewModel { TripFlowViewModel(get()) }
}