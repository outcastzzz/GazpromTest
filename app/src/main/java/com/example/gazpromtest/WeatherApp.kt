package com.example.gazpromtest

import android.app.Application
import com.example.gazpromtest.di.ApplicationComponent
import com.example.gazpromtest.di.DaggerApplicationComponent

class WeatherApp: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }

}