package com.example.atommovielisting.model

import java.util.*

class DetailsMovie(var id: Int,
                   var title: String?,
                   val poster_path: String?,
                   val overview: String?,
                   val popularity: Double?,
                   val releaseDate: Date?,
                   val genres: List<String>) {
}