package com.example.atommovielisting.database

import android.text.format.DateUtils.DAY_IN_MILLIS
import androidx.lifecycle.LiveData
import com.example.atommovielisting.model.Movie
import com.example.atommovielisting.utilities.LogUtils.log
import com.example.atommovielisting.network.NetworkDataSource
import com.example.atommovielisting.utilities.AppExecutors
import com.example.atommovielisting.utilities.LogUtils.inTestMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.util.*

/**
 * Handles data operations. Acts as a mediator between [NetworkDataSource]
 * and [MyDao]
 */
class Repository private constructor(
        private val mDao: MyDao,
        private val mNetworkDataSource: NetworkDataSource,
        private val mExecutors: AppExecutors
) {
    private var initializedEntries = false

    init {

        GlobalScope.launch(Dispatchers.Main) {

            mNetworkDataSource.entries.observeForever { newForecastsFromNetwork ->
                mExecutors.diskIO().execute {
                    // Deletes old historical data
                    deleteOldEntries()
                    // Insert our new weather data into Sunshine's database
                    mDao.insertEntries(newForecastsFromNetwork)
                    log( "Old entries deleted. New values inserted.")
                }
            }

        }

    }

    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private val isFetchEntriesNeeded: Boolean
        get() {
            val entrieCount = mDao.countAllEntries()
            log("stored entries count: $entrieCount")
            return entrieCount < NUM_MIN_ENTRIES_COUNTS
        }


    /** Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    @Synchronized
    fun initializeEntries() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (initializedEntries) return
        initializedEntries = true

//        mNetworkDataSource.scheduleFetchEntries()

        mExecutors.diskIO().execute {
            if (!isFetchEntriesNeeded) return@execute
            mNetworkDataSource.downloadEntries { firstEntry ->

            }
        }
    }

    @Synchronized
    fun loadNewEntries() {
        mExecutors.diskIO().execute {
            val nextPageNumber = getLastDownloadedPage() + 1
            mNetworkDataSource.downloadEntries(nextPageNumber) { firstEntry ->
            }
        }
    }

    private fun getLastDownloadedPage(): Int{
        val pageNumber =  mDao.getLastEntry()?.page
        if (pageNumber!=null) return pageNumber
        return 0
    }

    val allEntries: LiveData<List<Movie>>
        get() {
            initializeEntries()
            return mDao.getAllEntries()
        }

    private fun deleteOldEntries() {
        val oldMills = currentTimeMillis() - OLD_DAYS_COUNT * DAY_IN_MILLIS
        val oldDate = Date(oldMills)
        mDao.deleteOldEntries(oldDate)
    }

    fun deleteAllEntries() {
        mExecutors.diskIO().execute {
            mDao.deleteAllEntries()
        }

    }

    companion object {
        private val OLD_DAYS_COUNT = 30
        private val LOCK = Any()
        private var sInstance: Repository? = null
        private val NUM_MIN_ENTRIES_COUNTS = if (inTestMode) 1 else 20

        @Synchronized
        fun getInstance(
            myDao: MyDao, networkDataSource: NetworkDataSource,
            executors: AppExecutors
        ): Repository {
            log("Getting the repository")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance =
                            Repository(
                                    myDao, networkDataSource,
                                    executors
                            )
                    log("Made new repository")
                }
            }
            return sInstance!!
        }

    }
}