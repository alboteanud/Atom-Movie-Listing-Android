package com.example.atommovielisting.network
import com.example.atommovielisting.model.DetailsMovie
import com.example.atommovielisting.model.Movie
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection

internal class ServerResponseEntries (val entries: List<Movie>?)
internal class ServerResponseEntry (val entriy: DetailsMovie?)

internal class MyJsonParser {

    @Throws(JSONException::class)
    fun parseEntries(jsonObject: JSONObject): ServerResponseEntries {
        if (hasHttpError(jsonObject)) {
            return ServerResponseEntries(null)
        }
        val entries = fromJsonEntries(jsonObject)
        return ServerResponseEntries(entries)
    }

@Throws(JSONException::class)
    fun parseDetailedEntry(jsonObject: JSONObject): ServerResponseEntry {
        if (hasHttpError(jsonObject)) {
            return ServerResponseEntry(null)
        }
        val entry = fromJsonDetailedEntry(jsonObject)
        return ServerResponseEntry(entry)
    }


    companion object {
        private const val RESULTS = "results"
        private const val MESSAGE_CODE = "cod"

        @Throws(JSONException::class)
        private fun hasHttpError(forecastJson: JSONObject): Boolean {
            if (forecastJson.has(MESSAGE_CODE)) {
                return when (forecastJson.getInt(MESSAGE_CODE)) {
                    HttpURLConnection.HTTP_OK -> false
                    HttpURLConnection.HTTP_NOT_FOUND ->  true // Server probably down
                    else -> true
                }
            }
            return false
        }

        @Throws(JSONException::class)
        private fun fromJsonEntries(entriesJson: JSONObject): List<Movie> {
            val jsonResultArray = entriesJson.getJSONArray(RESULTS)
            val entries = mutableListOf<Movie>()
            for (i in 0 until jsonResultArray.length()) {
                val entryJson = jsonResultArray.getJSONObject(i)
                val entry = fromJsonToEntry(entryJson)
                entries.add(i, entry)
            }
            return entries.toList()
        }


        @Throws(JSONException::class)
        private fun fromJsonToEntry(entryJson: JSONObject): Movie {
            val id = entryJson.getInt("id")
            val title = entryJson.getString("title")
            val overview = entryJson.getString("overview")
            val posterPath = entryJson.getString("poster_path")
            val popularity = entryJson.getDouble("popularity")

            val releaseDateString = entryJson.getString("release_date")
//            val releaseDate = LocalDate.parse(releaseDateString, DateTimeFormatter.ISO_DATE)
           val releaseDate = java.sql.Date.valueOf(releaseDateString)
            return Movie(id, title, posterPath, overview, popularity, releaseDate)

        }
    }
}

// response type entries
//{
//"page": 3,
//"total_results": 10000,
//"total_pages": 500,
//"results": [
//{"popularity": 1387.546,
//"vote_count": 338,
//"video": false,
//"poster_path": "/7D430eqZj8y3oVkLFfsWXGRcpEG.jpg",
//"id": 528085,
//"adult": false,
//"backdrop_path": "/5UkzNSOK561c2QRy2Zr4AkADzLT.jpg",
//"original_language": "en",
//"original_title": "2067",
//"genre_ids": [878,53,18],
//"title": "2067",
//"vote_average": 4.7,
//"overview": "A lowly utility worker is called to the future by a mysterious radio signal, he must leave his dying wife to embark on a journey that will force him to face his deepest fears in an attempt to change the fabric of reality and save humankind from its greatest environmental crisis yet.",
//"release_date": "2020-10-01"}




@Throws(JSONException::class)
private fun fromJsonDetailedEntry(entryJson: JSONObject): DetailsMovie {
    val id = entryJson.getInt("id")

    val jsonGenresArray = entryJson.getJSONArray("genres")
    val genres = mutableListOf<String>()
    for (i in 0 until jsonGenresArray.length()) {
        val genre = jsonGenresArray.getJSONObject(i)
        val genreName = genre.getString("name")
        genres.add(i, genreName)
    }

    val popularity = entryJson.getDouble("popularity")
    val releaseDateString = entryJson.getString("release_date")
    val releaseDate = java.sql.Date.valueOf(releaseDateString)
    val posterPath = entryJson.getString("poster_path")
    val title = entryJson.getString("title")
    val overview = entryJson.getString("overview")
    return DetailsMovie(id, title, posterPath, overview, popularity, releaseDate, genres)
}

// response detailed entry
/*
   { "adult": false,
    "backdrop_path": "/aO5ILS7qnqtFIprbJ40zla0jhpu.jpg",
    "belongs_to_collection": null,
    "budget": 0,
    "genres": [
    {
        "id": 28,
        "name": "Action"
    },
    {
        "id": 53,
        "name": "Thriller"
    },
    {
        "id": 12,
        "name": "Adventure"
    },
    {
        "id": 18,
        "name": "Drama"
    }
    ],
    "homepage": "https://xmovies8.app/",
    "id": 741067,
    "imdb_id": "tt10804786",
    "original_language": "en",
    "original_title": "Welcome to Sudden Death",
    "overview": "Jesse Freeman is a former special forces officer and explosives expert now working a regular job as a security guard in a state-of-the-art basketball arena. Trouble erupts when a tech-savvy cadre of terrorists kidnap the team's owner and Jesse's daughter during opening night. Facing a ticking clock and impossible odds, it's up to Jesse to not only save them but also a full house of fans in this highly charged action thriller.",
    "popularity": 695.001,
    "poster_path": "/elZ6JCzSEvFOq4gNjNeZsnRFsvj.jpg",
    "production_companies": [],
    "production_countries": [
    {
        "iso_3166_1": "CA",
        "name": "Canada"
    },
    {
        "iso_3166_1": "FR",
        "name": "France"
    },
    {
        "iso_3166_1": "JP",
        "name": "Japan"
    },
    {
        "iso_3166_1": "GB",
        "name": "United Kingdom"
    },
    {
        "iso_3166_1": "US",
        "name": "United States of America"
    }
    ],
    "release_date": "2020-09-29",
    "revenue": 0,
    "runtime": 80,
    "spoken_languages": [
    {
        "english_name": "English",
        "iso_639_1": "en",
        "name": "English"
    },
    {
        "english_name": "German",
        "iso_639_1": "de",
        "name": "Deutsch"
    }
    ],
    "status": "Released",
    "tagline": "",
    "title": "Welcome to Sudden Death",
    "video": false,
    "vote_average": 6.4,
    "vote_count": 162
}*/
