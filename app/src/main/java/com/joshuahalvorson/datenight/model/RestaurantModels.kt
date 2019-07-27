package com.joshuahalvorson.datenight.model


data class All_reviews(val reviews: List<Any>?)

data class RestaurantResponse(
    val results_found: Int?,
    val results_start: Int?,
    val results_shown: Int?,
    val restaurants: List<Restaurants>?
)

data class Bg_color(val type: String?, val tint: String?)

data class Has_menu_status(val delivery: Int?, val takeaway: Int?)

data class Location(
    val address: String?,
    val locality: String?,
    val city: String?,
    val city_id: Int?,
    val latitude: Float?,
    val longitude: Float?,
    val zipcode: String?,
    val country_id: Int?,
    val locality_verbose: String?
)

data class Photo(
    val id: String?,
    val url: String?,
    val thumb_url: String?,
    val user: User?,
    val res_id: Int?,
    val caption: String?,
    val timestamp: Int?,
    val friendly_time: String?,
    val width: Int?,
    val height: Int?
)

data class Photos(val photo: Photo?)

data class R(val has_menu_status: Has_menu_status?, val res_id: Int?)

data class Rating_obj(val title: Title?, val bg_color: Bg_color?)

data class Restaurant(
    val R: R?,
    val apikey: String?,
    val id: String?,
    val name: String?,
    val url: String?,
    val location: Location?,
    val switch_to_order_menu: Int?,
    val cuisines: String?,
    val timings: String?,
    val average_cost_for_two: Int?,
    val price_range: Int?,
    val currency: String?,
    val highlights: List<String>?,
    val offers: List<Any>?,
    val opentable_support: Int?,
    val is_zomato_book_res: Int?,
    val mezzo_provider: String?,
    val is_book_form_web_view: Int?,
    val book_form_web_view_url: String?,
    val book_again_url: String?,
    val thumb: String?,
    val user_rating: User_rating?,
    val all_reviews_count: Int?,
    val photos_url: String?,
    val photo_count: Int?,
    val photos: List<Photos>?,
    val menu_url: String?,
    val featured_image: String?,
    val has_online_delivery: Int?,
    val is_delivering_now: Int?,
    val include_bogo_offers: Boolean?,
    val deeplink: String?,
    val is_table_reservation_supported: Int?,
    val has_table_booking: Int?,
    val events_url: String?,
    val phone_numbers: String?,
    val all_reviews: All_reviews?,
    val establishment: List<String>?,
    val establishment_types: List<Any>?
)

data class Restaurants(val restaurant: Restaurant?)

data class Title(val text: String?)

data class User(
    val name: String?,
    val foodie_level: String?,
    val foodie_level_num: Int?,
    val foodie_color: String?,
    val profile_url: String?,
    val profile_image: String?,
    val profile_deeplink: String?
)

data class User_rating(
    val aggregate_rating: Float?,
    val rating_text: String?,
    val rating_color: String?,
    val rating_obj: Rating_obj?,
    val votes: Int?,
    val custom_rating_text: String?,
    val custom_rating_text_background: String?,
    val rating_tool_tip: String?
)