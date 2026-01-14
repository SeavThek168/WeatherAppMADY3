package com.example.weatherapp.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.weatherapp.BuildConfig

// Colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)
private val TextWhite = Color(0xFFFFFFFF)

// OpenWeatherMap layer types for the card
private enum class MapLayer(val id: String, val icon: String, val label: String) {
    TEMPERATURE("temp_new", "🌡️", "Temp"),
    PRECIPITATION("precipitation_new", "🌧️", "Rain"),
    WIND("wind_new", "💨", "Wind"),
    CLOUDS("clouds_new", "☁️", "Clouds")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapCard(
    modifier: Modifier = Modifier,
    currentLat: Double = 11.5868,
    currentLon: Double = 104.8869,
    onExpandMap: () -> Unit = {},
    onLocationClick: (Double, Double) -> Unit = { _, _ -> },
    onCurrentLocationRequest: () -> Unit = {}
) {
    var selectedLayer by remember { mutableStateOf(MapLayer.TEMPERATURE) }
    var mapKey by remember { mutableStateOf(0) }
    
    // API Key
    val apiKey = if (BuildConfig.WEATHER_API_KEY.isNotEmpty()) {
        BuildConfig.WEATHER_API_KEY
    } else {
        "63030200ba49f825a3bd4ab30b8aad49"
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Title
        Text(
            text = "Weather Map",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PurplePrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Purple Header
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
                                tint = TextWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Weather Map",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "OpenWeatherMap",
                                    fontSize = 11.sp,
                                    color = TextWhite.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Row {
                            IconButton(onClick = onExpandMap) {
                                Icon(
                                    imageVector = Icons.Default.OpenInFull,
                                    contentDescription = "Expand",
                                    tint = TextWhite
                                )
                            }
                        }
                    }
                }
                
                // Map Layer Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MapLayer.entries) { layer ->
                        MapLayerChip(
                            icon = layer.icon,
                            text = layer.label,
                            selected = selectedLayer == layer,
                            onClick = {
                                selectedLayer = layer
                                mapKey++
                            }
                        )
                    }
                }
                
                // Map View using OpenWeatherMap
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    key(mapKey) {
                        OpenWeatherMapCardView(
                            layer = selectedLayer,
                            apiKey = apiKey,
                            lat = currentLat,
                            lon = currentLon,
                            zoom = 8
                        )
                    }
                    
                    // Layer indicator
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedLayer.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedLayer.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PurplePrimary
                            )
                        }
                    }
                    
                    // Full screen button
                    FloatingActionButton(
                        onClick = onExpandMap,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(40.dp),
                        containerColor = PurplePrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Full Screen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun OpenWeatherMapCardView(
    layer: MapLayer,
    apiKey: String,
    lat: Double,
    lon: Double,
    zoom: Int
) {
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
                    zoomControl: false,
                    attributionControl: true
                }).setView([$lat, $lon], $zoom);
                
                // Base layer - OpenStreetMap
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                    attribution: '© OSM'
                }).addTo(map);
                
                // Weather layer from OpenWeatherMap
                L.tileLayer('https://tile.openweathermap.org/map/${layer.id}/{z}/{x}/{y}.png?appid=$apiKey', {
                    maxZoom: 18,
                    opacity: 0.6,
                    attribution: '© OWM'
                }).addTo(map);
                
                // Center marker
                var marker = L.marker([$lat, $lon]).addTo(map);
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
private fun MapLayerChip(
    icon: String,
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) PurplePrimary else Color(0xFFF0F0F0)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else Color.DarkGray
            )
        }
    }
}
