package com.example.weatherapp.repository

import android.content.Context
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.api.RetrofitClient
import com.example.weatherapp.cache.WeatherCache
import com.example.weatherapp.cache.cityToCacheKey
import com.example.weatherapp.cache.coordsToCacheKey
import com.example.weatherapp.models.api.WeatherResponse
import com.example.weatherapp.util.RateLimiter
import com.example.weatherapp.util.WeatherError
import com.example.weatherapp.util.WeatherResult
import com.example.weatherapp.util.toWeatherError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class WeatherRepository(context: Context? = null) {
    private val apiService = RetrofitClient.weatherApiService
    
    // Use BuildConfig for secure API key storage
    private val apiKey: String = if (BuildConfig.WEATHER_API_KEY.isNotEmpty()) {
        BuildConfig.WEATHER_API_KEY
    } else {
        // Fallback for development - remove in production
        "63030200ba49f825a3bd4ab30b8aad49"
    }
    
    // Cache instance (optional - needs context)
    private val cache: WeatherCache? = context?.let { WeatherCache(it) }
    
    // Rate limiter: OpenWeatherMap free tier allows 60 calls/minute
    private val rateLimiter = RateLimiter(maxRequests = 55, windowMs = 60_000L)
    
    /**
     * Get weather by city name with caching and error handling
     */
    suspend fun getWeatherByCity(
        city: String, 
        units: String = "imperial"
    ): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        val cacheKey = cityToCacheKey(city, units)
        
        // Try cache first
        cache?.getCachedWeather(cacheKey)?.let { cached ->
            return@withContext Result.success(cached)
        }
        
        // Check rate limit
        if (!rateLimiter.canMakeRequest()) {
            val waitTime = rateLimiter.getWaitTimeMs()
            if (waitTime > 0) {
                delay(waitTime)
            }
        }
        
        return@withContext try {
            rateLimiter.recordRequest()
            val response = apiService.getCurrentWeather(city, apiKey, units)
            
            // Cache the response
            cache?.cacheWeather(cacheKey, response)
            
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(handleHttpError(e.code())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get weather by coordinates with caching and error handling
     */
    suspend fun getWeatherByCoords(
        lat: Double, 
        lon: Double, 
        units: String = "imperial"
    ): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        val cacheKey = coordsToCacheKey(lat, lon, units)
        
        // Try cache first
        cache?.getCachedWeather(cacheKey)?.let { cached ->
            return@withContext Result.success(cached)
        }
        
        // Check rate limit
        if (!rateLimiter.canMakeRequest()) {
            val waitTime = rateLimiter.getWaitTimeMs()
            if (waitTime > 0) {
                delay(waitTime)
            }
        }
        
        return@withContext try {
            rateLimiter.recordRequest()
            val response = apiService.getWeatherByCoords(lat, lon, apiKey, units)
            
            // Cache the response
            cache?.cacheWeather(cacheKey, response)
            
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(handleHttpError(e.code())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get weather with detailed result type
     */
    suspend fun getWeatherWithResult(
        city: String,
        units: String = "imperial"
    ): WeatherResult<WeatherResponse> = withContext(Dispatchers.IO) {
        val cacheKey = cityToCacheKey(city, units)
        
        // Try cache first
        cache?.getCachedWeather(cacheKey)?.let { cached ->
            return@withContext WeatherResult.Success(cached, fromCache = true)
        }
        
        // Check rate limit
        if (!rateLimiter.canMakeRequest()) {
            return@withContext WeatherResult.Error(WeatherError.RateLimitExceeded)
        }
        
        return@withContext try {
            rateLimiter.recordRequest()
            val response = apiService.getCurrentWeather(city, apiKey, units)
            cache?.cacheWeather(cacheKey, response)
            WeatherResult.Success(response, fromCache = false)
        } catch (e: HttpException) {
            WeatherResult.Error(httpCodeToError(e.code()))
        } catch (e: Exception) {
            WeatherResult.Error(e.toWeatherError())
        }
    }
    
    /**
     * Force refresh - bypass cache
     */
    suspend fun forceRefresh(city: String, units: String = "imperial"): Result<WeatherResponse> =
        withContext(Dispatchers.IO) {
            if (!rateLimiter.canMakeRequest()) {
                return@withContext Result.failure(Exception("Rate limit exceeded. Please wait."))
            }
            
            try {
                rateLimiter.recordRequest()
                val response = apiService.getCurrentWeather(city, apiKey, units)
                val cacheKey = cityToCacheKey(city, units)
                cache?.cacheWeather(cacheKey, response)
                Result.success(response)
            } catch (e: HttpException) {
                Result.failure(Exception(handleHttpError(e.code())))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Clear the weather cache
     */
    suspend fun clearCache() {
        cache?.clearCache()
    }
    
    private fun handleHttpError(code: Int): String {
        return when (code) {
            401 -> "Invalid API key"
            404 -> "City not found"
            429 -> "Too many requests. Please wait a moment."
            500, 502, 503 -> "Server error. Please try again later."
            else -> "Error: HTTP $code"
        }
    }
    
    private fun httpCodeToError(code: Int): WeatherError {
        return when (code) {
            401 -> WeatherError.InvalidApiKey
            404 -> WeatherError.CityNotFound
            429 -> WeatherError.RateLimitExceeded
            500, 502, 503 -> WeatherError.ServerError
            else -> WeatherError.Unknown("HTTP Error: $code")
        }
    }
}
