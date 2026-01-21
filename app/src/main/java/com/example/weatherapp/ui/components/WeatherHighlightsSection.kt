package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.AirQualityResponse
import com.example.weatherapp.data.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherHighlightsSection(
    weather: WeatherResponse?,
    airQuality: AirQualityResponse?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Today's Highlights",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        weather?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Air Quality Card
                airQuality?.let { aq ->
                    HighlightCard(
                        title = "Air Quality",
                        value = getAirQualityLabel(aq.list[0].main.aqi),
                        description = "AQI: ${aq.list[0].main.aqi}",
                        color = getAirQualityColor(aq.list[0].main.aqi),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Sunrise/Sunset Card
                HighlightCard(
                    title = "Sun Times",
                    value = formatTime(it.sys.sunrise),
                    description = "Sunset: ${formatTime(it.sys.sunset)}",
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Humidity Card
                HighlightCard(
                    title = "Humidity",
                    value = "${it.main.humidity}%",
                    description = "Feels like ${it.main.feels_like.toInt()}°C",
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                
                // Pressure Card
                HighlightCard(
                    title = "Pressure",
                    value = "${it.main.pressure} hPa",
                    description = "Visibility ${it.visibility / 1000}km",
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wind Card
                HighlightCard(
                    title = "Wind Speed",
                    value = "${it.wind.speed} m/s",
                    description = "Direction ${it.wind.deg}°",
                    color = Color(0xFF00BCD4),
                    modifier = Modifier.weight(1f)
                )
                
                // Cloudiness Card
                HighlightCard(
                    title = "Cloudiness",
                    value = "${it.clouds.all}%",
                    description = it.weather[0].main,
                    color = Color(0xFF607D8B),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HighlightCard(
    title: String,
    value: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
            
            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun getAirQualityLabel(aqi: Int): String {
    return when (aqi) {
        1 -> "Good"
        2 -> "Fair"
        3 -> "Moderate"
        4 -> "Poor"
        5 -> "Very Poor"
        else -> "Unknown"
    }
}

private fun getAirQualityColor(aqi: Int): Color {
    return when (aqi) {
        1 -> Color(0xFF4CAF50)
        2 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFFEB3B)
        4 -> Color(0xFFFF9800)
        5 -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

private fun formatTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp * 1000))
}
