package com.joshuahalvorson.datenight.network

import com.joshuahalvorson.datenight.model.RestaurantResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface ZomatoApiService {

    @GET("search")
    fun getLocalRestaurants(
        @Header("user-key") key: String,
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("sort") sort: String): Call<RestaurantResponse>

}