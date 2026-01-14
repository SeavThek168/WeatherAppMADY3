package com.example.weatherapp.api

import com.example.weatherapp.models.api.AirPollutionResponse
import com.example.weatherapp.models.api.ForecastResponse
import com.example.weatherapp.models.api.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    /**
     * Get current weather data
     * Free tier: Yes
     * Docs: https://openweathermap.org/current
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
    
    @GET("weather")
    suspend fun getWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
    
    /**
     * Get 5-day/3-hour forecast data
     * Free tier: Yes (40 timestamps = 5 days x 8 per day)
     * Docs: https://openweathermap.org/forecast5
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
    
    @GET("forecast")
    suspend fun getForecastByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
    
    /**
     * Get air pollution data
     * Free tier: Yes
     * Docs: https://openweathermap.org/api/air-pollution
     */
    @GET("air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirPollutionResponse
}
