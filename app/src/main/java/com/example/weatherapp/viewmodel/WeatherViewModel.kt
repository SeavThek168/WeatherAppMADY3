package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.*
import com.example.weatherapp.models.api.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WeatherUiState(
    val weatherData: WeatherData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCelsius: Boolean = false
)

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState
    
    private var lastCity: String = "Phnom Penh"
    
    init {
        searchWeatherByCity(lastCity)
    }
    
    fun toggleTemperatureUnit() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isCelsius = !currentState.isCelsius)
        // Refresh with new unit
        searchWeatherByCity(lastCity)
    }
    
    fun searchWeatherByCity(city: String) {
        lastCity = city
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val units = if (_uiState.value.isCelsius) "metric" else "imperial"
            val result = repository.getWeatherByCity(city, units)
            
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    weatherData = convertToWeatherData(response),
                    isLoading = false
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to fetch weather"
                )
            }
        }
    }
    
    private fun convertToWeatherData(response: WeatherResponse): WeatherData {
        val condition = response.weather.firstOrNull()?.main ?: "Clear"
        val description = response.weather.firstOrNull()?.description ?: ""
        
        return WeatherData(
            location = "${response.name}, ${response.sys.country}",
            currentTemp = response.main.temp.toInt(),
            condition = description.replaceFirstChar { it.uppercase() },
            highTemp = response.main.temp_max.toInt(),
            lowTemp = response.main.temp_min.toInt(),
            feelsLike = response.main.feels_like.toInt(),
            humidity = response.main.humidity,
            windSpeed = response.wind.speed.toInt(),
            airQuality = AirQuality(
                aqi = 42,
                quality = "Good",
                pm25 = 12.5,
                pm10 = 18.3
            ),
            uvIndex = UVIndex(
                index = 6,
                level = "Moderate",
                peakTime = "12:00 PM"
            ),
            sunriseSunset = SunriseSunset(
                sunrise = formatTime(response.sys.sunrise),
                sunset = formatTime(response.sys.sunset),
                dayLength = calculateDayLength(response.sys.sunrise, response.sys.sunset)
            ),
            hourlyForecast = generateHourlyForecast(response.main.temp.toInt()),
            fiveDayForecast = generateFiveDayForecast(response.main.temp.toInt())
        )
    }
    
    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    
    private fun calculateDayLength(sunrise: Long, sunset: Long): String {
        val diff = sunset - sunrise
        val hours = diff / 3600
        val minutes = (diff % 3600) / 60
        return "${hours}h ${minutes}m"
    }
    
    private fun generateHourlyForecast(baseTemp: Int): List<HourlyForecast> {
        val hours = listOf("Now", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM")
        return hours.mapIndexed { index, hour ->
            HourlyForecast(
                hour,
                baseTemp + (index - 1),
                if (index < 4) "Sunny" else "Clear",
                if (index < 4) "☀️" else "🌙"
            )
        }
    }
    
    private fun generateFiveDayForecast(baseTemp: Int): List<DailyForecast> {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        val dates = listOf("Dec 9", "Dec 10", "Dec 11", "Dec 12", "Dec 13")
        val conditions = listOf("Partly Cloudy", "Sunny", "Cloudy", "Rainy", "Clear")
        val icons = listOf("⛅", "☀️", "☁️", "🌧️", "🌤️")
        val rainChances = listOf(10, 0, 30, 80, 5)
        
        return days.indices.map { i ->
            DailyForecast(
                days[i],
                dates[i],
                baseTemp + (2 - i),
                baseTemp - (5 + i),
                conditions[i],
                icons[i],
                rainChances[i]
            )
        }
    }
}
