package com.joshuahalvorson.datenight.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joshuahalvorson.datenight.model.SavedRestaurant

@Database(entities = [SavedRestaurant::class], version = 1, exportSchema = false)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun savedRestaurantsDao(): SavedRestaurantsDao
}