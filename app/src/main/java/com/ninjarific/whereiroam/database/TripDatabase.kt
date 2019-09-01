package com.ninjarific.whereiroam.database

import androidx.room.*

@Database(entities = [Trip::class, VisitDetails::class], version = 1)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="first_country_code") val firstCountryCode: String,
    @ColumnInfo(name="trip_start") val tripStart: String,
    @ColumnInfo(name="trip_end") val tripEnd: String?
)

@Dao
interface TripDao {
    @Query("SELECT * FROM trip")
    suspend fun getAllTrips(): List<Trip>

    @Insert
    suspend fun insertTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)
}

@Entity
data class VisitDetails(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name="country_code") val countryCode: String,
    @ColumnInfo(name="start") val start: String,
    @ColumnInfo(name="end") val end: String
)