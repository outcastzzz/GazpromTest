package com.example.gazpromtest.data.repository

import android.util.Log
import com.example.gazpromtest.BuildConfig
import com.example.gazpromtest.data.network.ApiRoutes
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.entity.Weather
import com.example.gazpromtest.domain.repository.MainRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : MainRepository {

    override suspend fun getCities(): List<City> {
        val response = client.get {
            url(ApiRoutes.CITIES_ROUTE)
            contentType(ContentType.Text.Plain)
        }
        return response.body<List<City>>()
    }

    override suspend fun getWeatherForChosenCity(city: City): Weather {
        val response = client.get("https://api.openweathermap.org/data/2.5/weather") {
            url {
                parameters.append("lat", city.latitude)
                parameters.append("lon", city.longitude)
                parameters.append("appid", BuildConfig.apiKey)
            }
            contentType(ContentType.Application.Json)
        }
        return response.body<Weather>()
    }
}