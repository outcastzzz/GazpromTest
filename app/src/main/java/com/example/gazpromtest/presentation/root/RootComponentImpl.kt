package com.example.gazpromtest.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.presentation.cities.CitiesComponentImpl
import com.example.gazpromtest.presentation.weather.WeatherComponentImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable

class RootComponentImpl @AssistedInject constructor(
    @Assisted("componentContext") componentContext: ComponentContext,
    private val citiesComponentFactory: CitiesComponentImpl.Factory,
    private val weatherStoreFactory: WeatherComponentImpl.Factory
): RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Cities,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {

        return when(config) {
            Config.Cities -> {
                val component = citiesComponentFactory.create(
                    componentContext,
                    onForecastForCityRequested = { city ->
                        navigation.push(Config.Weather(city))
                    },
                )
                RootComponent.Child.Cities(component)
            }
            is Config.Weather -> {
                val component = weatherStoreFactory.create(
                    city = config.city,
                    onBackClicked = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Weather(component)
            }
        }

    }

    @Serializable
    sealed interface Config {

        @Serializable
        data object Cities: Config

        @Serializable
        data class Weather(val city: City): Config

    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): RootComponentImpl
    }

}