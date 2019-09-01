package com.ninjarific.whereiroam.database

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Database(entities = [Trip::class, VisitDetails::class], version = 1)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun visitDetailsDao(): VisitDetailsDao
    abstract fun tripDetailsDao(): TripDetailsDao
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trip order by trip.trip_start")
    suspend fun getAll(): List<Trip>

    @Insert
    suspend fun insert(trip: Trip) : Long

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)
}

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="first_country_code") val firstCountryCode: String,
    @ColumnInfo(name="trip_start") val tripStart: Long,
    @ColumnInfo(name="trip_end") val tripEnd: Long?
)

@Dao
interface VisitDetailsDao {
    @Query("SELECT * FROM visitDetails WHERE trip_id=:tripId")
    suspend fun getVisits(tripId: Long): List<VisitDetails>

    @Insert
    suspend fun insert(visit: VisitDetails) : Long

    @Update
    suspend fun update(visit: VisitDetails)

    @Delete
    suspend fun delete(visit: VisitDetails)
}

@Entity(foreignKeys = [
    ForeignKey(
        entity = Trip::class,
        parentColumns = ["uid"],
        childColumns = ["trip_id"],
        onDelete = CASCADE,
        onUpdate = CASCADE
        )
])
data class VisitDetails(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name="trip_id", index=true) val trip: Long,
    @ColumnInfo(name="country_code") val countryCode: String,
    @ColumnInfo(name="start") val start: Long,
    @ColumnInfo(name="end") val end: Long
)

@Dao
interface TripDetailsDao {
    @Transaction
    @Query("SELECT * FROM trip WHERE uid = :tripId")
    suspend fun get(tripId: Long): TripDetails
}

class TripDetails {
    @Embedded
    var trip: Trip? = null

    @Relation(parentColumn = "uid", entityColumn = "trip_id")
    var visitDetails: List<VisitDetails> = emptyList()
}