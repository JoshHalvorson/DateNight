package com.joshuahalvorson.datenight.di

import com.joshuahalvorson.datenight.view.fragment.RandomRestaurantFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ZomatoRepoModule::class])
interface ZomatoComponent {
    fun inject(randomRestaurantFragment: RandomRestaurantFragment)

}