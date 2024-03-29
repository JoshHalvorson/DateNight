package com.joshuahalvorson.datenight.network

import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.model.ResponseBase
import com.joshuahalvorson.datenight.model.ReviewResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpApiService {

    @GET("businesses/search")
    fun getLocalRestaurants(
        @Header("Authorization") key: String,
        @Query("term") term: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("categories") categories: String
    ): Observable<ResponseBase>

    @GET("businesses/{id}/reviews")
    fun getRestaurantReviews(
        @Header("Authorization") key: String,
        @Path("id") id: String
    ): Call<ReviewResponse>

    @GET("businesses/{id}")
    fun getRestaurant(
        @Header("Authorization") key: String,
        @Path("id") id: String
    ): Observable<Businesses>

}