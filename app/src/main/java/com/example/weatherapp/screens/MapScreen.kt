package com.example.weatherapp.screens

import android.content.Context
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

// OpenWeatherMap layer types
enum class WeatherMapLayer(
    val layerId: String,
    val displayName: String,
    val icon: String
) {
    TEMPERATURE("temp_new", "Temperature", "🌡️"),
    PRECIPITATION("precipitation_new", "Precipitation", "🌧️"),
    CLOUDS("clouds_new", "Clouds", "☁️"),
    WIND("wind_new", "Wind", "💨"),
    PRESSURE("pressure_new", "Pressure", "📊")
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
fun MapScreen(
    onLocationSelected: (Double, Double, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by weatherViewModel.uiState.collectAsState()
    
    var selectedLayer by remember { mutableStateOf(WeatherMapLayer.TEMPERATURE) }
    var selectedCity by remember { mutableStateOf<CambodiaCity?>(null) }
    var showCityWeather by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isLoadingLayer by remember { mutableStateOf(false) }
    var currentLayerKey by remember { mutableStateOf(0) } // Force recomposition
    
    // API Key
    val apiKey = if (BuildConfig.WEATHER_API_KEY.isNotEmpty()) {
        BuildConfig.WEATHER_API_KEY
    } else {
        "63030200ba49f825a3bd4ab30b8aad49"
    }
    
    // Default location (Cambodia center or current location)
    val defaultLat = uiState.currentLat ?: 12.5
    val defaultLon = uiState.currentLon ?: 104.9
    
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
        weatherOverlay.loadingLineColor = android.graphics.Color.argb(100, 102, 126, 234) // Purple loading line
        weatherOverlay.setColorFilter(null)
        map.overlays.add(0, weatherOverlay)
        map.invalidate()
        
        android.util.Log.d("WeatherMap", "Layer updated to: $layerId")
    }
    
    // Update weather overlay when layer changes
    LaunchedEffect(selectedLayer) {
        isLoadingLayer = true
        currentLayerKey++
        mapView?.let { map ->
            updateWeatherOverlay(map, selectedLayer.layerId, context, apiKey)
        }
        kotlinx.coroutines.delay(1500) // Give time for tiles to load
        isLoadingLayer = false
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
                            text = "OpenWeatherMap + OpenStreetMap",
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
                            onClick = { selectedLayer = layer }
                        )
                    }
                }
            }
        }
        
        // Map View with OSMDroid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // OSMDroid MapView
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(7.0)
                            controller.setCenter(GeoPoint(defaultLat, defaultLon))
                            
                            // Add weather overlay
                            val weatherTileSource = createWeatherTileSource(selectedLayer.layerId, apiKey)
                            val weatherTileProvider = org.osmdroid.tileprovider.MapTileProviderBasic(ctx, weatherTileSource)
                            val weatherOverlay = TilesOverlay(weatherTileProvider, ctx)
                            weatherOverlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
                            weatherOverlay.loadingLineColor = android.graphics.Color.TRANSPARENT
                            overlays.add(0, weatherOverlay)
                            
                            // Add city markers
                            cambodiaCities.forEach { city ->
                                val marker = Marker(this)
                                marker.position = GeoPoint(city.lat, city.lon)
                                marker.title = city.name
                                marker.snippet = "Tap for weather"
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.setOnMarkerClickListener { _, _ ->
                                    selectedCity = city
                                    showCityWeather = true
                                    controller.animateTo(GeoPoint(city.lat, city.lon), 10.0, 500L)
                                    true
                                }
                                overlays.add(marker)
                            }
                            
                            // Add current location marker
                            val currentMarker = Marker(this)
                            currentMarker.position = GeoPoint(defaultLat, defaultLon)
                            currentMarker.title = "Current Location"
                            currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            overlays.add(currentMarker)
                            
                            mapView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { map ->
                        // Update weather layer
                        updateWeatherOverlay(map, selectedLayer.layerId, context, apiKey)
                        
                        // Update when location changes
                        if (uiState.currentLat != null && uiState.currentLon != null) {
                            map.controller.setCenter(GeoPoint(uiState.currentLat!!, uiState.currentLon!!))
                        }
                    }
                )
                
                // Zoom Controls
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { mapView?.controller?.zoomIn() },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, "Zoom In", tint = PurplePrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { mapView?.controller?.zoomOut() },
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
                
                // Current layer indicator with loading
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
                        if (isLoadingLayer) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = PurplePrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(text = selectedLayer.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLoadingLayer) "Loading..." else selectedLayer.displayName,
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
                                showCityWeather = true
                                mapView?.controller?.animateTo(GeoPoint(city.lat, city.lon), 10.0, 500L)
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
                colors = CardDefaults.cardColors(containerColor = PurplePrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = selectedCity?.icon ?: "", fontSize = 24.sp)
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
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
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
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
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
        Column(modifier = Modifier.padding(8.dp)) {
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
                    Text(text = label, fontSize = 9.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
