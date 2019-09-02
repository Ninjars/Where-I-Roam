package com.ninjarific.whereiroam.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: TripDatabase
    private lateinit var tripEntryDao: TripEntryDao
    private lateinit var visitEntryDao: VisitEntryDao
    private lateinit var tripVisitsDao: TripVisitsDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TripDatabase::class.java).build()
        tripEntryDao = db.tripEntryDao()
        visitEntryDao = db.visitDetailsDao()
        tripVisitsDao = db.tripVisitsDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertReadUpdateDeleteTrip() = runBlocking {
        val trip = TripEntry(0, TITLE)
        val tripId = tripEntryDao.insert(trip)
        val trips = tripEntryDao.getAll()
        assertEquals(1, trips.size)
        assertEquals(trip.copy(uid = tripId), trips[0])

        val updatedTrip = trips[0].copy(title="updatedTitle")
        tripEntryDao.update(updatedTrip)
        val updatedTrips = tripEntryDao.getAll()
        assertEquals(1, updatedTrips.size)
        assertEquals(updatedTrip, updatedTrips[0])

        tripEntryDao.delete(updatedTrip)
        val tripsAfterDelete = tripEntryDao.getAll()
        assertEquals(0, tripsAfterDelete.size)
    }

    @Test
    fun insertReadUpdateDeleteVisitDetails() = runBlocking {
        val trip = TripEntry(0, TITLE)
        val tripId = tripEntryDao.insert(trip)

        val visit = VisitEntry(
            0,
            tripId,
            "visitCountry",
            DATE_START,
            DATE_END
        )
        visitEntryDao.insertAll(listOf(visit))

        val visits = visitEntryDao.getVisits(tripId)
        assertEquals(1, visits.size)
        assertEquals(visit.copy(uid = visits[0].uid), visits[0])

        val updatedVisit = visits[0].copy(countryCode="updatedCountryCode")
        visitEntryDao.update(updatedVisit)
        val updatedVisits = visitEntryDao.getVisits(tripId)
        assertEquals(1, updatedVisits.size)
        assertEquals(updatedVisit, updatedVisits[0])

        visitEntryDao.delete(updatedVisit)
        val visitsAfterDelete = visitEntryDao.getVisits(tripId)
        assertEquals(0, visitsAfterDelete.size)

        // verify tripEntry unaffected
        val trips = tripEntryDao.getAll()
        assertEquals(1, trips.size)
        assertEquals(trip.copy(uid = tripId), trips[0])
    }

    @Test
    fun tripDetails() = runBlocking {
        val tripId = 1L
        val trip = TripEntry(tripId, TITLE)
        tripEntryDao.insert(trip)
        val visit = VisitEntry(
            1,
            tripId,
            "visitCountry",
            DATE_START,
            DATE_END
        )
        visitEntryDao.insertAll(listOf(visit))

        val tripDetails = tripVisitsDao.get(tripId)
        assertEquals(trip.copy(uid=tripId), tripDetails.tripEntry)
        assertEquals(visit.copy(uid=1), tripDetails.visitDetails[0])
    }

    @Test
    fun tripVisits() = runBlocking {
        val tripId = 1L
        val trip = TripEntry(tripId, TITLE)
        tripEntryDao.insert(trip)

        val tripEnd = OffsetDateTime.parse("2010-01-20T00:00:00+00:00")
        val visits = listOf(
            VisitEntry(
                1,
                tripId,
                "visitCountry",
                DATE_START,
                DATE_END
            ),
            VisitEntry(
                2,
                tripId,
                "visitCountryTwo",
                DATE_END,
                tripEnd
            )
        )
        visitEntryDao.insertAll(visits)

        val tripVisits = tripVisitsDao.get(tripId)
        val recalledTrip = tripVisits.tripEntry!!
        val recalledVisits = tripVisits.visitDetails

        assertEquals(trip, recalledTrip)
        assertEquals(visits, recalledVisits)
    }

    companion object {
        private const val TITLE = "title"
        private val DATE_START = OffsetDateTime.parse("2010-01-01T00:00:00+00:00")
        private val DATE_END = OffsetDateTime.parse("2010-01-10T00:00:00+00:00")
    }
}