package com.ninjarific.whereiroam.features.newtrip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel

class TripFlowActivity : AppCompatActivity() {

    val viewModel by viewModel<TripFlowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView()

        viewModel.getState().observe(this, Observer { state ->
            when (state) {
                TripFlowState.Close -> TODO()
                TripFlowState.WhereTo -> TODO()
                is TripFlowState.TravelDates -> TODO()
                is TripFlowState.Summary -> TODO()
            }
        })
    }
}