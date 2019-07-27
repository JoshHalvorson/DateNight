package com.joshuahalvorson.datenight.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.joshuahalvorson.datenight.BuildConfig
import com.joshuahalvorson.datenight.model.RestaurantResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ZomatoRepository(application: Application) {

    private var restaurantResponse = MutableLiveData<RestaurantResponse>()
    private var zomatoApiService: ZomatoApiService? = RetrofitInstance.getService()

    fun getLocalRestaurantData(lat: Double, lon: Double, count: Int, page: Int): MutableLiveData<RestaurantResponse> {
        val call =
            zomatoApiService?.getLocalRestaurants(BuildConfig.api_key, page, count, lat, lon, "rating")
        call?.enqueue(object : Callback<RestaurantResponse> {
            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                Log.i("restaurantRespone", t.localizedMessage)
            }

            override fun onResponse(call: Call<RestaurantResponse>, response: Response<RestaurantResponse>) {
                restaurantResponse.postValue(response.body())
            }
        })
        return restaurantResponse
    }

}