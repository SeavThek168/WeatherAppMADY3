package com.example.weatherapp.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.components.*
import com.example.weatherapp.models.*
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

// CamWeather theme colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)
private val BackgroundLight = Color(0xFFF5F7FA)

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

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurplePrimary, PurpleSecondary)
                        )
                    )
                    .padding(top = 16.dp, bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // App Title Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌤️",
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CamWeather",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        // Unit Toggle
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            onClick = { viewModel.toggleTemperatureUnit() }
                        ) {
                            Text(
                                text = if (uiState.isCelsius) "°C" else "°F",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                "Search city...", 
                                color = Color.White.copy(alpha = 0.7f)
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.searchWeatherByCity(searchQuery)
                                        searchQuery = ""
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Search",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                IconButton(onClick = {
                                    if (locationPermissionsState.allPermissionsGranted) {
                                        viewModel.refreshWithDeviceLocation()
                                    } else {
                                        locationPermissionsState.launchMultiplePermissionRequest()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = "Use my location",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Quick City Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Phnom Penh", "Siem Reap", "Battambang").forEach { city ->
                            SuggestionChip(
                                onClick = { viewModel.searchWeatherByCity(city) },
                                label = { 
                                    Text(
                                        text = city, 
                                        fontSize = 12.sp,
                                        color = Color.White
                                    ) 
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color.White.copy(alpha = 0.15f)
                                ),
                                border = null
                            )
                        }
                    }
                }
            }
            
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
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
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Location & Last Updated
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$cityName, $countryCode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    
                    uiState.lastUpdated?.let { updated ->
                        Text(
                            text = "Updated: $updated",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 🌡️ Main Weather Card
                MainWeatherCard(
                    weatherData = weatherData,
                    isRefreshing = uiState.isRefreshing
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 📊 Weather Details Row
                WeatherDetailsRow(weatherData = weatherData)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // ⏰ Hourly Forecast
                HourlyForecastSection(hourlyForecasts = weatherData.hourlyForecast)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 📅 5-Day Forecast
                FiveDayForecastSection(dailyForecasts = weatherData.fiveDayForecast)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Weather Details Grid (Air Quality, UV, Sunrise/Sunset)
                Text(
                    text = "Weather Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // 🌬️ Air Quality Card
                AirQualityCard(airQuality = weatherData.airQuality)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Row for UV and Sunrise/Sunset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        UVIndexCard(uvIndex = weatherData.uvIndex)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SunriseSunsetCard(sunriseSunset = weatherData.sunriseSunset)
                    }
                }
                
                // Bottom spacing for navigation bar
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        
        // Pull to refresh indicator
        if (uiState.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = PurplePrimary
            )
        }
    }
}
