package com.example.gazpromtest.domain.repository

import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.entity.Weather

interface MainRepository {

    suspend fun getCities(): List<City>

    suspend fun getWeatherForChosenCity(city: City): Weather

}