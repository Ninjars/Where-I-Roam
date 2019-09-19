package com.ninjarific.whereiroam.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import com.ninjarific.whereiroam.features.newtrip.TripFlowItem
import java.util.*

class Repository(context: Context) {

    private val database =
        Room.databaseBuilder(context, TripDatabase::class.java, "TripDatabase").build()

    @WorkerThread
    suspend fun saveTrip(
        title: String,
        flowItems: ArrayList<TripFlowItem>
    ) {
        if (flowItems.isEmpty()) return

        val sortedItems = flowItems.sortedBy { it.startDate }

        val trip = TripEntry(title = title)
        val tripId = database.tripEntryDao().insert(trip)

        val visitDetails = sortedItems.map {
            VisitEntry(
                trip = tripId,
                countryCode = it.country.code,
                start = it.startDate,
                end = it.endDate
            )
        }
        database.visitDetailsDao().insertAll(visitDetails)
    }
}