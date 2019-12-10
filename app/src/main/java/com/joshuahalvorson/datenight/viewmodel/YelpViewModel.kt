package com.joshuahalvorson.datenight.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.model.ResponseBase
import com.joshuahalvorson.datenight.model.ReviewResponse
import com.joshuahalvorson.datenight.network.YelpRepository
import io.reactivex.Observable

class YelpViewModel(private var yelpRepository: YelpRepository) : ViewModel() {

    private lateinit var restaurantsList: MutableLiveData<ResponseBase>

    fun getLocalRestaurants(offset: Int, lat: Double, lon: Double, categories: String): Observable<ResponseBase>? {
        return yelpRepository.getLocalRestaurantData(offset, lat, lon, categories)
    }

    fun getRestaurantReviews(id: String): MutableLiveData<ReviewResponse> {
        return yelpRepository.getRestaurantReviews(id)
    }

    fun getRestaurant(id: String): Observable<Businesses>? {
        return yelpRepository.getRestaurant(id)
    }

}