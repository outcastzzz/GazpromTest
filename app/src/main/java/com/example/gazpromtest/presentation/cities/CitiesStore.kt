package com.example.gazpromtest.presentation.cities

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.useCase.GetCitiesUseCase
import com.example.gazpromtest.presentation.cities.CitiesStore.Intent
import com.example.gazpromtest.presentation.cities.CitiesStore.Label
import com.example.gazpromtest.presentation.cities.CitiesStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CitiesStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data class ClickCity(val city: City): Intent

        data object ClickUpdate: Intent

    }

    data class State(
        val citiesState: CitiesState
    ) {

        sealed interface CitiesState {

            data object Loading: CitiesState

            data object Error: CitiesState

            data class Loaded(val cities: List<City>): CitiesState

        }

    }

    sealed interface Label {

        data class ClickCity(val city: City): Label

    }
}

class CitiesStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getCitiesUseCase: GetCitiesUseCase
) {

    fun create(): CitiesStore =
        object : CitiesStore, Store<Intent, State, Label> by storeFactory.create(
            name = "CitiesStore",
            initialState = State(
                citiesState = State.CitiesState.Loading
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class CitiesLoaded(val cities: List<City>): Action

        data object LoadingCitiesError: Action

    }

    private sealed interface Msg {

        data object LoadingCitiesResult: Msg

        data object CitiesResultError: Msg

        data class CitiesResultLoaded(val cities: List<City>): Msg

    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                try {
                    val cities = getCitiesUseCase()
                    dispatch(Action.CitiesLoaded(cities))
                } catch (e: Exception) {
                    dispatch(Action.LoadingCitiesError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when(intent) {
                is Intent.ClickCity -> {
                    publish(Label.ClickCity(intent.city))
                }
                Intent.ClickUpdate -> {
                    dispatch(Msg.LoadingCitiesResult)
                    scope.launch {
                        try {
                            val cities = getCitiesUseCase()
                            dispatch(Msg.CitiesResultLoaded(cities))
                        } catch (e: Exception) {
                            dispatch(Msg.CitiesResultError)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                is Action.CitiesLoaded -> {
                    val cities = action.cities
                    dispatch(Msg.CitiesResultLoaded(cities))
                }
                Action.LoadingCitiesError -> {
                    dispatch(Msg.CitiesResultError)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when(msg) {
            Msg.CitiesResultError -> copy(citiesState = State.CitiesState.Error)
            is Msg.CitiesResultLoaded -> copy(citiesState = State.CitiesState.Loaded(msg.cities))
            Msg.LoadingCitiesResult -> copy(citiesState = State.CitiesState.Loading)
        }
    }
}
