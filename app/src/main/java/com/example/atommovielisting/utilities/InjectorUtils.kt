package com.example.atommovielisting.utilities

import android.content.Context
import com.example.atommovielisting.database.MyDatabase
import com.example.atommovielisting.database.Repository
import com.example.atommovielisting.network.NetworkDataSource
import com.example.atommovielisting.ui.ViewModelFactory

/**
 * Provides static methods to inject the various classes needed for the app
 */
object InjectorUtils {

    private fun provideRepository(context: Context): Repository {
        val myDatabase = MyDatabase.getInstance(context.applicationContext)
        val executors = AppExecutors.instance
        val networkDataSource =
                NetworkDataSource.getInstance(context.applicationContext)
        return Repository.getInstance(
                myDatabase.moviesDao()!!,
                networkDataSource,
                executors)
    }

    fun provideNetworkDataSource(context: Context): NetworkDataSource {
        // This call to provide repository is necessary if the app starts from a service - in this
// case the repository will not exist unless it is specifically created.
        provideRepository(context.applicationContext)
//        val executors = AppExecutors.instance
        return NetworkDataSource.getInstance(context.applicationContext)
    }

    //    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Date date) {
//        RepositoryWeather repository = provideRepository(context.getApplicationContext());
//        return new DetailViewModelFactory(repository, date);
//    }
    fun provideMainActivityViewModelFactory(context: Context): ViewModelFactory {
        val repository =
                provideRepository(context.applicationContext)
        return ViewModelFactory(repository)
    }
}