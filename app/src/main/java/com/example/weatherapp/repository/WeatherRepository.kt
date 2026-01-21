package com.example.weatherapp.repository

import android.content.Context
import android.util.Log
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.api.RetrofitClient
import com.example.weatherapp.cache.WeatherCache
import com.example.weatherapp.cache.cityToCacheKey
import com.example.weatherapp.cache.coordsToCacheKey
import com.example.weatherapp.models.api.*
import com.example.weatherapp.util.RateLimiter
import com.example.weatherapp.util.WeatherError
import com.example.weatherapp.util.WeatherResult
import com.example.weatherapp.util.toWeatherError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Combined weather data from multiple API endpoints
 */
data class CombinedWeatherData(
    val current: WeatherResponse,
    val forecast: ForecastResponse?,
    val airPollution: AirPollutionResponse?
)

class WeatherRepository(context: Context? = null) {
    private val apiService = RetrofitClient.weatherApiService
    
    // Use BuildConfig for secure API key storage
    private val apiKey: String = if (BuildConfig.WEATHER_API_KEY.isNotEmpty()) {
        BuildConfig.WEATHER_API_KEY
    } else {
        // Fallback for development - replace with your API key
        "63030200ba49f825a3bd4ab30b8aad49"
    }
    
    // Cache instance (optional - needs context)
    private val cache: WeatherCache? = context?.let { WeatherCache(it) }
    
    // Rate limiter: OpenWeatherMap free tier allows 60 calls/minute
    // We make 3 calls per refresh (current + forecast + air), so limit to ~18 refreshes/min
    private val rateLimiter = RateLimiter(maxRequests = 55, windowMs = 60_000L)
    
    // Set to true to force mock data (for offline testing)
    private var useMockData = false
    
    companion object {
        private const val TAG = "WeatherRepository"
    }
    
    /**
     * Generate mock weather data for demo/offline mode
     */
    private fun generateMockWeather(cityName: String, lat: Double = 11.5564, lon: Double = 104.9282): WeatherResponse {
        val currentTime = System.currentTimeMillis() / 1000
        
        // Generate slightly varied data based on city name hash for variety
        val hash = cityName.hashCode().let { if (it < 0) -it else it }
        val tempVariation = (hash % 10) - 5  // -5 to +4 variation
        val humidityVariation = (hash % 20) - 10  // -10 to +9 variation
        
        val conditions = listOf(
            Triple(800, "Clear", "clear sky"),
            Triple(801, "Clouds", "few clouds"),
            Triple(802, "Clouds", "scattered clouds"),
            Triple(803, "Clouds", "broken clouds"),
            Triple(500, "Rain", "light rain"),
            Triple(701, "Mist", "mist")
        )
        val condition = conditions[hash % conditions.size]
        
        return WeatherResponse(
            coord = Coord(lon = lon, lat = lat),
            weather = listOf(
                Weather(
                    id = condition.first,
                    main = condition.second,
                    description = condition.third,
                    icon = if (condition.first == 800) "01d" else "02d"
                )
            ),
            main = Main(
                temp = (86.0 + tempVariation).coerceIn(75.0, 95.0),  // 75-95°F
                feels_like = (91.4 + tempVariation).coerceIn(80.0, 100.0),
                temp_min = (82.4 + tempVariation).coerceIn(72.0, 90.0),
                temp_max = (89.6 + tempVariation).coerceIn(80.0, 98.0),
                pressure = 1012 + (hash % 10) - 5,
                humidity = (65 + humidityVariation).coerceIn(40, 90)
            ),
            wind = Wind(
                speed = 5.0 + (hash % 10),
                deg = (hash % 360)
            ),
            clouds = Clouds(all = 20 + (hash % 60)),
            sys = Sys(
                country = "KH",
                sunrise = currentTime - 21600,
                sunset = currentTime + 21600
            ),
            name = cityName.split(",").firstOrNull()?.trim() ?: cityName,
            dt = currentTime,
            visibility = 10000
        )
    }
    
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
        
        // If we already know API is unreachable, use mock data
        if (useMockData) {
            Log.d(TAG, "Using mock data for city: $city (offline mode)")
            return@withContext Result.success(generateMockWeather(city))
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
            useMockData = false  // API is working
            
            Result.success(response)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error for city $city: ${e.code()}")
            Result.failure(Exception(handleHttpError(e.code())))
        } catch (e: Exception) {
            // Network error - switch to mock data mode
            Log.e(TAG, "Network error for city $city, switching to mock data: ${e.message}")
            useMockData = true
            Result.success(generateMockWeather(city))
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
        
        // If we already know API is unreachable, use mock data
        if (useMockData) {
            Log.d(TAG, "Using mock data for coords: $lat, $lon (offline mode)")
            return@withContext Result.success(generateMockWeather("Phnom Penh", lat, lon))
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
            useMockData = false  // API is working
            
            Result.success(response)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error for coords $lat, $lon: ${e.code()}")
            Result.failure(Exception(handleHttpError(e.code())))
        } catch (e: Exception) {
            // Network error - switch to mock data mode
            Log.e(TAG, "Network error for coords, switching to mock data: ${e.message}")
            useMockData = true
            Result.success(generateMockWeather("Phnom Penh", lat, lon))
        }
    }
    
    /**
     * Get weather with detailed result type
     */
    suspend fun getWeatherWithResult(
        city: String,
        units: String = "metric"
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
    suspend fun forceRefresh(city: String, units: String = "metric"): Result<WeatherResponse> =
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
     * Get complete weather data including forecast and air quality
     * This fetches all available data from the free tier APIs
     */
    suspend fun getCompleteWeatherByCity(
        city: String,
        units: String = "metric"
    ): Result<CombinedWeatherData> = withContext(Dispatchers.IO) {
        if (useMockData) {
            Log.d(TAG, "Using mock data for complete weather: $city")
            val mockCurrent = generateMockWeather(city)
            return@withContext Result.success(
                CombinedWeatherData(current = mockCurrent, forecast = null, airPollution = null)
            )
        }
        
        // Check rate limit (we'll make up to 3 calls)
        if (!rateLimiter.canMakeRequest()) {
            val waitTime = rateLimiter.getWaitTimeMs()
            if (waitTime > 0) delay(waitTime)
        }
        
        try {
            // Fetch current weather first (required)
            rateLimiter.recordRequest()
            val currentWeather = apiService.getCurrentWeather(city, apiKey, units)
            
            // Fetch forecast and air quality in parallel
            val forecastDeferred = async {
                try {
                    rateLimiter.recordRequest()
                    apiService.getForecast(city, apiKey, units)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch forecast: ${e.message}")
                    null
                }
            }
            
            val airPollutionDeferred = async {
                try {
                    rateLimiter.recordRequest()
                    apiService.getAirPollution(
                        currentWeather.coord.lat,
                        currentWeather.coord.lon,
                        apiKey
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch air pollution: ${e.message}")
                    null
                }
            }
            
            val forecast = forecastDeferred.await()
            val airPollution = airPollutionDeferred.await()
            
            useMockData = false
            Result.success(
                CombinedWeatherData(
                    current = currentWeather,
                    forecast = forecast,
                    airPollution = airPollution
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error: ${e.code()}")
            Result.failure(Exception(handleHttpError(e.code())))
        } catch (e: Exception) {
            Log.e(TAG, "Network error, switching to mock: ${e.message}")
            useMockData = true
            val mockCurrent = generateMockWeather(city)
            Result.success(
                CombinedWeatherData(current = mockCurrent, forecast = null, airPollution = null)
            )
        }
    }
    
    /**
     * Get complete weather data by coordinates
     */
    suspend fun getCompleteWeatherByCoords(
        lat: Double,
        lon: Double,
        units: String = "metric"
    ): Result<CombinedWeatherData> = withContext(Dispatchers.IO) {
        if (useMockData) {
            Log.d(TAG, "Using mock data for coords: $lat, $lon")
            val mockCurrent = generateMockWeather("Location", lat, lon)
            return@withContext Result.success(
                CombinedWeatherData(current = mockCurrent, forecast = null, airPollution = null)
            )
        }
        
        if (!rateLimiter.canMakeRequest()) {
            val waitTime = rateLimiter.getWaitTimeMs()
            if (waitTime > 0) delay(waitTime)
        }
        
        try {
            // Fetch all data in parallel
            val currentDeferred = async {
                rateLimiter.recordRequest()
                apiService.getWeatherByCoords(lat, lon, apiKey, units)
            }
            
            val forecastDeferred = async {
                try {
                    rateLimiter.recordRequest()
                    apiService.getForecastByCoords(lat, lon, apiKey, units)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch forecast: ${e.message}")
                    null
                }
            }
            
            val airPollutionDeferred = async {
                try {
                    rateLimiter.recordRequest()
                    apiService.getAirPollution(lat, lon, apiKey)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch air pollution: ${e.message}")
                    null
                }
            }
            
            val currentWeather = currentDeferred.await()
            val forecast = forecastDeferred.await()
            val airPollution = airPollutionDeferred.await()
            
            useMockData = false
            Result.success(
                CombinedWeatherData(
                    current = currentWeather,
                    forecast = forecast,
                    airPollution = airPollution
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error: ${e.code()}")
            Result.failure(Exception(handleHttpError(e.code())))
        } catch (e: Exception) {
            Log.e(TAG, "Network error, switching to mock: ${e.message}")
            useMockData = true
            val mockCurrent = generateMockWeather("Location", lat, lon)
            Result.success(
                CombinedWeatherData(current = mockCurrent, forecast = null, airPollution = null)
            )
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
