package com.example.weatherapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// 🇰🇭 Beautiful Cambodia Sky Colors
private val SkyBlueDeep = Color(0xFF2E86AB)
private val SkyBlueMedium = Color(0xFF5BA4CF)
private val SkyBlueLight = Color(0xFF87CEEB)
private val SunriseGold = Color(0xFFFFD700)
private val AngkorGold = Color(0xFFD4AF37)
private val WarmWhite = Color(0xFFFFFEFC)

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Animations
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600),
        label = "logoAlpha"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 400),
        label = "textAlpha"
    )
    
    val textOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 30f,
        animationSpec = tween(800, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "textOffset"
    )
    
    // Sun animation
    val infiniteTransition = rememberInfiniteTransition(label = "sunAnim")
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )
    
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlueDeep,
                        SkyBlueMedium,
                        SkyBlueLight
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = WarmWhite.copy(alpha = 0.05f),
                radius = 300f,
                center = Offset(size.width * 0.2f, size.height * 0.3f)
            )
            drawCircle(
                color = WarmWhite.copy(alpha = 0.03f),
                radius = 200f,
                center = Offset(size.width * 0.8f, size.height * 0.7f)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Sun Logo
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .blur(30.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SunriseGold.copy(alpha = glowPulse),
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
                        .size(100.dp)
                        .rotate(sunRotation)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // App Name
            Column(
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🇰🇭",
                    fontSize = 32.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Cambodia",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmWhite,
                    letterSpacing = 2.sp
                )
                
                Text(
                    text = "WEATHER",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = AngkorGold,
                    letterSpacing = 8.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Tagline
                Text(
                    text = "អាកាសធាតុប្រចាំថ្ងៃ",
                    fontSize = 16.sp,
                    color = WarmWhite.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Beautiful Weather, Beautiful Kingdom",
                    fontSize = 14.sp,
                    color = WarmWhite.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }
        }
        
        // Bottom decoration
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(textAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "─── ◈ ───",
                color = AngkorGold.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Phnom Penh • Siem Reap • Battambang",
                fontSize = 12.sp,
                color = WarmWhite.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
        }
    }
}
