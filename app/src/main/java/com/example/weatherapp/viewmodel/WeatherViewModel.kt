package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.AirQualityResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val weather: WeatherResponse? = null,
    val forecast: ForecastResponse? = null,
    val airQuality: AirQualityResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState
    
    fun searchWeatherByCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val weatherResult = repository.getWeatherByCity(cityName)
            
            weatherResult.onSuccess { weather ->
                _uiState.value = _uiState.value.copy(
                    weather = weather,
                    isLoading = false
                )
                
                // Fetch additional data
                fetchForecastAndAirQuality(weather.coord.lat, weather.coord.lon)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to fetch weather"
                )
            }
        }
    }
    
    fun searchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val weatherResult = repository.getCurrentWeather(lat, lon)
            
            weatherResult.onSuccess { weather ->
                _uiState.value = _uiState.value.copy(
                    weather = weather,
                    isLoading = false
                )
                
                // Fetch additional data
                fetchForecastAndAirQuality(lat, lon)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to fetch weather"
                )
            }
        }
    }
    
    private fun fetchForecastAndAirQuality(lat: Double, lon: Double) {
        viewModelScope.launch {
            val forecastResult = repository.getForecast(lat, lon)
            val airQualityResult = repository.getAirQuality(lat, lon)
            
            forecastResult.onSuccess { forecast ->
                _uiState.value = _uiState.value.copy(forecast = forecast)
            }
            
            airQualityResult.onSuccess { airQuality ->
                _uiState.value = _uiState.value.copy(airQuality = airQuality)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
