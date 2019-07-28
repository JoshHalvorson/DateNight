package com.joshuahalvorson.datenight

import android.app.Application
import com.joshuahalvorson.datenight.di.AppModule
import com.joshuahalvorson.datenight.di.DaggerYelpComponent
import com.joshuahalvorson.datenight.di.YelpComponent

class App : Application() {
    lateinit var yelpComponent: YelpComponent

    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        yelpComponent = DaggerYelpComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}