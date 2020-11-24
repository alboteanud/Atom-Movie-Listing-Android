package com.example.atommovielisting.network
import com.example.atommovielisting.model.FeedEntry
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

internal class ServerResponse (val entries: List<FeedEntry>?)

internal class MyJsonParser {

    @Throws(JSONException::class)
    fun parseForecastWeather(jsonObject: JSONObject): ServerResponse {
        if (hasHttpError(jsonObject)) {
            return ServerResponse(null)
        }
        val weatherForecast = fromJsonForecast(jsonObject)
        return ServerResponse(weatherForecast)
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
        private fun fromJsonForecast(forecastJson: JSONObject): List<FeedEntry> {
            val jsonResultArray = forecastJson.getJSONArray(RESULTS)
            val entries = mutableListOf<FeedEntry>()
            for (i in 0 until jsonResultArray.length()) {
                val entryJson = jsonResultArray.getJSONObject(i)
                val entry = fromJsonToEntry(entryJson)
                entries.add(i, entry)
            }
            return entries.toList()
        }


        private fun fromJsonToEntry(entryJson: JSONObject): FeedEntry {
            val id = entryJson.getInt("id")
            val title = entryJson.getString("title")
            val overview = entryJson.getString("overview")
            val posterPath = entryJson.getString("poster_path")
            val popularity = entryJson.getDouble("popularity")

            val releaseDateString = entryJson.getString("release_date")
//            val releaseDate = LocalDate.parse(releaseDateString, DateTimeFormatter.ISO_DATE)
           val releaseDate = java.sql.Date.valueOf(releaseDateString)
            return FeedEntry(id, title, posterPath, overview, popularity, releaseDate)

        }
    }
}

// response
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