package com.example.weatherapp.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.models.WeatherData

// 🇰🇭 Beautiful Cambodia Color Palette
private val CardBackground = Color(0xFFFFFEFC)
private val SkyBlueLight = Color(0xFFE8F4FD)
private val AngkorGold = Color(0xFFD4AF37)
private val DeepNavy = Color(0xFF1A365D)
private val TropicalTeal = Color(0xFF17A2B8)
private val SunsetOrange = Color(0xFFFF6B35)
private val FreshGreen = Color(0xFF28A745)

/**
 * 🇰🇭 Beautiful Weather Details Card
 * 
 * Shows detailed weather information in a beautiful grid layout
 */
@Composable
fun CurrentWeatherCard(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weather Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
                
                // Feels like badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SkyBlueLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Feels like ",
                            fontSize = 12.sp,
                            color = DeepNavy.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${weatherData.feelsLike}°",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Weather details grid - 2x2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherDetailItem(
                    icon = Icons.Outlined.Thermostat,
                    label = "High / Low",
                    value = "${weatherData.highTemp}° / ${weatherData.lowTemp}°",
                    iconColor = SunsetOrange,
                    modifier = Modifier.weight(1f)
                )
                WeatherDetailItem(
                    icon = Icons.Outlined.WaterDrop,
                    label = "Humidity",
                    value = "${weatherData.humidity}%",
                    iconColor = TropicalTeal,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherDetailItem(
                    icon = Icons.Outlined.Air,
                    label = "Wind",
                    value = "${weatherData.windSpeed} km/h",
                    iconColor = FreshGreen,
                    modifier = Modifier.weight(1f)
                )
                WeatherDetailItem(
                    icon = Icons.Outlined.Visibility,
                    label = "UV Index",
                    value = "${weatherData.uvIndex.index} ${weatherData.uvIndex.level}",
                    iconColor = AngkorGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SkyBlueLight.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = DeepNavy.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepNavy
                )
            }
        }
    }
}
