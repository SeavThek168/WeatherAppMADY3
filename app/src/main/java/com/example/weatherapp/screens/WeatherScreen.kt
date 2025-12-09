package com.example.weatherapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.components.*
import com.example.weatherapp.models.*
import com.example.weatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    val weatherData = uiState.weatherData ?: WeatherData(
            location = "Loading...",
            currentTemp = 72,
            condition = "Partly Cloudy",
            highTemp = 75,
            lowTemp = 65,
            feelsLike = 70,
            humidity = 65,
            windSpeed = 12,
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
                sunrise = "6:45 AM",
                sunset = "7:30 PM",
                dayLength = "12h 45m"
            ),
            hourlyForecast = listOf(
                HourlyForecast("Now", 72, "Cloudy", "☁️"),
                HourlyForecast("1 PM", 73, "Sunny", "☀️"),
                HourlyForecast("2 PM", 74, "Sunny", "☀️"),
                HourlyForecast("3 PM", 75, "Clear", "🌤️"),
                HourlyForecast("4 PM", 74, "Clear", "🌤️"),
                HourlyForecast("5 PM", 72, "Cloudy", "☁️"),
                HourlyForecast("6 PM", 70, "Cloudy", "☁️"),
                HourlyForecast("7 PM", 68, "Clear", "🌙")
            ),
            fiveDayForecast = listOf(
                DailyForecast("Monday", "Dec 9", 75, 65, "Partly Cloudy", "⛅", 10),
                DailyForecast("Tuesday", "Dec 10", 77, 66, "Sunny", "☀️", 0),
                DailyForecast("Wednesday", "Dec 11", 73, 64, "Cloudy", "☁️", 30),
                DailyForecast("Thursday", "Dec 12", 70, 62, "Rainy", "🌧️", 80),
                DailyForecast("Friday", "Dec 13", 72, 63, "Clear", "🌤️", 5)
            )
        )
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearch = { 
                    if (searchQuery.isNotBlank()) {
                        viewModel.searchWeatherByCity(searchQuery)
                    }
                },
                onLocationClick = { 
                    // For now, search Phnom Penh as default location
                    viewModel.searchWeatherByCity("Phnom Penh")
                },
                isCelsius = uiState.isCelsius,
                onToggleUnit = { viewModel.toggleTemperatureUnit() }
            )
            
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Current Weather
            if (!uiState.isLoading) {
                CurrentWeatherCard(weatherData = weatherData)
        
        // Hourly Forecast
        HourlyForecastSection(hourlyForecasts = weatherData.hourlyForecast)
        
        // 5-Day Forecast
        FiveDayForecastSection(dailyForecasts = weatherData.fiveDayForecast)
        
        // Air Quality
        AirQualityCard(airQuality = weatherData.airQuality)
        
        // UV Index
        UVIndexCard(uvIndex = weatherData.uvIndex)
        
        // Sunrise & Sunset
        SunriseSunsetCard(sunriseSunset = weatherData.sunriseSunset)
        
                // Weather Map
                WeatherMapCard()
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
