package com.example.weatherapp.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

// 🇰🇭 Cambodia-inspired colors
private val CambodiaSkyBlue = Color(0xFF87CEEB)
private val CambodiaSkyDeep = Color(0xFF4A9FCF)
private val AngkorGold = Color(0xFFD4AF37)
private val DeepBlue = Color(0xFF1A365D)

/**
 * 🇰🇭 Cambodia-themed Top Section
 * Bold asymmetric design with Angkor-inspired patterns
 * 
 * Features:
 * - City name + Country with location icon
 * - Current date & time
 * - Current Location button
 * - Subtle Angkor geometric patterns
 */
@Composable
fun HomeTopSection(
    cityName: String,
    countryName: String = "",
    onRefreshClick: () -> Unit = {},
    onCurrentLocationClick: () -> Unit = {},
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Current date/time state - updates every minute
    var currentDateTime by remember { mutableStateOf(getCurrentDateTime()) }
    
    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = getCurrentDateTime()
            kotlinx.coroutines.delay(60_000)
        }
    }
    
    // Pattern animation
    val infiniteTransition = rememberInfiniteTransition(label = "pattern")
    val patternOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "patternOffset"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 16.dp  // Asymmetric design
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CambodiaSkyBlue,
                        CambodiaSkyDeep
                    )
                )
            )
    ) {
        // Angkor-inspired subtle pattern overlay
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .alpha(0.1f)
        ) {
            drawAngkorDecoration(AngkorGold, patternOffset)
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 28.dp)
        ) {
            // Top row with decorative element
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decorative Khmer-style element
                Text(
                    text = "༄",
                    fontSize = 20.sp,
                    color = AngkorGold
                )
                
                // Date badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = currentDateTime.dayOfWeek,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Main Location Row - Bold asymmetric layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Location icon with gold accent ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = AngkorGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // City & Country - Large bold typography
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cityName,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        lineHeight = 36.sp
                    )
                    
                    if (countryName.isNotBlank()) {
                        Text(
                            text = countryName.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AngkorGold,
                            letterSpacing = 3.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Decorative divider with Khmer motif
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Text(
                    text = " ◈ ",
                    color = AngkorGold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date & Time Row with generous spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date & Time
                Column {
                    Text(
                        text = currentDateTime.date,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = currentDateTime.time,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
                
                // Action buttons row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Refresh button
                    FilledIconButton(
                        onClick = onRefreshClick,
                        enabled = !isRefreshing,
                        modifier = Modifier.size(44.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    
                    // Current Location Button - Prominent
                    FilledIconButton(
                        onClick = onCurrentLocationClick,
                        modifier = Modifier.size(44.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AngkorGold,
                            contentColor = DeepBlue
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Current Location",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draw subtle Angkor-inspired decorative pattern
 */
private fun DrawScope.drawAngkorDecoration(color: Color, offset: Float) {
    val spacing = 60f
    
    // Draw lotus-inspired curves and temple spire hints
    for (i in 0..6) {
        val x = i * spacing + offset
        
        // Temple spire shape
        val path = Path().apply {
            moveTo(x, size.height)
            lineTo(x + 15f, size.height - 40f)
            lineTo(x + 30f, size.height)
        }
        drawPath(path, color, style = Stroke(width = 1.5f))
        
        // Small decorative circle
        drawCircle(
            color = color,
            radius = 4f,
            center = Offset(x + 15f, size.height - 50f)
        )
    }
}

/**
 * Data class to hold formatted date and time
 */
private data class DateTimeInfo(
    val date: String,
    val time: String,
    val dayOfWeek: String
)

/**
 * Get current date and time formatted for Cambodia
 */
private fun getCurrentDateTime(): DateTimeInfo {
    val now = Date()
    val locale = Locale.getDefault()
    
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", locale)
    val timeFormat = SimpleDateFormat("h:mm a", locale)
    val dayFormat = SimpleDateFormat("EEEE", locale)
    
    return DateTimeInfo(
        date = dateFormat.format(now),
        time = timeFormat.format(now),
        dayOfWeek = dayFormat.format(now).uppercase()
    )
}

/**
 * Compact version for scrolling state
 */
@Composable
fun CompactHomeTopSection(
    cityName: String,
    countryName: String = "",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CambodiaSkyDeep,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = AngkorGold,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (countryName.isNotBlank()) "$cityName, $countryName" else cityName,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
