package com.example.atommovielisting.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.atommovielisting.model.Movie

@Dao
interface MyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg entries: Movie)

//    @Insert
//    fun insertBothUsers(user1: User, user2: User)

//    @Insert
//    fun insertUsersAndFriends(user: User, friends: List<User>)

    @Query("SELECT * FROM movies")
    fun loadAllEntries(): LiveData<Array<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(vararg weather: Movie)


    @Query("SELECT * FROM movies")
    fun getAllEntries(): LiveData<List<Movie>>
//
//
//    @Query("SELECT COUNT(*) FROM weather WHERE date > :date")
//    fun countAllFutureWeatherEntries(date: Date): Int
}