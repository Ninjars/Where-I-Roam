package com.ninjarific.whereiroam.features.newtrip

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ninjarific.whereiroam.R
import kotlinx.android.synthetic.main.activity_trip_flow.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class TripFlowActivity : AppCompatActivity() {

    private val viewModel by viewModel<TripFlowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_flow)

        viewModel.getState().observe(this, Observer { state ->
            Timber.i(state.toString())
            when (state) {
//                TripFlowState.Close -> TODO()
                is TripFlowState.TitleInput -> askForTitle(state)
                is TripFlowState.WhereTo -> showCountryCodePicker(state)
//                is TripFlowState.TravelDates -> TODO()
//                is TripFlowState.Summary -> TODO()
            }
        })
    }

    private fun showCountryCodePicker(state: TripFlowState.WhereTo) {
        tripTitleInput.visibility = View.GONE
        codePicker.visibility = View.VISIBLE

        state.prefill?.let {
            codePicker.setDefaultCountryUsingNameCode(it.code)
            // TODO: might need to also call resetToDefaultCountry
        }

        codePicker.setOnCountryChangeListener {
            Timber.i(codePicker.selectedCountryName)
        }

        confirmButton.setOnClickListener {
            viewModel.updateCountry(Country(codePicker.selectedCountryNameCode, codePicker.selectedCountryName))
        }
    }

    private fun askForTitle(state: TripFlowState.TitleInput) {
        tripTitleInput.editableText.clear()

        state.prefill?.let { tripTitleInput.editableText.insert(0, it) }

        tripTitleInput.visibility = View.VISIBLE
        codePicker.visibility = View.GONE

        confirmButton.setOnClickListener {
            viewModel.updateTitle(tripTitleInput.editableText.toString())
        }
    }
}