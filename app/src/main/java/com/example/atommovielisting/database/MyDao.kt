package com.example.atommovielisting.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.atommovielisting.model.FeedEntry
import java.util.*

@Dao
interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntries(users: List<FeedEntry>)

    @Query("SELECT * FROM movies")
    fun getAllEntries(): LiveData<List<FeedEntry>>

    @Delete
    fun deleteAllEntries(entries: List<FeedEntry>)

    @Query("SELECT COUNT(*) FROM movies")
    fun countAllEntries(): Int

    @Query("DELETE FROM movies WHERE releaseDate < :oldDate")
    fun deleteOldEntries(oldDate: Date)
}