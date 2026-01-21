package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.AirQualityResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
    
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
    
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
    
    @GET("air_pollution")
    suspend fun getAirQuality(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponse
}
