package com.example.gazpromtest.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.example.gazpromtest.presentation.cities.CitiesContent
import com.example.gazpromtest.presentation.theme.GazpromTestTheme
import com.example.gazpromtest.presentation.weather.WeatherContent

@Composable
fun RootContent(component: RootComponent) {

    GazpromTestTheme {
        Children(
            stack = component.stack
        ) { 
            when(val instance = it.instance) {
                is RootComponent.Child.Cities -> CitiesContent(instance.component)
                is RootComponent.Child.Weather -> WeatherContent(instance.component)
            }
        }
    }

}