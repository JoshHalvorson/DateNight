package com.joshuahalvorson.datenight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joshuahalvorson.datenight.network.YelpRepository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class YelpViewModelFactory @Inject constructor(var yelpRepository: YelpRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return YelpViewModel(yelpRepository) as T
    }

}