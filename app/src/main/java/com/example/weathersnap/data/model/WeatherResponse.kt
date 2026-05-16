package com.example.weathersnap.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather?,
    @SerializedName("hourly")
    val hourly: Hourly?
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    @SerializedName("weathercode")
    val weatherCode: Int,
    val time: String
)

data class Hourly(
    val time: List<String>,
    @SerializedName("relative_humidity_2m")
    val humidity: List<Double>,
    @SerializedName("surface_pressure")
    val pressure: List<Double>
)