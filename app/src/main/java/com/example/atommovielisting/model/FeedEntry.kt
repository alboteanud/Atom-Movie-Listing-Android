package com.example.atommovielisting.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity(tableName = "movies", indices = [Index(value = ["id"], unique = true)])
class FeedEntry(
    @PrimaryKey var id: Int,
    var title: String?,
    val poster_path: String?,
    val overview: String?,
    val popularity: Double?,
    val releaseDate: Date?
//    @Ignore val picture: Bitmap?
)

