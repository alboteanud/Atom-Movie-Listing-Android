package com.example.atommovielisting.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.atommovielisting.R
import com.example.atommovielisting.model.DetailsMovie
import com.example.atommovielisting.model.Movie
import com.example.atommovielisting.utilities.LogUtils.log
import org.json.JSONException

/**
 * Provides an API for doing all operations with the server data
 */
class NetworkDataSource private constructor(private val context: Context) {

    // LiveData storing the latest downloaded entries
    private val mDownloadedEntries: MutableLiveData<List<Movie>> = MutableLiveData()

    val entries: LiveData<List<Movie>>
        get() = mDownloadedEntries

 private val mDownloadedEntry: MutableLiveData<DetailsMovie> = MutableLiveData()

    val entriy: LiveData<DetailsMovie>
        get() = mDownloadedEntry

    fun downloadEntries(pageNumber: Int = 1, function: (success: List<Movie>?) -> Unit) {
        val url =  getMoviesDownloadUrl(pageNumber)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
//                    val text = "Response: %s".format(response.toString())
//                    Log.d("tag", text)
                try {
//                // Parse the JSON into a list of entries
                    val parsedResult = MyJsonParser().parseEntries(response)
                    val entries = parsedResult.entries
                    log("downloaded entries: " + entries?.size)

                    if (!entries.isNullOrEmpty()) {
                        mDownloadedEntries.postValue(entries)
//                    NotifUtils.notifyIfNeeded(context, entries)
                    }
                    function.invoke(entries)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("tag", "Network: That didn't work! " + error.message)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    private fun getMoviesDownloadUrl(pageNumber: Int): String {
        return "https://api.themoviedb.org/3/movie/popular?api_key=8700e0b55b9438b27963771c2aff54f5&&page=$pageNumber"
    }

    fun downloadEntry(movieId: Int, function: (success: DetailsMovie?) -> Unit) {
        val entryUrl = getEntryEndpointUrl(context, movieId)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, entryUrl, null, { response ->
//                    val text = "Response: %s".format(response.toString())
//                    Log.d("tag", text)
                try {
//                // Parse the JSON into a list of entries
                    val parsedResult = MyJsonParser().parseDetailedEntry(response)
                    val entry = parsedResult.entriy
                    log("downloaded entry: " + entry?.title)

//                    if (!entries.isNullOrEmpty()) {
                        mDownloadedEntry.postValue(entry)
//                    NotifUtils.notifyIfNeeded(context, entries)

                    function.invoke(entry)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("tag", "Network: That didn't work! " + error.message)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }


    companion object {

        private val LOG_TAG = NetworkDataSource::class.java.simpleName
        private val LOCK = Any()
        private var sInstance: NetworkDataSource? = null

        // https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg
        fun buildPosterPhotoUrl(posterPath: String): String {
            return "https://image.tmdb.org/t/p/w500$posterPath"
        }

        // https://api.themoviedb.org/3/movie/741067?api_key=8700e0b55b9438b27963771c2aff54f5
        fun getEntryEndpointUrl(context: Context, entryId: Int): String {
            val apiKey = context.getString(R.string.tmdb_api_key)
            val url = "https://api.themoviedb.org/3/movie/$entryId?api_key=$apiKey"
            return url
        }

        // Get the singleton for this class
        fun getInstance(context: Context): NetworkDataSource {
            log("Getting the network data source")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = NetworkDataSource(context.applicationContext)
                    log("Made new network data source")
                }
            }
            return sInstance!!
        }



    }

}