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
    private lateinit var visitDao: VisitDetailsDao
    private lateinit var tripDetailsDao: TripDetailsDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TripDatabase::class.java).build()
        tripDao = db.tripDao()
        visitDao = db.visitDetailsDao()
        tripDetailsDao = db.tripDetailsDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertReadUpdateDeleteTrip() = runBlocking {
        val trip = Trip(
            0,
            "title",
            "countryCode",
            0,
            10
        )
        val tripId = tripDao.insert(trip)
        val trips = tripDao.getAll()
        assertEquals(1, trips.size)
        assertEquals(trip.copy(uid = tripId), trips[0])

        val updatedTrip = trips[0].copy(title="updatedTitle")
        tripDao.update(updatedTrip)
        val updatedTrips = tripDao.getAll()
        assertEquals(1, updatedTrips.size)
        assertEquals(updatedTrip, updatedTrips[0])

        tripDao.delete(updatedTrip)
        val tripsAfterDelete = tripDao.getAll()
        assertEquals(0, tripsAfterDelete.size)
    }

    @Test
    fun insertReadUpdateDeleteVisitDetails() = runBlocking {
        val trip = Trip(
            0,
            "title",
            "countryCode",
            0,
            10
        )
        val tripId = tripDao.insert(trip)

        val visit = VisitDetails(
            0,
            tripId,
            "visitCountry",
            1,
            2
        )
        val visitId = visitDao.insert(visit)

        val visits = visitDao.getVisits(tripId)
        assertEquals(1, visits.size)
        assertEquals(visit.copy(uid = visitId), visits[0])

        val updatedVisit = visits[0].copy(countryCode="updatedCountryCode")
        visitDao.update(updatedVisit)
        val updatedVisits = visitDao.getVisits(tripId)
        assertEquals(1, updatedVisits.size)
        assertEquals(updatedVisit, updatedVisits[0])

        visitDao.delete(updatedVisit)
        val visitsAfterDelete = visitDao.getVisits(tripId)
        assertEquals(0, visitsAfterDelete.size)

        // verify trip unaffected
        val trips = tripDao.getAll()
        assertEquals(1, trips.size)
        assertEquals(trip.copy(uid = tripId), trips[0])
    }

    @Test
    fun tripDetails() = runBlocking {
        val tripId = 1L
        val trip = Trip(
            tripId,
            "title",
            "countryCode",
            0,
            10
        )
        tripDao.insert(trip)
        val visit = VisitDetails(
            1,
            tripId,
            "visitCountry",
            1,
            2
        )
        visitDao.insert(visit)

        val tripDetails = tripDetailsDao.get(tripId)
        assertEquals(trip.copy(uid=tripId), tripDetails.trip)
        assertEquals(visit.copy(uid=1), tripDetails.visitDetails[0])
    }
}