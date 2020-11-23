
package com.example.atommovielisting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.atommovielisting.database.Repository

class ViewModelFactory(private val mRepository: Repository) :
    NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyViewModel(mRepository) as T
    }

}