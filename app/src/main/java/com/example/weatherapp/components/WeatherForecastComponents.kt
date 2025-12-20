package com.example.weatherapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.models.WeatherData
import java.text.SimpleDateFormat
import java.util.*

// 🎨 Color palette matching the screenshots
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)
private val PurpleLight = Color(0xFF9b8dd4)
private val GoldAccent = Color(0xFFd4af37)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGray = Color(0xFF6b7280)
private val CardBackground = Color(0xFFFFFFFF)
private val BackgroundLight = Color(0xFFF8F9FE)

/**
 * 🟣 Weather Forecast Header
 * Purple gradient card with search, location button, and popular cities
 */
@Composable
fun WeatherForecastHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLocationClick: () -> Unit,
    isCelsius: Boolean,
    onToggleUnit: () -> Unit,
    popularCities: List<String>,
    onCitySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PurplePrimary, PurpleSecondary)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                // Header Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "☁️",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Weather Forecast",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Search Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Input
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = Color.Black
                                ),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search for a city...",
                                            color = TextGray,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                    
                    // Search Button
                    Surface(
                        modifier = Modifier
                            .clickable { onSearch() },
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = PurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Search",
                                color = PurplePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Current Location Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLocationClick() },
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Current Location",
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Current Location",
                            color = PurplePrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Popular Cities
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular:",
                        color = TextWhite.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        popularCities.forEach { city ->
                            Surface(
                                modifier = Modifier.clickable { onCitySelect(city) },
                                shape = RoundedCornerShape(20.dp),
                                color = TextWhite.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = city,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Temperature Unit Toggle
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = CardBackground,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(4.dp)
            ) {
                // Celsius button - highlighted when isCelsius is true
                Surface(
                    modifier = Modifier.clickable { if (!isCelsius) onToggleUnit() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCelsius) GoldAccent else Color.Transparent
                ) {
                    Text(
                        text = "°C",
                        color = if (isCelsius) Color.White else TextGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                // Fahrenheit button - highlighted when isCelsius is false
                Surface(
                    modifier = Modifier.clickable { if (isCelsius) onToggleUnit() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isCelsius) GoldAccent else Color.Transparent
                ) {
                    Text(
                        text = "°F",
                        color = if (!isCelsius) Color.White else TextGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * 📍 Location Header
 * Shows current location and last updated time
 */
@Composable
fun LocationHeader(
    cityName: String,
    countryCode: String,
    lastUpdated: Long?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location
        Text(
            text = "$cityName, $countryCode",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PurplePrimary
        )
        
        // Last Updated
        lastUpdated?.let {
            val timeFormat = SimpleDateFormat("MM/dd/yyyy, h:mm:ss a", Locale.getDefault())
            Text(
                text = "Last updated: ${timeFormat.format(Date(it))}",
                fontSize = 12.sp,
                color = TextGray
            )
        }
    }
}

/**
 * 🌡️ Main Weather Card
 * Large purple card with current temperature and conditions
 */
@Composable
fun MainWeatherCard(
    weatherData: WeatherData,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PurplePrimary, PurpleSecondary, PurpleLight)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                // Date and Updated indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentDate,
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔄",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRefreshing) "Updating..." else "Updated Just now",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - Temperature
                    Column {
                        Text(
                            text = "${weatherData.currentTemp}°",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Feels like ${weatherData.feelsLike}°",
                            color = TextWhite.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = weatherData.condition,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextWhite.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weatherData.location,
                                color = TextWhite.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(
                                text = "↓ ${weatherData.lowTemp}°",
                                color = TextWhite.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "↑ ${weatherData.highTemp}°",
                                color = TextWhite.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    // Right side - Weather Icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    color = PurpleLight.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getWeatherEmoji(weatherData.condition),
                                fontSize = 50.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PurpleLight.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "Day",
                                color = TextWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 📊 Weather Details Row
 * Wind, Humidity, Pressure, Visibility cards
 */
@Composable
fun WeatherDetailsRow(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeatherDetailItem(
            icon = "💨",
            value = "${weatherData.windSpeed} m/s",
            label = "Wind",
            modifier = Modifier.weight(1f)
        )
        WeatherDetailItem(
            icon = "💧",
            value = "${weatherData.humidity}%",
            label = "Humidity",
            modifier = Modifier.weight(1f)
        )
        WeatherDetailItem(
            icon = "🌡️",
            value = "${weatherData.pressure}",
            label = "Pressure",
            modifier = Modifier.weight(1f)
        )
        WeatherDetailItem(
            icon = "👁️",
            value = "${weatherData.visibility} km",
            label = "Visibility",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WeatherDetailItem(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLight.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PurplePrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextGray
            )
        }
    }
}

/**
 * Get weather emoji based on condition
 */
private fun getWeatherEmoji(condition: String): String {
    return when {
        condition.contains("clear", ignoreCase = true) -> "☀️"
        condition.contains("sunny", ignoreCase = true) -> "☀️"
        condition.contains("cloud", ignoreCase = true) -> "☁️"
        condition.contains("overcast", ignoreCase = true) -> "☁️"
        condition.contains("rain", ignoreCase = true) -> "🌧️"
        condition.contains("storm", ignoreCase = true) -> "⛈️"
        condition.contains("snow", ignoreCase = true) -> "❄️"
        condition.contains("fog", ignoreCase = true) -> "🌫️"
        condition.contains("mist", ignoreCase = true) -> "🌫️"
        else -> "⛅"
    }
}
