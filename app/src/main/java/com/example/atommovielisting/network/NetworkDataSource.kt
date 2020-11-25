package com.example.atommovielisting.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.atommovielisting.model.FeedEntry
import com.example.atommovielisting.utilities.AppExecutors
import com.example.atommovielisting.utilities.LogUtils.log
import org.json.JSONException

/**
 * Provides an API for doing all operations with the server data
 */
class NetworkDataSource private constructor(private val context: Context) {

    // LiveData storing the latest downloaded entries
    private val mDownloadedEntries: MutableLiveData<List<FeedEntry>> = MutableLiveData()

    val entries: LiveData<List<FeedEntry>>
        get() = mDownloadedEntries

    fun downloadEntries(function: (success: List<FeedEntry>?) -> Unit) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
//                    val text = "Response: %s".format(response.toString())
//                    Log.d("tag", text)
                try {
//                // Parse the JSON into a list of entries
                    val parsedResult = MyJsonParser().parseForecastWeather(response)
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
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }


    companion object {
        private val url = "https://api.themoviedb.org/3/movie/popular?api_key=8700e0b55b9438b27963771c2aff54f5"
        private val LOG_TAG = NetworkDataSource::class.java.simpleName
        private val LOCK = Any()
        private var sInstance: NetworkDataSource? = null


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