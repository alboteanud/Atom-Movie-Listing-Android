package com.example.atommovielisting.database

import android.text.format.DateUtils.HOUR_IN_MILLIS
import androidx.lifecycle.LiveData
import com.example.atommovielisting.model.FeedEntry
import com.example.atommovielisting.utilities.LogUtils.log
import com.example.atommovielisting.network.NetworkDataSource
import com.example.atommovielisting.utilities.AppExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.util.*

/**
 * Handles data operations. Acts as a mediator between [NetworkDataSource]
 * and [WeatherDao]
 */
class Repository private constructor(
        private val mDao: MyDao,
        private val mNetworkDataSource: NetworkDataSource,
        private val mExecutors: AppExecutors
) {
    private var initializedForecast = false

    init {

        GlobalScope.launch(Dispatchers.Main) {

            mNetworkDataSource.forecasts.observeForever { newForecastsFromNetwork ->
                mExecutors.diskIO().execute {
                    // Deletes old historical data
                    deleteOldWeatherData()
                    // Insert our new weather data into Sunshine's database
                    mDao.bulkInsert(*newForecastsFromNetwork)
                    log( "Old data deleted. New values inserted.")
                }
            }

        }

    }


    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private val isFetchForecastNeeded: Boolean
        get() {
//            val now = Date(currentTimeMillis())
//            val count = mWeatherDao.countAllFutureWeatherEntries(now)
//            return count < NetworkDataSource.NUM_MIN_DATA_COUNTS
            return true
        }


    /** Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    @Synchronized
    fun initializeForecastData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (initializedForecast) return
        initializedForecast = true


//            // java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
        mExecutors.diskIO().execute {
            if (!isFetchForecastNeeded) return@execute
            mNetworkDataSource.fetchMovies { firstWeatherEntry ->
                if (firstWeatherEntry != null){
//                    initializeWebcamData(firstWeatherEntry)
                }

            }
        }
    }


    val allEntries: LiveData<Array<FeedEntry>>
        get() {
            initializeForecastData()


            return mDao.loadAllEntries()
        }


    private fun deleteOldWeatherData() {
        //        Date today = SunshineDateUtils.getNormalizedUtcDateForToday();
        val oldTime = currentTimeMillis() - HOUR_IN_MILLIS
        val date = Date(oldTime)
//        mWeatherDao.deleteOldWeather(date)
    }


    companion object {

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: Repository? = null

        @Synchronized
        fun getInstance(
                weatherDao: MyDao, networkDataSource: NetworkDataSource,
                executors: AppExecutors
        ): Repository {
            log("Getting the repository")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance =
                            Repository(
                                    weatherDao, networkDataSource,
                                    executors
                            )
                    log("Made new repository")
                }
            }
            return sInstance!!
        }

    }
}