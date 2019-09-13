package com.joshuahalvorson.datenight.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.joshuahalvorson.datenight.model.SavedRestaurant

@Dao
interface SavedRestaurantsDao {
    @Query("SELECT * FROM saved_restaurants")
    fun getAllRestaurants(): List<SavedRestaurant>

    @Query("SELECT COUNT(restaurant_id) FROM saved_restaurants WHERE restaurant_id = (:restaurantId)")
    fun getRestaurantById(restaurantId: String): Int

    @Query("DELETE FROM saved_restaurants WHERE restaurant_id = (:restaurantId)")
    fun deleteRestaurantById(restaurantId: String)

    @Insert
    fun insertAll(vararg restaurants: SavedRestaurant)

    @Query("DELETE FROM saved_restaurants")
    fun deleteAll()
}