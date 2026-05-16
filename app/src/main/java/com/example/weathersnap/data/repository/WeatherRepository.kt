package com.example.weathersnap.data.repository

import com.example.weathersnap.data.local.WeatherDao
import com.example.weathersnap.data.local.WeatherReport
import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherResponse
import com.example.weathersnap.data.remote.WeatherApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) {
    private val cityCache = mutableMapOf<String, List<City>>()

    suspend fun searchCity(query: String): List<City> {
        if (cityCache.containsKey(query)) {
            return cityCache[query]!!
        }
        return try {
            val response = apiService.searchCity(query)
            val cities = response.results ?: emptyList()
            cityCache[query] = cities
            cities
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse {
        return apiService.getWeather(latitude, longitude)
    }

    suspend fun saveReport(report: WeatherReport) {
        weatherDao.insertReport(report)
    }

    fun getAllReports(): Flow<List<WeatherReport>> {
        return weatherDao.getAllReports()
    }
}