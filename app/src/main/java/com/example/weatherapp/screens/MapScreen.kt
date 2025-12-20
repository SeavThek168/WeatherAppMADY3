package com.example.weatherapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

// Colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

// Cambodia weather stations
private data class WeatherStationData(
    val name: String,
    val location: LatLng,
    val temperature: Int,
    val condition: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double
)

private val cambodiaWeatherStations = listOf(
    WeatherStationData("Phnom Penh", LatLng(11.5564, 104.9282), 29, "Overcast", "☁️", 59, 6.19),
    WeatherStationData("Siem Reap", LatLng(13.3633, 103.8564), 31, "Sunny", "☀️", 45, 3.5),
    WeatherStationData("Battambang", LatLng(13.1023, 103.1962), 30, "Partly Cloudy", "⛅", 52, 4.2),
    WeatherStationData("Sihanoukville", LatLng(10.6093, 103.5296), 28, "Cloudy", "☁️", 72, 8.1),
    WeatherStationData("Kampot", LatLng(10.5940, 104.1640), 27, "Clear", "🌤️", 65, 5.3),
    WeatherStationData("Kratie", LatLng(12.4880, 106.0189), 32, "Hot", "🔥", 48, 2.1),
    WeatherStationData("Koh Kong", LatLng(11.6150, 102.9840), 26, "Rainy", "🌧️", 85, 9.2),
    WeatherStationData("Banlung", LatLng(13.7396, 106.9872), 28, "Fair", "🌤️", 55, 3.8),
    WeatherStationData("Poipet", LatLng(13.6577, 102.5634), 33, "Hot", "☀️", 42, 4.5),
    WeatherStationData("Prey Veng", LatLng(11.4847, 105.3254), 30, "Cloudy", "☁️", 58, 3.2)
)

// Custom map style JSON
private val mapStyleJson = """
[
  {"elementType": "geometry", "stylers": [{"color": "#f5f5f5"}]},
  {"elementType": "labels.text.fill", "stylers": [{"color": "#616161"}]},
  {"elementType": "labels.text.stroke", "stylers": [{"color": "#f5f5f5"}]},
  {"featureType": "water", "elementType": "geometry", "stylers": [{"color": "#c9e4f6"}]},
  {"featureType": "water", "elementType": "labels.text.fill", "stylers": [{"color": "#9e9e9e"}]},
  {"featureType": "road", "elementType": "geometry", "stylers": [{"color": "#ffffff"}]},
  {"featureType": "road.highway", "elementType": "geometry", "stylers": [{"color": "#dadada"}]},
  {"featureType": "poi.park", "elementType": "geometry", "stylers": [{"color": "#c8e6c9"}]},
  {"featureType": "poi.business", "stylers": [{"visibility": "off"}]},
  {"featureType": "transit", "stylers": [{"visibility": "off"}]}
]
"""

@Composable
fun MapScreen(
    onLocationSelected: (Double, Double, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedStation by remember { mutableStateOf<WeatherStationData?>(null) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var showStationPopup by remember { mutableStateOf(false) }
    val uiState by weatherViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Use current location from ViewModel if available
    val defaultLocation = LatLng(
        uiState.currentLat ?: 11.5564,
        uiState.currentLon ?: 104.9282
    )
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 7f)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = mapType,
                mapStyleOptions = try {
                    MapStyleOptions(mapStyleJson)
                } catch (e: Exception) { null }
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = true
            ),
            onMapClick = { latLng ->
                selectedLocation = latLng
                selectedStation = null
                showStationPopup = false
            }
        ) {
            // Current weather location marker
            Marker(
                state = MarkerState(position = defaultLocation),
                title = "Current Location",
                snippet = uiState.weatherData?.location ?: "Weather location"
            )
            
            // Weather station markers
            cambodiaWeatherStations.forEach { station ->
                Marker(
                    state = MarkerState(position = station.location),
                    title = station.name,
                    snippet = "${station.temperature}°C - ${station.condition}",
                    onClick = {
                        selectedStation = station
                        selectedLocation = null
                        showStationPopup = true
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(station.location, 12f),
                                durationMs = 500
                            )
                        }
                        true
                    }
                )
            }
            
            // Selected location marker
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Selected Location",
                    snippet = "Tap 'Get Weather' to see weather here"
                )
            }
        }
        
        // Top bar with purple gradient
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(20.dp),
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
                        Text(
                            text = "Select Location",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Row {
                        // Map type toggle
                        IconButton(onClick = {
                            mapType = when (mapType) {
                                MapType.NORMAL -> MapType.SATELLITE
                                MapType.SATELLITE -> MapType.TERRAIN
                                MapType.TERRAIN -> MapType.HYBRID
                                else -> MapType.NORMAL
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Change Map Type",
                                tint = Color.White
                            )
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
            }
        }
        
        // Map controls (zoom + my location)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.zoomIn(), 300)
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
                    scope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.zoomOut(), 300)
                    }
                },
                modifier = Modifier.size(40.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Remove, "Zoom Out", tint = PurplePrimary)
            }
        }
        
        // My Location button
        FloatingActionButton(
            onClick = {
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f),
                        durationMs = 500
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 180.dp)
                .size(48.dp),
            containerColor = PurplePrimary
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                tint = Color.White
            )
        }
        
        // Weather station popup
        AnimatedVisibility(
            visible = showStationPopup && selectedStation != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        ) {
            selectedStation?.let { station ->
                WeatherStationPopupCard(
                    station = station,
                    onClose = {
                        showStationPopup = false
                        selectedStation = null
                    },
                    onGetWeather = {
                        weatherViewModel.searchWeatherByCoords(
                            station.location.latitude,
                            station.location.longitude
                        )
                        onLocationSelected(
                            station.location.latitude,
                            station.location.longitude,
                            station.name
                        )
                    }
                )
            }
        }
        
        // Bottom card with Get Weather button for custom selection
        selectedLocation?.let { location ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PurplePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Location Selected",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                        }
                        Text(
                            text = "Lat: ${String.format("%.4f", location.latitude)}, Lon: ${String.format("%.4f", location.longitude)}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Button(
                            onClick = {
                                weatherViewModel.searchWeatherByCoords(
                                    location.latitude,
                                    location.longitude
                                )
                                onLocationSelected(
                                    location.latitude,
                                    location.longitude,
                                    "Selected Location"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Get Weather", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        // Instruction card when no location selected
        if (selectedLocation == null && !showStationPopup) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = PurplePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select a Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PurplePrimary
                        )
                    }
                    Text(
                        text = "📍 Tap anywhere on the map or select a weather station marker to get weather data for that location.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherStationPopupCard(
    station: WeatherStationData,
    onClose: () -> Unit,
    onGetWeather: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = station.icon, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = station.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PurplePrimary
                        )
                        Text(
                            text = "Cambodia",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weather info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${station.temperature}°C",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(text = "Temperature", fontSize = 11.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = station.condition,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(text = "Condition", fontSize = 11.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Additional info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${station.humidity}%", fontSize = 14.sp, color = Color.DarkGray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${station.windSpeed} m/s", fontSize = 14.sp, color = Color.DarkGray)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Get weather button
            Button(
                onClick = onGetWeather,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get Full Weather for ${station.name}", fontWeight = FontWeight.Bold)
            }
        }
    }
}
