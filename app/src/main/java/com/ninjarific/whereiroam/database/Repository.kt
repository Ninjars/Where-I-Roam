package com.ninjarific.whereiroam.database

import android.content.Context
import androidx.room.Room
import com.ninjarific.whereiroam.features.newtrip.Document
import com.ninjarific.whereiroam.features.newtrip.TripFlowItem
import java.util.ArrayList

class Repository(context: Context) {

    private val database = Room.databaseBuilder(context, TripDatabase::class.java, "TripDatabase").build()

    suspend fun saveTrip(flowItems: ArrayList<TripFlowItem>, documents: ArrayList<Document>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}