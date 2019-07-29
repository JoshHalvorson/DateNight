package com.joshuahalvorson.datenight.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joshuahalvorson.datenight.model.ResponseBase
import com.joshuahalvorson.datenight.network.YelpRepository

class YelpViewModel(private var yelpRepository: YelpRepository) : ViewModel() {

    private lateinit var responseBase: MutableLiveData<ResponseBase>

    fun getLocalRestaurants(type: String, lat: Double, lon: Double): MutableLiveData<ResponseBase> {
        return yelpRepository.getLocalRestaurantData(type, lat, lon)
    }

}