package com.joshuahalvorson.datenight.di

import com.joshuahalvorson.datenight.view.fragment.RandomRestaurantFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, YelpRepoModule::class])
interface YelpComponent {
    fun inject(randomRestaurantFragment: RandomRestaurantFragment)

}