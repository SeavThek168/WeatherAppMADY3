package com.example.weatherapp.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.components.*
import com.example.weatherapp.models.*
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng

// Background color
private val BackgroundLight = Color(0xFFF8F9FE)

/**
 * Parse location string to extract city and country
 */
private fun parseLocation(location: String): Pair<String, String> {
    val parts = location.split(",").map { it.trim() }
    return when {
        parts.size >= 2 -> Pair(parts[0], parts.subList(1, parts.size).joinToString(", "))
        parts.isNotEmpty() -> Pair(parts[0], "KH")
        else -> Pair("Phnom Penh", "KH")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    // Location permissions state
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    
    // Request location when permissions are granted
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.refreshWithDeviceLocation()
        }
    }
    
    // Default weather data for Cambodia
    val weatherData = uiState.weatherData ?: WeatherData(
            location = "Phnom Penh, KH",
            country = "Cambodia",
            currentTemp = 29,
            condition = "Overcast Clouds",
            highTemp = 29,
            lowTemp = 29,
            feelsLike = 31,
            humidity = 59,
            windSpeed = 6,
            pressure = 1011,
            visibility = 10.0,
            airQuality = AirQuality(
                aqi = 2,
                quality = "Fair",
                pm25 = 23.0,
                pm10 = 29.0,
                ozone = 62.0,
                no2 = 1.0
            ),
            uvIndex = UVIndex(
                index = 2,
                level = "Low",
                peakTime = "12:00 PM"
            ),
            sunriseSunset = SunriseSunset(
                sunrise = "5:45 AM",
                sunset = "5:50 PM",
                dayLength = "11h 26m"
            ),
            hourlyForecast = listOf(
                HourlyForecast("3 AM", 29, "Overcast Clouds", "☁️", 59, 6.19),
                HourlyForecast("6 AM", 30, "Overcast Clouds", "☁️", 55, 4.69),
                HourlyForecast("9 AM", 31, "Overcast Clouds", "☁️", 49, 3.99),
                HourlyForecast("12 PM", 27, "Overcast Clouds", "☁️", 64, 2.75),
                HourlyForecast("3 PM", 26, "Overcast Clouds", "☁️", 68, 3.17)
            ),
            fiveDayForecast = listOf(
                DailyForecast("Saturday", "20 Dec", 29, 29, "Overcast Clouds", "☁️", 59, 6.19),
                DailyForecast("Sunday", "21 Dec", 23, 23, "Overcast Clouds", "☁️", 74, 4.09),
                DailyForecast("Monday", "22 Dec", 24, 24, "Overcast Clouds", "☁️", 74, 3.87),
                DailyForecast("Tuesday", "23 Dec", 23, 23, "Overcast Clouds", "☁️", 77, 2.98),
                DailyForecast("Wednesday", "24 Dec", 23, 23, "Scattered Clouds", "⛅", 76, 3.08)
            )
        )
    
    val (cityName, countryCode) = parseLocation(weatherData.location)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 🟣 Weather Forecast Header Card
            WeatherForecastHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearch = { 
                    if (searchQuery.isNotBlank()) {
                        viewModel.searchWeatherByCity(searchQuery)
                    }
                },
                onLocationClick = {
                    if (locationPermissionsState.allPermissionsGranted) {
                        viewModel.refreshWithDeviceLocation()
                    } else {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }
                },
                isCelsius = uiState.isCelsius,
                onToggleUnit = { viewModel.toggleTemperatureUnit() },
                popularCities = listOf("Phnom Penh", "Siem Reap", "Battambang", "Sihanoukville", "Kampot"),
                onCitySelect = { city -> viewModel.searchWeatherByCity(city) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 📍 Location Display
            LocationHeader(
                cityName = cityName,
                countryCode = countryCode,
                lastUpdated = uiState.lastUpdated
            )
            
            // Loading/Error States
            if (uiState.isLoading) {
                LoadingOverlay(isLoading = true)
            }
            
            uiState.error?.let { error ->
                ErrorCard(
                    message = error,
                    onRetry = { viewModel.forceRefresh() },
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            // 🌡️ Main Weather Card
            MainWeatherCard(
                weatherData = weatherData,
                isRefreshing = uiState.isRefreshing
            )
            
            // 📊 Weather Details Row
            WeatherDetailsRow(weatherData = weatherData)
            
            // ⏰ Hourly Forecast
            HourlyForecastSection(hourlyForecasts = weatherData.hourlyForecast)
            
            // 🌬️ Air Quality Card
            AirQualityCard(airQuality = weatherData.airQuality)
            
            // ☀️ UV Index Card
            UVIndexCard(uvIndex = weatherData.uvIndex)
            
            // 🌅 Sunrise & Sunset
            SunriseSunsetCard(sunriseSunset = weatherData.sunriseSunset)
            
            // 📅 5-Day Forecast
            FiveDayForecastSection(dailyForecasts = weatherData.fiveDayForecast)
            
            // 🗺️ Weather Map
            WeatherMapCard(
                currentLocation = LatLng(
                    uiState.currentLat ?: 11.5564,
                    uiState.currentLon ?: 104.9282
                ),
                onExpandMap = onNavigateToMap,
                onLocationClick = { latLng ->
                    viewModel.searchWeatherByCoords(latLng.latitude, latLng.longitude)
                },
                onCurrentLocationRequest = {
                    if (locationPermissionsState.allPermissionsGranted) {
                        viewModel.refreshWithDeviceLocation()
                    } else {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Profile button
        IconButton(
            onClick = onNavigateToProfile,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
