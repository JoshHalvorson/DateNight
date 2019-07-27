package com.joshuahalvorson.datenight.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.joshuahalvorson.datenight.model.RestaurantResponse
import com.joshuahalvorson.datenight.network.ZomatoRepository

class ZomatoViewModel(private var zomatoRepository: ZomatoRepository): ViewModel() {

    fun getLocalRestaurants(lat: Double, lon: Double, count: Int, page: Int): LiveData<RestaurantResponse> {
        return zomatoRepository.getLocalRestaurantData(lat, lon, count, page)
    }

}