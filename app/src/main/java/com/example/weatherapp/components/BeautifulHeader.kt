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
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

// 🇰🇭 Beautiful Cambodia Color Palette
private val SunriseGold = Color(0xFFFFD700)
private val AngkorGold = Color(0xFFD4AF37)
private val SkyBlueLight = Color(0xFF87CEEB)
private val SkyBlueMedium = Color(0xFF5BA4CF)
private val SkyBlueDeep = Color(0xFF2E86AB)
private val TropicalTeal = Color(0xFF17A2B8)
private val WarmWhite = Color(0xFFFFFEFC)
private val DeepNavy = Color(0xFF1A365D)

/**
 * 🇰🇭 Beautiful Cambodia Weather Header
 * 
 * A stunning header with:
 * - Animated gradient sky background
 * - Floating sun animation
 * - Location with Cambodian flag hint
 * - Real-time date/time
 * - Current location button
 */
@Composable
fun BeautifulCambodiaHeader(
    cityName: String,
    countryName: String = "Cambodia",
    currentTemp: Int,
    condition: String,
    onCurrentLocationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Time-based state
    var currentDateTime by remember { mutableStateOf(getCurrentDateTimeFormatted()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = getCurrentDateTimeFormatted()
            kotlinx.coroutines.delay(60_000)
        }
    }
    
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "headerAnim")
    
    // Sun rotation animation
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )
    
    // Floating animation for sun
    val sunFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunFloat"
    )
    
    // Glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 48.dp,
                    bottomEnd = 48.dp
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlueDeep,
                        SkyBlueMedium,
                        SkyBlueLight
                    )
                )
            )
    ) {
        // Decorative circles in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Large subtle circles
            drawCircle(
                color = WarmWhite.copy(alpha = 0.05f),
                radius = 200f,
                center = Offset(size.width * 0.8f, size.height * 0.2f)
            )
            drawCircle(
                color = WarmWhite.copy(alpha = 0.03f),
                radius = 150f,
                center = Offset(size.width * 0.1f, size.height * 0.7f)
            )
        }
        
        // Sun glow effect
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 30.dp)
                .offset(y = sunFloat.dp)
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .blur(20.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SunriseGold.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            // Sun icon
            Icon(
                imageVector = Icons.Outlined.WbSunny,
                contentDescription = "Sun",
                tint = SunriseGold,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .rotate(sunRotation)
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = 16.dp)
        ) {
            // Top row: Date badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date badge
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = WarmWhite.copy(alpha = 0.2f),
                    modifier = Modifier.shadow(
                        elevation = 0.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📅",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentDateTime.dayOfWeek,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarmWhite
                        )
                    }
                }
                
                // Current Location Button
                FilledIconButton(
                    onClick = onCurrentLocationClick,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = AngkorGold,
                        contentColor = DeepNavy
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Current Location",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Location Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location pin with glow
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Glow behind icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .blur(8.dp)
                            .background(
                                color = AngkorGold.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = AngkorGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = cityName,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmWhite,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇰🇭",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = countryName.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AngkorGold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom section: Temperature & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Temperature display
                Column {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "$currentTemp",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmWhite,
                            lineHeight = 72.sp
                        )
                        Text(
                            text = "°C",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = WarmWhite.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    // Condition badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WarmWhite.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getConditionEmoji(condition),
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = condition,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = WarmWhite
                            )
                        }
                    }
                }
                
                // Time display
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = currentDateTime.time,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmWhite
                    )
                    Text(
                        text = currentDateTime.date,
                        fontSize = 14.sp,
                        color = WarmWhite.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * Get weather condition emoji
 */
private fun getConditionEmoji(condition: String): String {
    return when {
        condition.contains("sunny", ignoreCase = true) -> "☀️"
        condition.contains("clear", ignoreCase = true) -> "🌤️"
        condition.contains("cloud", ignoreCase = true) -> "⛅"
        condition.contains("overcast", ignoreCase = true) -> "☁️"
        condition.contains("rain", ignoreCase = true) -> "🌧️"
        condition.contains("storm", ignoreCase = true) -> "⛈️"
        condition.contains("thunder", ignoreCase = true) -> "🌩️"
        else -> "🌤️"
    }
}

/**
 * DateTime data class
 */
private data class FormattedDateTime(
    val date: String,
    val time: String,
    val dayOfWeek: String
)

/**
 * Get formatted date and time
 */
private fun getCurrentDateTimeFormatted(): FormattedDateTime {
    val now = Date()
    val locale = Locale.getDefault()
    
    return FormattedDateTime(
        date = SimpleDateFormat("MMM d, yyyy", locale).format(now),
        time = SimpleDateFormat("h:mm a", locale).format(now),
        dayOfWeek = SimpleDateFormat("EEEE", locale).format(now)
    )
}
