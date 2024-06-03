package com.example.gazpromtest.presentation.cities

import com.example.gazpromtest.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface CitiesComponent {

    val model: StateFlow<CitiesStore.State>

    fun onClickCity(city: City)

    fun onUpdateRequest()

}