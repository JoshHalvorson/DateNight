package com.joshuahalvorson.datenight.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.joshuahalvorson.datenight.BuildConfig
import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.model.ResponseBase
import com.joshuahalvorson.datenight.model.ReviewResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YelpRepository(application: Application) {

    private var yelpApiService: YelpApiService? = RetrofitInstance.getService()
    private var responseBase = MutableLiveData<ResponseBase>()
    private var reviewResponse = MutableLiveData<ReviewResponse>()
    private var businessResponse = MutableLiveData<Businesses>()

    fun getLocalRestaurantData(offset: Int, lat: Double, lon: Double): Observable<ResponseBase>? {
        /*val call =
            yelpApiService?.getLocalRestaurants(BuildConfig.api_key, type, lat, lon, 50)
        call?.enqueue(object : Callback<ResponseBase> {
            override fun onFailure(call: Call<ResponseBase>, t: Throwable) {
                Log.i("responseBase", t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseBase>, response: Response<ResponseBase>) {
                responseBase.postValue(response.body())
            }
        })*/
        return yelpApiService?.getLocalRestaurants(BuildConfig.api_key, "restaurant", lat, lon, 50, offset)
    }

    fun getRestaurantReviews(id: String): MutableLiveData<ReviewResponse> {
        val call = yelpApiService?.getRestaurantReviews(BuildConfig.api_key, id)
        call?.enqueue(object : Callback<ReviewResponse> {
            override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {
                Log.i("reviewResponse", t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ReviewResponse>,
                response: Response<ReviewResponse>
            ) {
                reviewResponse.postValue(response.body())
            }
        })
        return reviewResponse
    }

    fun getRestaurant(id: String): Observable<Businesses>? {
        /*val call = yelpApiService?.getRestaurant(BuildConfig.api_key, id)
        call?.enqueue(object: Callback<Businesses> {
            override fun onFailure(call: Call<Businesses>, t: Throwable) {
                Log.i("businessResponse", t.localizedMessage)
            }

            override fun onResponse(call: Call<Businesses>, response: Response<Businesses>) {
                businessResponse.postValue(response.body())
            }

        })*/
        return yelpApiService?.getRestaurant(BuildConfig.api_key, id)
    }

}