package com.ninjarific.whereiroam.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: TripDatabase
    private lateinit var tripDao: TripDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TripDatabase::class.java).build()
        tripDao = db.tripDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertReadDeleteTrip() = runBlocking {
        val trip = Trip(
            null,
            "title",
            "countryCode",
            "start",
            "end"
        )
        tripDao.insertTrip(trip)

        val trips = tripDao.getAllTrips()
        assertEquals(1, trips.size)

        assertEquals(trip.copy(uid = 1), trips[0])

        tripDao.deleteTrip(trips[0])

        val tripsAfterDelete = tripDao.getAllTrips()
        assertEquals(0, tripsAfterDelete.size)
    }
}