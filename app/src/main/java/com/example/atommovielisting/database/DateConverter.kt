
package com.example.atommovielisting.database

import androidx.room.TypeConverter
import java.util.*

/**
 * [TypeConverter] for long to [Date]
 *
 *
 * This stores the _date as a long in the database, but returns it as a [Date]
 */
internal object DateConverter {
    @JvmStatic
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @JvmStatic
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}