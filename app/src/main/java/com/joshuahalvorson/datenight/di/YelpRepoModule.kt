package com.joshuahalvorson.datenight.di

import android.app.Application
import com.joshuahalvorson.datenight.network.YelpRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class YelpRepoModule {

    @Provides
    @Singleton
    fun yelpRepoProvider(application: Application): YelpRepository {
        return YelpRepository(application)
    }

}