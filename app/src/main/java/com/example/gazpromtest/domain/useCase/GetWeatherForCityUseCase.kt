package com.example.gazpromtest.domain.useCase

import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.repository.MainRepository
import javax.inject.Inject

class GetWeatherForCityUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(city: City) = repository.getWeatherForChosenCity(city)

}