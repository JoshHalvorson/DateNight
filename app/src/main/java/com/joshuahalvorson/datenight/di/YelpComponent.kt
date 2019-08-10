package com.joshuahalvorson.datenight.di

import com.joshuahalvorson.datenight.view.fragment.SavedRestaurantsFragment
import com.joshuahalvorson.datenight.view.fragment.RandomRestaurantFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, YelpRepoModule::class])
interface YelpComponent {
    fun inject(randomRestaurantFragment: RandomRestaurantFragment)
    fun inject(savedRestaurantsFragment: SavedRestaurantsFragment)
}