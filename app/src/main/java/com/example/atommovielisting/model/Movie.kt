package com.example.atommovielisting.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "movies", indices = [Index(value = ["id"], unique = true)])
class Movie(
    @PrimaryKey var id: Int,
    var title: String?,
    val poster_path: String?,
    val overview: String?,
    val page: Int,
    val popularity: Double?
//    @Ignore val picture: Bitmap?
)

