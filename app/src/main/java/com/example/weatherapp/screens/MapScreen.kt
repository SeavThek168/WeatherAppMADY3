package com.example.weatherapp.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.viewmodel.WeatherViewModel

// Colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

// OpenWeatherMap layer types
enum class WeatherMapLayer(
    val layerId: String,
    val displayName: String,
    val icon: String,
    val description: String
) {
    TEMPERATURE("temp_new", "Temperature", "🌡️", "Surface temperature"),
    PRECIPITATION("precipitation_new", "Precipitation", "🌧️", "Rain and snow"),
    CLOUDS("clouds_new", "Clouds", "☁️", "Cloud coverage"),
    WIND("wind_new", "Wind", "💨", "Wind speed"),
    PRESSURE("pressure_new", "Pressure", "📊", "Sea level pressure")
}

// Cambodia cities for quick selection
private data class CambodiaCity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val icon: String
)

private val cambodiaCities = listOf(
    CambodiaCity("Phnom Penh", 11.5564, 104.9282, "🏛️"),
    CambodiaCity("Siem Reap", 13.3633, 103.8564, "🏯"),
    CambodiaCity("Battambang", 13.1023, 103.1962, "🌾"),
    CambodiaCity("Sihanoukville", 10.6093, 103.5296, "🏖️"),
    CambodiaCity("Kampot", 10.5940, 104.1640, "🌶️"),
    CambodiaCity("Kratie", 12.4880, 106.0189, "🐬"),
    CambodiaCity("Koh Kong", 11.6150, 102.9840, "🌴"),
    CambodiaCity("Banlung", 13.7396, 106.9872, "🌲")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onLocationSelected: (Double, Double, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsState()
    
    var selectedLayer by remember { mutableStateOf(WeatherMapLayer.TEMPERATURE) }
    var currentZoom by remember { mutableStateOf(6) }
    var centerLat by remember { mutableStateOf(uiState.currentLat ?: 12.5) }
    var centerLon by remember { mutableStateOf(uiState.currentLon ?: 104.9) }
    var selectedCity by remember { mutableStateOf<CambodiaCity?>(null) }
    var showCityWeather by remember { mutableStateOf(false) }
    
    // Trigger recomposition when layer or position changes
    var mapKey by remember { mutableStateOf(0) }
    
    // API Key
    val apiKey = if (BuildConfig.WEATHER_API_KEY.isNotEmpty()) {
        BuildConfig.WEATHER_API_KEY
    } else {
        "63030200ba49f825a3bd4ab30b8aad49"
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PurplePrimary, PurpleSecondary)
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Weather Map",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "OpenWeatherMap",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
        
        // Layer Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Weather Layers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PurplePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(WeatherMapLayer.entries) { layer ->
                        WeatherLayerChip(
                            layer = layer,
                            isSelected = selectedLayer == layer,
                            onClick = { 
                                selectedLayer = layer
                                mapKey++ // Force map reload
                            }
                        )
                    }
                }
            }
        }
        
        // Map View
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // OpenWeatherMap WebView
                key(mapKey) {
                    OpenWeatherMapView(
                        layer = selectedLayer,
                        apiKey = apiKey,
                        lat = centerLat,
                        lon = centerLon,
                        zoom = currentZoom
                    )
                }
                
                // Zoom Controls
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { 
                            if (currentZoom < 12) {
                                currentZoom++
                                mapKey++
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, "Zoom In", tint = PurplePrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { 
                            if (currentZoom > 3) {
                                currentZoom--
                                mapKey++
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color.White
                    ) {
                        Icon(Icons.Default.Remove, "Zoom Out", tint = PurplePrimary)
                    }
                }
                
                // Layer Legend
                LayerLegend(
                    layer = selectedLayer,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
                
                // Current layer info
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedLayer.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedLayer.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Quick City Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Quick Access - Cambodia Cities",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PurplePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cambodiaCities) { city ->
                        CityChip(
                            city = city,
                            isSelected = selectedCity?.name == city.name,
                            onClick = {
                                selectedCity = city
                                centerLat = city.lat
                                centerLon = city.lon
                                currentZoom = 10
                                showCityWeather = true
                                mapKey++
                            }
                        )
                    }
                }
            }
        }
        
        // Selected City Action
        AnimatedVisibility(visible = showCityWeather && selectedCity != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PurplePrimary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedCity?.icon ?: "",
                            fontSize = 24.sp
                        )
                        Text(
                            text = selectedCity?.name ?: "",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Tap to get weather",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    
                    Row {
                        OutlinedButton(
                            onClick = {
                                showCityWeather = false
                                selectedCity = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                selectedCity?.let { city ->
                                    weatherViewModel.searchWeatherByCoords(city.lat, city.lon)
                                    onLocationSelected(city.lat, city.lon, city.name)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PurplePrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Get Weather")
                        }
                    }
                }
            }
        }
        
        // Bottom spacing for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun OpenWeatherMapView(
    layer: WeatherMapLayer,
    apiKey: String,
    lat: Double,
    lon: Double,
    zoom: Int
) {
    // Create HTML content for the map using Leaflet.js
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { width: 100%; height: 100vh; }
                .leaflet-control-attribution { font-size: 8px !important; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', {
                    zoomControl: false
                }).setView([$lat, $lon], $zoom);
                
                // Base layer - OpenStreetMap
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                    attribution: '© OpenStreetMap'
                }).addTo(map);
                
                // Weather layer from OpenWeatherMap
                L.tileLayer('https://tile.openweathermap.org/map/${layer.layerId}/{z}/{x}/{y}.png?appid=$apiKey', {
                    maxZoom: 18,
                    opacity: 0.6,
                    attribution: '© OpenWeatherMap'
                }).addTo(map);
                
                // Add marker for current view center
                var marker = L.marker([$lat, $lon]).addTo(map);
                marker.bindPopup("<b>${layer.displayName} Map</b><br>Lat: " + $lat.toFixed(4) + "<br>Lon: " + $lon.toFixed(4));
            </script>
        </body>
        </html>
    """.trimIndent()
    
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://tile.openweathermap.org",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun WeatherLayerChip(
    layer: WeatherMapLayer,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PurplePrimary else Color.Gray.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = layer.icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = layer.displayName,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.DarkGray
            )
        }
    }
}

@Composable
private fun CityChip(
    city: CambodiaCity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PurplePrimary.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f),
        border = if (isSelected) BorderStroke(2.dp, PurplePrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = city.icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = city.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PurplePrimary else Color.DarkGray
            )
        }
    }
}

@Composable
private fun LayerLegend(
    layer: WeatherMapLayer,
    modifier: Modifier = Modifier
) {
    val legendData = when (layer) {
        WeatherMapLayer.TEMPERATURE -> listOf(
            "❄️" to "-40°C",
            "🔵" to "0°C",
            "🟢" to "20°C",
            "🟡" to "30°C",
            "🔴" to "40°C+"
        )
        WeatherMapLayer.PRECIPITATION -> listOf(
            "⬜" to "0mm",
            "🟦" to "Light",
            "🟩" to "Moderate",
            "🟨" to "Heavy",
            "🟥" to "Extreme"
        )
        WeatherMapLayer.CLOUDS -> listOf(
            "⬜" to "Clear",
            "🔘" to "Partly",
            "☁️" to "Cloudy",
            "🌫️" to "Overcast"
        )
        WeatherMapLayer.WIND -> listOf(
            "🟢" to "Calm",
            "🟡" to "Moderate",
            "🟠" to "Strong",
            "🔴" to "Gale"
        )
        WeatherMapLayer.PRESSURE -> listOf(
            "🔵" to "Low",
            "🟢" to "Normal",
            "🔴" to "High"
        )
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Legend",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            legendData.forEach { (icon, label) ->
                Row(
                    modifier = Modifier.padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
