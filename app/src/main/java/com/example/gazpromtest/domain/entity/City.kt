package com.example.gazpromtest.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: String? = "без идентефикатора",
    var city: String? = "без названия",
    val latitude: String,
    val longitude: String
)
