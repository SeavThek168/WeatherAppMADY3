package com.example.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.location.LocationManager
import com.example.weatherapp.models.*
import com.example.weatherapp.models.api.AirPollutionResponse
import com.example.weatherapp.models.api.ForecastResponse
import com.example.weatherapp.models.api.WeatherResponse
import com.example.weatherapp.repository.CombinedWeatherData
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.toWeatherError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WeatherUiState(
    val weatherData: WeatherData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCelsius: Boolean = true,
    val currentLat: Double? = null,
    val currentLon: Double? = null,
    val isFromCache: Boolean = false,
    val lastUpdated: String? = null,
    val isRefreshing: Boolean = false
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    // Repository with context for caching
    private val repository = WeatherRepository(application.applicationContext)
    
    // Location manager for device location
    private val locationManager = LocationManager(application.applicationContext)
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState
    
    private var lastCity: String = "Phnom Penh"
    private var searchJob: Job? = null
    
    // Debounce delay for search
    private val searchDebounceMs = 500L
    
    init {
        // Try to get device location first, fallback to default city
        viewModelScope.launch {
            tryGetDeviceLocation()
        }
    }
    
    /**
     * Try to get weather for device location
     */
    private suspend fun tryGetDeviceLocation() {
        if (locationManager.hasLocationPermission()) {
            try {
                // Use the improved getBestLocation method
                val location = locationManager.getBestLocation()
                
                if (location != null) {
                    android.util.Log.d("WeatherViewModel", 
                        "Got device location: ${location.latitude}, ${location.longitude}")
                    searchWeatherByCoords(location.latitude, location.longitude)
                    return
                } else {
                    android.util.Log.w("WeatherViewModel", "Location was null, using default city")
                }
            } catch (e: Exception) {
                android.util.Log.e("WeatherViewModel", "Location error: ${e.message}")
                // Fall through to default city
            }
        } else {
            android.util.Log.w("WeatherViewModel", "No location permission, using default city")
        }
        // Fallback to default city
        searchWeatherByCity(lastCity)
    }
    
    /**
     * Refresh weather using device location
     */
    fun refreshWithDeviceLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            tryGetDeviceLocation()
        }
    }
    
    fun toggleTemperatureUnit() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isCelsius = !currentState.isCelsius)
        // Refresh with new unit
        if (currentState.currentLat != null && currentState.currentLon != null) {
            searchWeatherByCoords(currentState.currentLat, currentState.currentLon)
        } else {
            searchWeatherByCity(lastCity)
        }
    }
    
    /**
     * Search with debouncing to prevent rapid API calls
     */
    fun searchWeatherByCityDebounced(city: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(searchDebounceMs)
            searchWeatherByCity(city)
        }
    }
    
    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a city name"
            )
            return
        }
        
        lastCity = city
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val units = if (_uiState.value.isCelsius) "metric" else "imperial"
            val result = repository.getCompleteWeatherByCity(city, units)
            
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    weatherData = convertToWeatherData(data, _uiState.value.isCelsius),
                    isLoading = false,
                    currentLat = data.current.coord.lat,
                    currentLon = data.current.coord.lon,
                    lastUpdated = formatLastUpdated(),
                    error = null
                )
            }.onFailure { exception ->
                val errorMessage = exception.toWeatherError().message
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }
    
    fun searchWeatherByCoords(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val units = if (_uiState.value.isCelsius) "metric" else "imperial"
            val result = repository.getCompleteWeatherByCoords(lat, lon, units)
            
            result.onSuccess { data ->
                lastCity = data.current.name
                _uiState.value = _uiState.value.copy(
                    weatherData = convertToWeatherData(data, _uiState.value.isCelsius),
                    isLoading = false,
                    currentLat = lat,
                    currentLon = lon,
                    lastUpdated = formatLastUpdated(),
                    error = null
                )
            }.onFailure { exception ->
                val errorMessage = exception.toWeatherError().message
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }
    
    /**
     * Force refresh - bypass cache
     */
    fun forceRefresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            
            val units = if (_uiState.value.isCelsius) "metric" else "imperial"
            val lat = _uiState.value.currentLat
            val lon = _uiState.value.currentLon
            
            val result = if (lat != null && lon != null) {
                repository.getCompleteWeatherByCoords(lat, lon, units)
            } else {
                repository.getCompleteWeatherByCity(lastCity, units)
            }
            
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    weatherData = convertToWeatherData(data, _uiState.value.isCelsius),
                    isRefreshing = false,
                    currentLat = data.current.coord.lat,
                    currentLon = data.current.coord.lon,
                    lastUpdated = formatLastUpdated(),
                    isFromCache = false,
                    error = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = exception.toWeatherError().message
                )
            }
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Check if location permission is available
     */
    fun hasLocationPermission(): Boolean = locationManager.hasLocationPermission()
    
    private fun formatLastUpdated(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Convert combined API data to WeatherData model
     */
    private fun convertToWeatherData(data: CombinedWeatherData, isCelsius: Boolean): WeatherData {
        val current = data.current
        val forecast = data.forecast
        val airPollution = data.airPollution
        
        val condition = current.weather.firstOrNull()?.main ?: "Clear"
        val description = current.weather.firstOrNull()?.description ?: ""
        val iconCode = current.weather.firstOrNull()?.icon ?: "01d"
        
        // Calculate daily high/low from forecast if available
        val (dailyHigh, dailyLow) = calculateDailyHighLow(current, forecast)
        
        // Get wind speed with proper unit display
        // OpenWeatherMap returns: metric = m/s, imperial = mph
        val windSpeedValue = current.wind.speed
        val windSpeedDisplay = if (isCelsius) {
            // Convert m/s to km/h for display
            (windSpeedValue * 3.6).toInt()
        } else {
            windSpeedValue.toInt()
        }
        
        return WeatherData(
            location = "${current.name}, ${current.sys.country}",
            country = current.sys.country,
            currentTemp = current.main.temp.toInt(),
            condition = description.replaceFirstChar { it.uppercase() },
            highTemp = dailyHigh,
            lowTemp = dailyLow,
            feelsLike = current.main.feels_like.toInt(),
            humidity = current.main.humidity,
            windSpeed = windSpeedDisplay,
            pressure = current.main.pressure,
            visibility = (current.visibility ?: 10000) / 1000.0,
            airQuality = convertAirQuality(airPollution),
            uvIndex = UVIndex(
                index = estimateUVIndex(current),
                level = getUVLevel(estimateUVIndex(current)),
                peakTime = "12:00 PM"
            ),
            sunriseSunset = SunriseSunset(
                sunrise = formatTime(current.sys.sunrise, current.timezone ?: 0),
                sunset = formatTime(current.sys.sunset, current.timezone ?: 0),
                dayLength = calculateDayLength(current.sys.sunrise, current.sys.sunset)
            ),
            hourlyForecast = convertHourlyForecast(forecast, isCelsius),
            fiveDayForecast = convertDailyForecast(forecast, isCelsius)
        )
    }
    
    /**
     * Calculate daily high/low from forecast data
     */
    private fun calculateDailyHighLow(current: WeatherResponse, forecast: ForecastResponse?): Pair<Int, Int> {
        if (forecast == null || forecast.list.isEmpty()) {
            // Fallback to current temp with estimation
            val currentTemp = current.main.temp.toInt()
            return Pair(currentTemp + 2, currentTemp - 4)
        }
        
        // Get today's date
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Filter forecast items for today
        val todayForecasts = forecast.list.filter { item ->
            item.dt_txt.startsWith(today)
        }
        
        if (todayForecasts.isEmpty()) {
            // Use first day's forecasts
            val firstDayForecasts = forecast.list.take(8)
            val high = firstDayForecasts.maxOfOrNull { it.main.temp_max }?.toInt() ?: current.main.temp.toInt()
            val low = firstDayForecasts.minOfOrNull { it.main.temp_min }?.toInt() ?: current.main.temp.toInt()
            return Pair(high, low)
        }
        
        val high = todayForecasts.maxOfOrNull { it.main.temp_max }?.toInt() ?: current.main.temp.toInt()
        val low = todayForecasts.minOfOrNull { it.main.temp_min }?.toInt() ?: current.main.temp.toInt()
        
        return Pair(high, low)
    }
    
    /**
     * Convert API air pollution data to our model
     */
    private fun convertAirQuality(airPollution: AirPollutionResponse?): AirQuality {
        if (airPollution == null || airPollution.list.isEmpty()) {
            return AirQuality() // Default values
        }
        
        val data = airPollution.list.first()
        val aqi = data.main.aqi
        
        val qualityText = when (aqi) {
            1 -> "Good"
            2 -> "Fair"
            3 -> "Moderate"
            4 -> "Poor"
            5 -> "Very Poor"
            else -> "Unknown"
        }
        
        return AirQuality(
            aqi = aqi,
            quality = qualityText,
            pm25 = data.components.pm2_5,
            pm10 = data.components.pm10,
            ozone = data.components.o3,
            no2 = data.components.no2
        )
    }
    
    /**
     * Convert forecast to hourly format (next 8 items = 24 hours)
     */
    private fun convertHourlyForecast(forecast: ForecastResponse?, isCelsius: Boolean): List<HourlyForecast> {
        if (forecast == null || forecast.list.isEmpty()) {
            return emptyList()
        }
        
        val timeFormat = SimpleDateFormat("h a", Locale.getDefault())
        
        return forecast.list.take(8).mapIndexed { index, item ->
            val time = if (index == 0) "Now" else timeFormat.format(Date(item.dt * 1000))
            val icon = getWeatherEmoji(item.weather.firstOrNull()?.id ?: 800, item.sys.pod == "d")
            val windSpeed = if (isCelsius) (item.wind.speed * 3.6) else item.wind.speed
            
            HourlyForecast(
                time = time,
                temp = item.main.temp.toInt(),
                condition = item.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                icon = icon,
                humidity = item.main.humidity,
                windSpeed = windSpeed
            )
        }
    }
    
    /**
     * Convert forecast to daily format
     * Group 3-hourly forecasts by day and get high/low
     */
    private fun convertDailyForecast(forecast: ForecastResponse?, isCelsius: Boolean): List<DailyForecast> {
        if (forecast == null || forecast.list.isEmpty()) {
            return emptyList()
        }
        
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
        val groupFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // Group forecasts by day
        val dailyGroups = forecast.list.groupBy { item ->
            groupFormat.format(Date(item.dt * 1000))
        }
        
        return dailyGroups.entries.take(5).map { (_, items) ->
            val firstItem = items.first()
            val date = Date(firstItem.dt * 1000)
            val highTemp = items.maxOfOrNull { it.main.temp_max }?.toInt() ?: 0
            val lowTemp = items.minOfOrNull { it.main.temp_min }?.toInt() ?: 0
            
            // Get the most common weather condition for the day
            val mainCondition = items
                .mapNotNull { it.weather.firstOrNull() }
                .groupBy { it.main }
                .maxByOrNull { it.value.size }
                ?.key ?: "Clear"
            
            val conditionId = items.firstOrNull { it.weather.firstOrNull()?.main == mainCondition }
                ?.weather?.firstOrNull()?.id ?: 800
            
            // Calculate average precipitation probability
            val avgPop = (items.mapNotNull { it.pop }.average() * 100).toInt()
            
            // Calculate average wind speed
            val avgWind = items.map { it.wind.speed }.average()
            val windDisplay = if (isCelsius) avgWind * 3.6 else avgWind
            
            DailyForecast(
                day = dayFormat.format(date),
                date = dateFormat.format(date),
                highTemp = highTemp,
                lowTemp = lowTemp,
                condition = mainCondition,
                icon = getWeatherEmoji(conditionId, true),
                precipChance = avgPop,
                windSpeed = windDisplay
            )
        }
    }
    
    /**
     * Get weather emoji based on condition code
     * https://openweathermap.org/weather-conditions
     */
    private fun getWeatherEmoji(conditionId: Int, isDay: Boolean): String {
        return when (conditionId) {
            in 200..232 -> "⛈️"  // Thunderstorm
            in 300..321 -> "🌧️"  // Drizzle
            in 500..504 -> "🌧️"  // Rain
            511 -> "🌨️"         // Freezing rain
            in 520..531 -> "🌧️"  // Shower rain
            in 600..622 -> "❄️"  // Snow
            in 701..781 -> "🌫️"  // Atmosphere (fog, mist, etc.)
            800 -> if (isDay) "☀️" else "🌙"  // Clear
            801 -> if (isDay) "🌤️" else "☁️"  // Few clouds
            802 -> "⛅"          // Scattered clouds
            803, 804 -> "☁️"    // Broken/Overcast clouds
            else -> "🌤️"
        }
    }
    
    /**
     * Estimate UV Index based on weather conditions and time
     * (OpenWeatherMap free tier doesn't include UV data)
     */
    private fun estimateUVIndex(current: WeatherResponse): Int {
        val clouds = current.clouds.all
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        
        // Base UV by time of day (tropical latitude like Cambodia)
        val baseUV = when (hour) {
            in 6..7 -> 2
            in 8..9 -> 4
            in 10..11 -> 7
            in 12..14 -> 9
            in 15..16 -> 6
            in 17..18 -> 3
            else -> 0
        }
        
        // Reduce by cloud cover
        val cloudReduction = (clouds / 100.0 * 0.5).coerceIn(0.0, 0.5)
        return ((baseUV * (1 - cloudReduction)).toInt()).coerceIn(0, 11)
    }
    
    private fun getUVLevel(uv: Int): String {
        return when (uv) {
            in 0..2 -> "Low"
            in 3..5 -> "Moderate"
            in 6..7 -> "High"
            in 8..10 -> "Very High"
            else -> "Extreme"
        }
    }
    
    private fun formatTime(timestamp: Long, timezoneOffset: Int): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        // Add timezone offset
        val adjustedTime = (timestamp + timezoneOffset) * 1000
        return sdf.format(Date(adjustedTime))
    }
    
    private fun calculateDayLength(sunrise: Long, sunset: Long): String {
        val diff = sunset - sunrise
        val hours = diff / 3600
        val minutes = (diff % 3600) / 60
        return "${hours}h ${minutes}m"
    }
}
