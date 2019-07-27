package com.joshuahalvorson.datenight.di

import android.app.Application
import com.joshuahalvorson.datenight.network.ZomatoRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ZomatoRepoModule {

    @Provides
    @Singleton
    fun zomatoRepoProvider(application: Application): ZomatoRepository {
        return ZomatoRepository(application)
    }

}