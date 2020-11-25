package com.example.atommovielisting.ui

import androidx.lifecycle.*
import com.example.atommovielisting.database.Repository
import com.example.atommovielisting.model.FeedEntry

class MyViewModel internal constructor(repository: Repository) : ViewModel(),
        LifecycleObserver {
    val entries: LiveData<List<FeedEntry>> = repository.allEntries

//    private val mutableTimestamp: MutableLiveData<Long> = MutableLiveData()

//    private fun searchCurrentWeatherByTimestamp(timestamp: Long) {
//        mutableTimestamp.value = timestamp
//    }

//    val currentWeatherObservable: LiveData<List<WeatherEntry>> =
//            Transformations.switchMap(mutableTimestamp) { timestamp ->
//                repository.getCurrentWeather(timestamp)
//            }

}