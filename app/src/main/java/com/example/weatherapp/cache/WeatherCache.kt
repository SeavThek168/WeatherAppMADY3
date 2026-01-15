package com.example.weatherapp.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.models.api.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_cache")

/**
 * Weather cache manager using DataStore
 * Caches weather data for 10 minutes to reduce API calls
 */
class WeatherCache(private val context: Context) {
    
    private val gson = Gson()
    
    companion object {
        private const val CACHE_DURATION_MS = 10 * 60 * 1000L // 10 minutes
        private val WEATHER_DATA_KEY = stringPreferencesKey("weather_data")
        private val CACHE_TIMESTAMP_KEY = longPreferencesKey("cache_timestamp")
        private val CACHE_KEY_PREFIX = stringPreferencesKey("cache_key")
    }
    
    /**
     * Cache weather response with location key
     */
    suspend fun cacheWeather(key: String, response: WeatherResponse) {
        context.dataStore.edit { preferences ->
            preferences[WEATHER_DATA_KEY] = gson.toJson(response)
            preferences[CACHE_TIMESTAMP_KEY] = System.currentTimeMillis()
            preferences[CACHE_KEY_PREFIX] = key
        }
    }
    
    /**
     * Get cached weather if valid (not expired and same location)
     */
    suspend fun getCachedWeather(key: String): WeatherResponse? {
        return try {
            val preferences = context.dataStore.data.first()
            val cachedKey = preferences[CACHE_KEY_PREFIX]
            val timestamp = preferences[CACHE_TIMESTAMP_KEY] ?: 0L
            val weatherJson = preferences[WEATHER_DATA_KEY]
            
            // Check if cache is valid
            if (cachedKey == key && 
                System.currentTimeMillis() - timestamp < CACHE_DURATION_MS &&
                weatherJson != null) {
                gson.fromJson(weatherJson, WeatherResponse::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if cache is valid for given key
     */
    suspend fun isCacheValid(key: String): Boolean {
        return try {
            val preferences = context.dataStore.data.first()
            val cachedKey = preferences[CACHE_KEY_PREFIX]
            val timestamp = preferences[CACHE_TIMESTAMP_KEY] ?: 0L
            
            cachedKey == key && System.currentTimeMillis() - timestamp < CACHE_DURATION_MS
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Clear all cached data
     */
    suspend fun clearCache() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Get cache age in minutes
     */
    suspend fun getCacheAgeMinutes(): Int {
        return try {
            val preferences = context.dataStore.data.first()
            val timestamp = preferences[CACHE_TIMESTAMP_KEY] ?: 0L
            ((System.currentTimeMillis() - timestamp) / 60000).toInt()
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }
}

/**
 * Generate cache key from city name
 */
fun cityToCacheKey(city: String, units: String): String = "city:${city.lowercase()}:$units"

/**
 * Generate cache key from coordinates
 */
fun coordsToCacheKey(lat: Double, lon: Double, units: String): String = 
    "coords:${String.format("%.2f", lat)}:${String.format("%.2f", lon)}:$units"
