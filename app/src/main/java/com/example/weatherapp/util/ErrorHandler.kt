package com.example.weatherapp.util

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Sealed class representing different types of errors
 */
sealed class WeatherError(val message: String) {
    object NoInternet : WeatherError("No internet connection. Please check your network settings.")
    object Timeout : WeatherError("Request timed out. Please try again.")
    object ServerError : WeatherError("Server error. Please try again later.")
    object CityNotFound : WeatherError("City not found. Please check the spelling and try again.")
    object RateLimitExceeded : WeatherError("Too many requests. Please wait a moment before trying again.")
    object InvalidApiKey : WeatherError("API key error. Please contact support.")
    data class Unknown(val errorMessage: String) : WeatherError(errorMessage)
}

/**
 * Convert exception to user-friendly WeatherError
 */
fun Throwable.toWeatherError(): WeatherError {
    return when (this) {
        is UnknownHostException -> WeatherError.NoInternet
        is SocketTimeoutException -> WeatherError.Timeout
        is IOException -> WeatherError.NoInternet
        else -> {
            val msg = message ?: "Unknown error"
            when {
                msg.contains("404") -> WeatherError.CityNotFound
                msg.contains("401") -> WeatherError.InvalidApiKey
                msg.contains("429") -> WeatherError.RateLimitExceeded
                msg.contains("500") || msg.contains("502") || msg.contains("503") -> 
                    WeatherError.ServerError
                else -> WeatherError.Unknown(msg)
            }
        }
    }
}

/**
 * Result wrapper with better error handling
 */
sealed class WeatherResult<out T> {
    data class Success<T>(val data: T, val fromCache: Boolean = false) : WeatherResult<T>()
    data class Error(val error: WeatherError) : WeatherResult<Nothing>()
    object Loading : WeatherResult<Nothing>()
}

/**
 * Rate limiter to prevent too many API calls
 */
class RateLimiter(
    private val maxRequests: Int = 60,
    private val windowMs: Long = 60_000L // 1 minute window
) {
    private val requestTimestamps = mutableListOf<Long>()
    
    @Synchronized
    fun canMakeRequest(): Boolean {
        val now = System.currentTimeMillis()
        // Remove old timestamps
        requestTimestamps.removeAll { now - it > windowMs }
        return requestTimestamps.size < maxRequests
    }
    
    @Synchronized
    fun recordRequest() {
        requestTimestamps.add(System.currentTimeMillis())
    }
    
    @Synchronized
    fun getWaitTimeMs(): Long {
        if (canMakeRequest()) return 0
        val oldestRequest = requestTimestamps.firstOrNull() ?: return 0
        return windowMs - (System.currentTimeMillis() - oldestRequest)
    }
}
