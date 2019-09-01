package com.ninjarific.whereiroam.database

import android.content.Context
import androidx.room.Room

class Repository(context: Context) {
    private val database = Room.databaseBuilder(context, TripDatabase::class.java, "TripDatabase").build()
}