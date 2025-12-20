package com.example.weatherapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

// Colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)
private val TextWhite = Color(0xFFFFFFFF)

// Cambodia weather station locations
private val weatherStations = listOf(
    WeatherStation("Khan Sen Sok", LatLng(11.5868, 104.8869), 29, "Overcast", "☁️"),
    WeatherStation("Phnom Penh", LatLng(11.5564, 104.9282), 29, "Overcast", "☁️"),
    WeatherStation("Siem Reap", LatLng(13.3633, 103.8564), 31, "Sunny", "☀️"),
    WeatherStation("Battambang", LatLng(13.1023, 103.1962), 30, "Partly Cloudy", "⛅"),
    WeatherStation("Sihanoukville", LatLng(10.6093, 103.5296), 28, "Cloudy", "☁️"),
    WeatherStation("Kampot", LatLng(10.5940, 104.1640), 27, "Clear", "🌤️"),
    WeatherStation("Kratie", LatLng(12.4880, 106.0189), 32, "Hot", "🔥"),
    WeatherStation("Koh Kong", LatLng(11.6150, 102.9840), 26, "Rainy", "🌧️")
)

data class WeatherStation(
    val name: String,
    val location: LatLng,
    val temperature: Int,
    val condition: String,
    val icon: String
)

// Custom map style for dark/weather theme
private val mapStyleJson = """
[
  {
    "elementType": "geometry",
    "stylers": [{"color": "#f5f5f5"}]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#616161"}]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [{"color": "#f5f5f5"}]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{"color": "#c9e4f6"}]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#9e9e9e"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{"color": "#ffffff"}]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [{"color": "#c8e6c9"}]
  }
]
"""

@Composable
fun WeatherMapCard(
    modifier: Modifier = Modifier,
    currentLocation: LatLng = LatLng(11.5868, 104.8869), // Default: Khan Sen Sok, Phnom Penh
    onExpandMap: () -> Unit = {},
    onLocationClick: (LatLng) -> Unit = {},
    onCurrentLocationRequest: () -> Unit = {}
) {
    var selectedLayer by remember { mutableStateOf("Temp") }
    var selectedStation by remember { mutableStateOf<WeatherStation?>(null) }
    var showStationPopup by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 7f)
    }
    
    // Update camera when location changes
    LaunchedEffect(currentLocation) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(currentLocation, 10f),
            durationMs = 1000
        )
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
                            Text(
                                text = "Weather Map",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapLayerChip("🌡️", "Temperature", selectedLayer == "Temp") { selectedLayer = "Temp" }
                    MapLayerChip("🌧️", "Precipitation", selectedLayer == "Precip") { selectedLayer = "Precip" }
                    MapLayerChip("💨", "Wind", selectedLayer == "Wind") { selectedLayer = "Wind" }
                    MapLayerChip("☁️", "Clouds", selectedLayer == "Clouds") { selectedLayer = "Clouds" }
                }
                
                // Map
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = true,
                            mapType = MapType.NORMAL,
                            mapStyleOptions = try {
                                MapStyleOptions(mapStyleJson)
                            } catch (e: Exception) {
                                null
                            }
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = false,
                            mapToolbarEnabled = false,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true,
                            compassEnabled = true
                        ),
                        onMapClick = { latLng ->
                            showStationPopup = false
                            selectedStation = null
                            onLocationClick(latLng)
                        }
                    ) {
                        // Current location marker
                        Marker(
                            state = MarkerState(position = currentLocation),
                            title = "Current Location",
                            snippet = "You are here",
                            onClick = {
                                showStationPopup = false
                                selectedStation = null
                                true
                            }
                        )
                        
                        // Weather station markers
                        weatherStations.forEach { station ->
                            Marker(
                                state = MarkerState(position = station.location),
                                title = station.name,
                                snippet = "${station.temperature}°C - ${station.condition}",
                                onClick = {
                                    selectedStation = station
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
                    }
                    
                    // Current Location Button
                    FloatingActionButton(
                        onClick = onCurrentLocationRequest,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(48.dp),
                        containerColor = PurplePrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My Location",
                            tint = Color.White
                        )
                    }
                    
                    // Zoom Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.zoomIn(),
                                        durationMs = 300
                                    )
                                }
                            },
                            modifier = Modifier.size(36.dp),
                            containerColor = Color.White
                        ) {
                            Icon(Icons.Default.Add, "Zoom In", tint = PurplePrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.zoomOut(),
                                        durationMs = 300
                                    )
                                }
                            },
                            modifier = Modifier.size(36.dp),
                            containerColor = Color.White
                        ) {
                            Icon(Icons.Default.Remove, "Zoom Out", tint = PurplePrimary)
                        }
                    }
                    
                    // Weather Station Popup
                    if (showStationPopup && selectedStation != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        ) {
                            selectedStation?.let { station ->
                                WeatherStationPopup(
                                    station = station,
                                    onClose = {
                                        showStationPopup = false
                                        selectedStation = null
                                    },
                                    onSelect = {
                                        onLocationClick(station.location)
                                        showStationPopup = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStationPopup(
    station: WeatherStation,
    onClose: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = station.icon, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = station.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
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
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${station.temperature}°C",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Temperature",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = station.condition,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "Condition",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get Weather for ${station.name}")
            }
        }
    }
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
