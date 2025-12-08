package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.model.AirQualityResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse

class WeatherRepository {
    private val apiService = RetrofitClient.weatherApiService
    
    // Replace with your actual API key from https://openweathermap.org/api
    private val API_KEY = "63030200ba49f825a3bd4ab30b8aad49"
    
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeather(lat, lon, API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> 
                    "No internet connection. Please check your network."
                e.message?.contains("timeout") == true -> 
                    "Connection timeout. Please try again."
                else -> "Failed to fetch weather: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeatherByCity(cityName, API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> 
                    "No internet connection. Please check your network."
                e.message?.contains("timeout") == true -> 
                    "Connection timeout. Please try again."
                e.message?.contains("404") == true -> 
                    "City not found. Please check spelling."
                else -> "Failed to fetch weather: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    suspend fun getForecast(lat: Double, lon: Double): Result<ForecastResponse> {
        return try {
            val response = apiService.getForecast(lat, lon, API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAirQuality(lat: Double, lon: Double): Result<AirQualityResponse> {
        return try {
            val response = apiService.getAirQuality(lat, lon, API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
