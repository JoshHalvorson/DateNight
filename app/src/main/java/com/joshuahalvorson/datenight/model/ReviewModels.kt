package com.joshuahalvorson.datenight.model

data class ReviewResponse(val reviews: List<Review>?, val total: Number?, val possible_languages: List<String>?)

data class Review(
    val id: String?,
    val url: String?,
    val text: String?,
    val rating: Number?,
    val time_created: String?,
    val user: User?
)

data class User(val id: String?, val profile_url: String?, val image_url: String?, val name: String?)
