package com.ninjarific.whereiroam.features.newtrip

import androidx.lifecycle.*
import com.ninjarific.whereiroam.database.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import java.util.*

class TripFlowViewModel(private val repository: Repository) : ViewModel() {

    private val flowItems = ArrayList<TripFlowItem>()
    private var title: String = ""
    private var workingItem = WorkingTripFlowItem(null, null, null)
    private val currentPage = MutableLiveData<Screen>().apply { value = Screen.TITLE }

    fun getState(): LiveData<TripFlowState> {
        val mapped = Transformations.map(currentPage) { screen: Screen ->
            when (screen) {
                Screen.TITLE -> TripFlowState.TitleInput(title)
                Screen.COUNTRY -> TripFlowState.WhereTo(workingItem.country)
                Screen.DATES -> TripFlowState.TravelDates(
                    workingItem.country!!,
                    workingItem.startDate,
                    workingItem.endDate
                )
                Screen.SUMMARY -> TripFlowState.Summary(flowItems)
                Screen.COMPLETE -> TripFlowState.Saving
                Screen.CLOSE -> TripFlowState.Close
            }
        }
        return Transformations.distinctUntilChanged(mapped)
    }

    fun goBack() {
        currentPage.postValue(
            when (currentPage.value) {
                Screen.TITLE -> Screen.CLOSE
                Screen.COUNTRY -> {
                    if (flowItems.isEmpty()) {
                        Screen.TITLE
                    } else {
                        Screen.SUMMARY
                    }
                }
                Screen.DATES -> Screen.COUNTRY
                Screen.SUMMARY -> Screen.SUMMARY
                Screen.COMPLETE -> Screen.COMPLETE
                Screen.CLOSE -> Screen.CLOSE
                null -> throw NullPointerException("unexpectedly null screen")
            }
        )
    }

    fun updateTitle(title: String) {
        this.title = title
        currentPage.postValue(Screen.COUNTRY)
    }

    fun updateCountry(country: Country) {
        workingItem = workingItem.copy(country = country)
        currentPage.postValue(Screen.DATES)
    }

    fun updateTimes(start: OffsetDateTime, end: OffsetDateTime?) {
        workingItem = workingItem.copy(startDate = start, endDate = end)
        moveWorkingItemToFlowItems()
        currentPage.postValue(Screen.SUMMARY)
    }

    fun complete() {
        currentPage.postValue(Screen.COMPLETE)
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveTrip(title, flowItems)

            viewModelScope.launch {
                currentPage.postValue(Screen.CLOSE)
            }
        }
    }

    fun addExtraVisit() {
        workingItem = WorkingTripFlowItem( null, null, null)
        currentPage.postValue(Screen.COUNTRY)
    }

    private fun moveWorkingItemToFlowItems() {
        flowItems.add(TripFlowItem(workingItem.country!!, workingItem.startDate!!, workingItem.endDate))
        workingItem = WorkingTripFlowItem( null, null, null)
    }
}

sealed class TripFlowState {
    object Close : TripFlowState()
    object Saving : TripFlowState()
    data class TitleInput(val prefill: String?) : TripFlowState()
    data class WhereTo(val prefill: Country?) : TripFlowState()
    data class TravelDates(
        val country: Country,
        val prefillStart: OffsetDateTime?,
        val prefillEnd: OffsetDateTime?
    ) : TripFlowState()

    data class Summary(val itinerary: List<TripFlowItem>) : TripFlowState()

}

enum class Screen {
    TITLE,
    COUNTRY,
    DATES,
    SUMMARY,
    COMPLETE,
    CLOSE
}

data class WorkingTripFlowItem(
    val country: Country?,
    val startDate: OffsetDateTime?,
    val endDate: OffsetDateTime?
)

data class TripFlowItem(
    val country: Country,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime?
)

data class Country(
    val code: String,
    val name: String
)