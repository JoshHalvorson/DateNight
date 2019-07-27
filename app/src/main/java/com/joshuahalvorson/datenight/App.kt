package com.joshuahalvorson.datenight

import android.app.Application
import com.joshuahalvorson.datenight.di.AppModule
import com.joshuahalvorson.datenight.di.DaggerZomatoComponent
import com.joshuahalvorson.datenight.di.ZomatoComponent

class App : Application() {
    lateinit var zomatoComponent: ZomatoComponent

    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        zomatoComponent = DaggerZomatoComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}