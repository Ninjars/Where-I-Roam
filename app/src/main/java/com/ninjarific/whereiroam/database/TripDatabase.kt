package com.ninjarific.whereiroam.database

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@Database(entities = [TripEntry::class, VisitEntry::class], version = 1)
@TypeConverters(RoamConverters::class)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripEntryDao(): TripEntryDao
    abstract fun visitDetailsDao(): VisitEntryDao
    abstract fun tripVisitsDao(): TripVisitsDao
}

object RoamConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}

@Dao
interface TripEntryDao {
    @Query("SELECT * FROM tripEntry")
    suspend fun getAll(): List<TripEntry>

    @Insert
    suspend fun insert(tripEntry: TripEntry): Long

    @Update
    suspend fun update(tripEntry: TripEntry)

    @Delete
    suspend fun delete(tripEntry: TripEntry)
}

@Entity
data class TripEntry(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "title") val title: String
)

@Dao
interface VisitEntryDao {
    @Query("SELECT * FROM visitEntry WHERE trip_id=:tripId")
    suspend fun getVisits(tripId: Long): List<VisitEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visits: List<VisitEntry>)

    @Update
    suspend fun update(visit: VisitEntry)

    @Delete
    suspend fun delete(visit: VisitEntry)
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TripEntry::class,
            parentColumns = ["uid"],
            childColumns = ["trip_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class VisitEntry(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "trip_id", index = true) val trip: Long,
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "start") val start: OffsetDateTime,
    @ColumnInfo(name = "end") val end: OffsetDateTime?
)

@Dao
interface TripVisitsDao {
    @Transaction
    @Query("SELECT * FROM tripEntry WHERE uid = :tripId")
    suspend fun get(tripId: Long): TripVisits
}

class TripVisits {
    @Embedded
    var tripEntry: TripEntry? = null

    @Relation(parentColumn = "uid", entityColumn = "trip_id")
    var visitDetails: List<VisitEntry> = emptyList()

    // TODO: implement these in an object that wraps this database object
//    val countryCodes = visitDetails.map { it.countryCode }
//    val tripStart = visitDetails.firstOrNull()?.start
//    val tripEnd = visitDetails.lastOrNull()?.end
}