package com.example.weathersnap.data.model

data class GeocodingResponse(
    val results: List<City>?
)

data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
) {
    val displayName: String
        get() = listOfNotNull(name, admin1, country).joinToString(", ")
}