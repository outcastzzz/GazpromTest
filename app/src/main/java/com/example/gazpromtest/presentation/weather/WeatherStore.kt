package com.example.gazpromtest.presentation.weather

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.entity.Weather
import com.example.gazpromtest.domain.useCase.GetWeatherForCityUseCase
import com.example.gazpromtest.presentation.weather.WeatherStore.Intent
import com.example.gazpromtest.presentation.weather.WeatherStore.Label
import com.example.gazpromtest.presentation.weather.WeatherStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface WeatherStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickBack: Intent

        data object ClickUpdate: Intent

    }

    data class State(
        val city: City,
        val weatherState: WeatherState
    ) {

        sealed interface WeatherState {

            data object Initial : WeatherState

            data object Loading : WeatherState

            data object Error : WeatherState

            data class Loaded(val weather: Weather) : WeatherState

        }

    }

    sealed interface Label {

        data object ClickBack: Label

    }
}

class WeatherStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getWeatherForCityUseCase: GetWeatherForCityUseCase,
) {

    fun create(city: City): WeatherStore =
        object : WeatherStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ForecastStore",
            initialState = State(
                city,
                State.WeatherState.Initial
            ),
            bootstrapper = BootstrapperImpl(city),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class WeatherLoaded(val weather: Weather): Action

        data object WeatherStartLoading: Action

        data object WeatherLoadingError: Action

    }

    private sealed interface Msg {

        data class WeatherLoaded(val weather: Weather): Msg

        data object WeatherStartLoading: Msg

        data object WeatherLoadingError: Msg

    }

    private inner class BootstrapperImpl(
        private val city: City
    ): CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.WeatherStartLoading)
            scope.launch {
                try {
                    val weather = getWeatherForCityUseCase(city)
                    dispatch(Action.WeatherLoaded(weather))
                } catch (e: Exception) {
                    dispatch(Action.WeatherLoadingError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when(intent) {
                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }
                Intent.ClickUpdate -> {
                    dispatch(Msg.WeatherStartLoading)
                    scope.launch {
                        val state = state()
                        try {
                            val weather = getWeatherForCityUseCase(state.city)
                            dispatch(Msg.WeatherLoaded(weather))
                        } catch (e: Exception) {
                            dispatch(Msg.WeatherLoadingError)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                is Action.WeatherLoaded -> dispatch(Msg.WeatherLoaded(action.weather))
                Action.WeatherLoadingError -> dispatch(Msg.WeatherLoadingError)
                Action.WeatherStartLoading -> dispatch(Msg.WeatherStartLoading)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when(msg) {
            is Msg.WeatherLoaded -> {
                copy(weatherState = State.WeatherState.Loaded(msg.weather))
            }
            Msg.WeatherLoadingError -> {
                copy(weatherState = State.WeatherState.Error)
            }
            Msg.WeatherStartLoading -> {
                copy(weatherState = State.WeatherState.Loading)
            }
        }
    }
}
