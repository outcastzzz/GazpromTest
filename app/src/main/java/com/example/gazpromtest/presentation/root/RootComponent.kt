package com.example.gazpromtest.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.gazpromtest.presentation.cities.CitiesComponent
import com.example.gazpromtest.presentation.weather.WeatherComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class Cities(val component: CitiesComponent): Child

        data class Weather(val component: WeatherComponent): Child

    }

}