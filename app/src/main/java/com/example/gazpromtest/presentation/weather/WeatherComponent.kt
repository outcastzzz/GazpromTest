package com.example.gazpromtest.presentation.weather

import kotlinx.coroutines.flow.StateFlow

interface WeatherComponent {

    val model: StateFlow<WeatherStore.State>

    fun onClickBack()

    fun onUpdateRequest()

}