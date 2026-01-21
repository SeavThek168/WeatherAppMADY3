package com.example.weatherapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.data.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CurrentWeatherCard(
    weather: WeatherResponse,
    modifier: Modifier = Modifier
) {
    val isNightTime = isNight(weather.sys.sunrise, weather.sys.sunset, weather.dt)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    brush = if (isNightTime) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1a1a2e),
                                Color(0xFF16213e)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E90FF),
                                Color(0xFF00BFFF)
                            )
                        )
                    }
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Time indicator and location
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = getCurrentDate(),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = if (isNightTime) "Night" else "Day",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${weather.name}, ${weather.sys.country}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Temperature and Weather Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${weather.main.temp.toInt()}°",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Feels like ${weather.main.feels_like.toInt()}°",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = weather.weather[0].description.replaceFirstChar { it.uppercase() },
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "↓ ${weather.main.temp_min.toInt()}°",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "↑ ${weather.main.temp_max.toInt()}°",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weather.weather[0].icon}@4x.png",
                        contentDescription = weather.weather[0].description,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                // Divider
                Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
                
                // Additional Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherInfoItem("Wind", "${weather.wind.speed} m/s")
                    WeatherInfoItem("Humidity", "${weather.main.humidity}%")
                    WeatherInfoItem("Pressure", "${weather.main.pressure} hPa")
                    WeatherInfoItem("Visibility", "${(weather.visibility / 1000f).toInt()} km")
                }
            }
        }
    }
}

@Composable
private fun WeatherInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    return dateFormat.format(Date())
}

private fun isNight(sunrise: Long, sunset: Long, currentTime: Long): Boolean {
    return currentTime < sunrise || currentTime > sunset
}
