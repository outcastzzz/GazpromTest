package com.example.gazpromtest.presentation.cities

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.presentation.utils.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitiesComponentImpl @AssistedInject constructor(
    private val storeFactory: CitiesStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext,
    @Assisted("onForecastForCityRequested") private val onForecastForCityRequested: (City) -> Unit,
) : CitiesComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when(it) {
                    is CitiesStore.Label.ClickCity -> onForecastForCityRequested(it.city)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<CitiesStore.State> = store.stateFlow

    override fun onClickCity(city: City) {
        store.accept(CitiesStore.Intent.ClickCity(city))
    }

    override fun onUpdateRequest() {
        store.accept(CitiesStore.Intent.ClickUpdate)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext,
            @Assisted("onForecastForCityRequested") onForecastForCityRequested: (City) -> Unit,
        ): CitiesComponentImpl
    }

}