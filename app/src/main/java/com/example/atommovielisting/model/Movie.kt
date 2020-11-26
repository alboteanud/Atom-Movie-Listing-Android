package com.example.atommovielisting.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity(tableName = "movies", indices = [Index(value = ["id"], unique = true)])
class Movie(
    @PrimaryKey var id: Int,
    var title: String?,
    val poster_path: String?,
    val overview: String?,
    val popularity: Double?,
    val releaseDate: Date?,
    val page: Int?
//    @Ignore val picture: Bitmap?
)

