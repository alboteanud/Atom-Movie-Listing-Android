package com.example.atommovielisting.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.atommovielisting.model.Movie
import java.util.*

@Dao
interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntries(users: List<Movie>)

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getAllEntries(): LiveData<List<Movie>>

    @Query("DELETE FROM movies")
    fun deleteAllEntries()

    @Query("SELECT COUNT(*) FROM movies")
    fun countAllEntries(): Int

    @Query("DELETE FROM movies WHERE releaseDate < :oldDate")
    fun deleteOldEntries(oldDate: Date)
}