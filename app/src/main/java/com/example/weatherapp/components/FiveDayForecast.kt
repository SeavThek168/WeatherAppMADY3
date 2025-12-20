package com.example.weatherapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.models.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

// Cyan/Teal colors for 5-Day Forecast
private val CyanPrimary = Color(0xFF00BCD4)
private val CyanSecondary = Color(0xFF0097A7)
private val TextWhite = Color(0xFFFFFFFF)
private val RedTemp = Color(0xFFEF5350)
private val BlueTemp = Color(0xFF42A5F5)
private val PurplePrimary = Color(0xFF667eea)

@Composable
fun FiveDayForecastSection(
    dailyForecasts: List<DailyForecast>,
    modifier: Modifier = Modifier
) {
    val currentTime = remember {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Title
        Text(
            text = "5-Day Forecast",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PurplePrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Cyan Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(CyanPrimary, CyanSecondary)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "5-Day Forecast",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = TextWhite.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Updated $currentTime",
                            fontSize = 12.sp,
                            color = TextWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
        
        // Forecast Cards Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                dailyForecasts.forEach { forecast ->
                    DailyForecastItem(forecast)
                }
            }
        }
    }
}

@Composable
private fun DailyForecastItem(forecast: DailyForecast) {
    Card(
        modifier = Modifier.width(130.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Day Name
            Text(
                text = forecast.day,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )
            
            // Date
            Text(
                text = forecast.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Weather Icon
            Text(
                text = forecast.icon,
                fontSize = 36.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Temperature
            Text(
                text = "${forecast.highTemp}°C",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            // High/Low
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "↑ ${forecast.highTemp}°",
                    fontSize = 12.sp,
                    color = RedTemp
                )
                Text(
                    text = "↓ ${forecast.lowTemp}°",
                    fontSize = 12.sp,
                    color = BlueTemp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Condition
            Text(
                text = forecast.condition,
                fontSize = 11.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Humidity
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💧", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${forecast.precipChance}%",
                    fontSize = 12.sp,
                    color = BlueTemp
                )
            }
            
            // Wind Speed
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💨", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${String.format("%.2f", forecast.windSpeed)} m/s",
                    fontSize = 12.sp,
                    color = CyanPrimary
                )
            }
        }
    }
}
