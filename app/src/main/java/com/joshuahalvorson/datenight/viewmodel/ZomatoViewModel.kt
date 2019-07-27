package com.joshuahalvorson.datenight.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joshuahalvorson.datenight.model.RestaurantResponse
import com.joshuahalvorson.datenight.network.ZomatoRepository

class ZomatoViewModel(private var zomatoRepository: ZomatoRepository): ViewModel() {

    private lateinit var restaurantResponse: MutableLiveData<RestaurantResponse>

    fun getLocalRestaurants(lat: Double, lon: Double, count: Int, page: Int): MutableLiveData<RestaurantResponse> {
        restaurantResponse = zomatoRepository.getLocalRestaurantData(lat, lon, count, page)
        return restaurantResponse
    }

}