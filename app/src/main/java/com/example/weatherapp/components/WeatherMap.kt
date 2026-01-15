package com.example.weatherapp.components

import android.content.Context
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
import com.example.weatherapp.BuildConfig
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay

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

/**
 * Create OpenWeatherMap tile source for weather overlay
 */
private fun createWeatherTileSource(layerId: String, apiKey: String): OnlineTileSourceBase {
    return object : XYTileSource(
        "OWM_$layerId",
        0, 18, 256, ".png",
        arrayOf("https://tile.openweathermap.org/map/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "https://tile.openweathermap.org/map/$layerId/$zoom/$x/$y.png?appid=$apiKey"
        }
    }
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
    val context = LocalContext.current
    var selectedLayer by remember { mutableStateOf(MapLayer.TEMPERATURE) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isLoadingLayer by remember { mutableStateOf(false) }
    
    // API Key
    val apiKey = if (BuildConfig.WEATHER_API_KEY.isNotEmpty()) {
        BuildConfig.WEATHER_API_KEY
    } else {
        "63030200ba49f825a3bd4ab30b8aad49"
    }
    
    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = context.packageName
    }
    
    // Function to update weather overlay
    fun updateWeatherOverlay(map: MapView, layerId: String, ctx: Context, key: String) {
        // Remove existing weather overlays (keep markers)
        val overlaysToRemove = map.overlays.filterIsInstance<TilesOverlay>()
        overlaysToRemove.forEach { overlay ->
            map.overlays.remove(overlay)
        }
        
        // Add new weather tile overlay with fresh cache
        val weatherTileSource = createWeatherTileSource(layerId, key)
        val weatherTileProvider = org.osmdroid.tileprovider.MapTileProviderBasic(ctx, weatherTileSource)
        weatherTileProvider.clearTileCache()
        val weatherOverlay = TilesOverlay(weatherTileProvider, ctx)
        weatherOverlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
        weatherOverlay.loadingLineColor = android.graphics.Color.argb(100, 102, 126, 234)
        weatherOverlay.setColorFilter(null)
        map.overlays.add(0, weatherOverlay)
        map.invalidate()
        
        android.util.Log.d("WeatherMapCard", "Layer updated to: $layerId")
    }
    
    // Update weather overlay when layer changes
    LaunchedEffect(selectedLayer) {
        isLoadingLayer = true
        mapView?.let { map ->
            updateWeatherOverlay(map, selectedLayer.id, context, apiKey)
        }
        kotlinx.coroutines.delay(1500)
        isLoadingLayer = false
    }
    
    // Update map center when location changes
    LaunchedEffect(currentLat, currentLon) {
        mapView?.controller?.animateTo(GeoPoint(currentLat, currentLon))
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
                                    text = "Live Weather",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "${selectedLayer.icon} ${selectedLayer.label} Layer",
                                    fontSize = 12.sp,
                                    color = TextWhite.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        Row {
                            IconButton(
                                onClick = onCurrentLocationRequest,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "My Location",
                                    tint = TextWhite
                                )
                            }
                            IconButton(
                                onClick = onExpandMap,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Expand Map",
                                    tint = TextWhite
                                )
                            }
                        }
                    }
                }
                
                // Map Content with OSMDroid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(9.0)
                                controller.setCenter(GeoPoint(currentLat, currentLon))
                                
                                // Add weather overlay
                                val weatherTileSource = createWeatherTileSource(selectedLayer.id, apiKey)
                                val weatherTileProvider = org.osmdroid.tileprovider.MapTileProviderBasic(ctx, weatherTileSource)
                                val weatherOverlay = TilesOverlay(weatherTileProvider, ctx)
                                weatherOverlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
                                weatherOverlay.loadingLineColor = android.graphics.Color.TRANSPARENT
                                overlays.add(0, weatherOverlay)
                                
                                // Add current location marker
                                val marker = Marker(this)
                                marker.position = GeoPoint(currentLat, currentLon)
                                marker.title = "Current Location"
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                overlays.add(marker)
                                
                                // Handle map clicks
                                setOnTouchListener { _, event ->
                                    if (event.action == android.view.MotionEvent.ACTION_UP) {
                                        val projection = projection
                                        val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                                        onLocationClick(geoPoint.latitude, geoPoint.longitude)
                                    }
                                    false
                                }
                                
                                mapView = this
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { map ->
                            // Update weather layer when selection changes
                            updateWeatherOverlay(map, selectedLayer.id, context, apiKey)
                        }
                    )
                    
                    // Quick layer indicator
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedLayer.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedLayer.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Zoom controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(8.dp)
                    ) {
                        SmallFloatingActionButton(
                            onClick = { mapView?.controller?.zoomIn() },
                            containerColor = Color.White,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, "Zoom In", tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        SmallFloatingActionButton(
                            onClick = { mapView?.controller?.zoomOut() },
                            containerColor = Color.White,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, "Zoom Out", tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                // Layer Selection Chips
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(MapLayer.entries.toList()) { layer ->
                            Surface(
                                onClick = { selectedLayer = layer },
                                shape = RoundedCornerShape(16.dp),
                                color = if (selectedLayer == layer) 
                                    PurplePrimary else Color.Gray.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = layer.icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = layer.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedLayer == layer) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedLayer == layer) TextWhite else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}
