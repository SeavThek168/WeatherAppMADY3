package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitClient
import com.example.weatherapp.models.api.WeatherResponse

class WeatherRepository {
    private val apiService = RetrofitClient.weatherApiService
    private val apiKey = "63030200ba49f825a3bd4ab30b8aad49"
    
    suspend fun getWeatherByCity(city: String, units: String = "imperial"): Result<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeather(city, apiKey, units)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWeatherByCoords(lat: Double, lon: Double, units: String = "imperial"): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeatherByCoords(lat, lon, apiKey, units)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
