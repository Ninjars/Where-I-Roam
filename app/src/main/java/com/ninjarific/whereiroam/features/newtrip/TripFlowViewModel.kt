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
    private val documents = ArrayList<Document>()
    private var title = ""
    private val workingItem = MutableLiveData<WorkingTripFlowItem>().apply {
        value = WorkingTripFlowItem(null, null, null, null)
    }

    private fun resetWorkingData() {
        flowItems.clear()
        documents.clear() // TODO: delete any working documents that are persisted
        workingItem.value = WorkingTripFlowItem(null, null, null, null)
    }

    fun getState(): LiveData<TripFlowState> {
        return Transformations.map(workingItem) {
            when {
                it.title == null -> TripFlowState.TitleInput(null)
                it.country == null -> TripFlowState.WhereTo(null)
                !it.timesConfirmed -> TripFlowState.TravelDates(it.country, null, null)
                it.finished -> TripFlowState.Close
                else -> TripFlowState.Summary(flowItems, documents, it.saving)
            }
        }
    }

    fun goBack(): Boolean {
        return false
    }

    fun reset() {
        resetWorkingData()
    }

    fun updateTitle(title: String) {
        workingItem.value = workingItem.value?.copy(title = title)
    }

    fun updateCountry(country: Country) {
        workingItem.value = workingItem.value?.copy(country = country)
    }

    fun updateTimes(start: OffsetDateTime, end: OffsetDateTime?) {
        workingItem.value =
            workingItem.value?.copy(startDate = start, endDate = end, timesConfirmed = true)
    }

    fun appendDocuments(documents: List<Document>) {
        this.documents.addAll(documents)
    }

    fun complete() {
        workingItem.value = workingItem.value?.copy(saving = true)
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveTrip(title, flowItems, documents)

            viewModelScope.launch {
                workingItem.value = workingItem.value?.copy(finished = true)
            }
        }
    }
}

sealed class TripFlowState {
    object Close : TripFlowState()
    data class TitleInput(val prefill: String?) : TripFlowState()
    data class WhereTo(val prefill: Country?) : TripFlowState()
    data class TravelDates(
        val country: Country,
        val prefillStart: OffsetDateTime?,
        val prefillEnd: OffsetDateTime?
    ) : TripFlowState()

    data class Summary(
        val itinerary: List<TripFlowItem>,
        val documents: List<Document>,
        val saving: Boolean
    ) : TripFlowState()
}

data class WorkingTripFlowItem(
    val title: String?,
    val country: Country?,
    val startDate: OffsetDateTime?,
    val endDate: OffsetDateTime?,
    val timesConfirmed: Boolean = false,
    val saving: Boolean = false,
    val finished: Boolean = false
)

data class TripFlowItem(
    val country: Country,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime?,
    val documents: List<Document> = emptyList()
)

data class Country(
    val code: String,
    val name: String
)

data class Document(
    val name: String,
    val location: String
)