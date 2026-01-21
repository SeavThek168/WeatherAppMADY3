package com.example.weatherapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.ForecastResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FiveDayForecastCard(
    forecast: ForecastResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4FACFE),
                            Color(0xFF00F2FE)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "5-Day Forecast",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val uniqueDayForecasts = getUniqueDayForecasts(forecast.list)
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uniqueDayForecasts.forEach { forecastItem ->
                    ForecastDayItem(forecastItem)
                }
            }
        }
    }
}

@Composable
private fun ForecastDayItem(forecastItem: ForecastItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDay(forecastItem.dt_txt),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = formatDate(forecastItem.dt_txt),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${forecastItem.weather[0].icon}@2x.png",
                contentDescription = forecastItem.weather[0].description,
                modifier = Modifier.size(50.dp)
            )
            
            Text(
                text = "${forecastItem.main.temp.toInt()}°C",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.width(70.dp)
            )
        }
    }
}

private fun getUniqueDayForecasts(forecasts: List<ForecastItem>): List<ForecastItem> {
    val uniqueDays = mutableMapOf<String, ForecastItem>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    forecasts.forEach { forecast ->
        val date = dateFormat.format(Date(forecast.dt * 1000))
        if (!uniqueDays.containsKey(date) && uniqueDays.size < 5) {
            uniqueDays[date] = forecast
        }
    }
    
    return uniqueDays.values.toList()
}

private fun formatDay(dateString: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = format.parse(dateString)
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    return dayFormat.format(date ?: Date())
}

private fun formatDate(dateString: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = format.parse(dateString)
    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    return dateFormat.format(date ?: Date())
}
