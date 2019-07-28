package com.joshuahalvorson.datenight.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.joshuahalvorson.datenight.BuildConfig
import com.joshuahalvorson.datenight.model.ResponseBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YelpRepository(application: Application) {

    private var responseBase = MutableLiveData<ResponseBase>()
    private var yelpApiService: YelpApiService? = RetrofitInstance.getService()

    fun getLocalRestaurantData(type: String, lat: Double, lon: Double): MutableLiveData<ResponseBase> {
        val call =
            yelpApiService?.getLocalRestaurants(BuildConfig.api_key, type, lat, lon, 50)
        call?.enqueue(object : Callback<ResponseBase> {
            override fun onFailure(call: Call<ResponseBase>, t: Throwable) {
                Log.i("responseBase", t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseBase>, response: Response<ResponseBase>) {
                responseBase.postValue(response.body())
            }
        })
        return responseBase
    }

}