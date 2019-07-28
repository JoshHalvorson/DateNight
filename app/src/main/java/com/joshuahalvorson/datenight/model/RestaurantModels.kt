package com.joshuahalvorson.datenight.model

data class ResponseBase(val businesses: List<Businesses>, val total: Number?, val region: Region?)

data class Businesses(
    val id: String?,
    val alias: String?,
    val name: String?,
    val image_url: String?,
    val is_closed: Boolean?,
    val url: String?,
    val review_count: Int?,
    val categories: List<Categories>?,
    val rating: Double?,
    val coordinates: Coordinates?,
    val transactions: List<Any>?,
    val price: String?,
    val location: Location?,
    val phone: String?,
    val display_phone: String?,
    val distance: Double?
)

data class Categories(val alias: String?, val title: String?)

data class Center(val longitude: Double?, val latitude: Double?)

data class Coordinates(val latitude: Double?, val longitude: Double?)

data class Location(
    val address1: String?,
    val address2: String?,
    val address3: String?,
    val city: String?,
    val zip_code: String?,
    val country: String?,
    val state: String?,
    val display_address: List<String>?
)

data class Region(val center: Center?)