package com.joshuahalvorson.datenight.network

import com.joshuahalvorson.datenight.model.ResponseBase
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApiService {

    @GET("businesses/search")
    fun getLocalRestaurants(
        @Header("Authorization") key: String,
        @Query("term") term: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("limit") limit: Int
    ): Call<ResponseBase>

}