package com.example.weatherapp.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// CamWeather theme colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

data class FAQItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faqItems = remember {
        listOf(
            FAQItem(
                question = "How accurate is CamWeather's forecast?",
                answer = "CamWeather uses data from OpenWeatherMap, one of the world's leading weather data providers. Our forecasts are updated every 3 hours and typically have an accuracy of 80-90% for short-term predictions (1-3 days). Long-term forecasts (7+ days) may have lower accuracy as weather patterns become harder to predict."
            ),
            FAQItem(
                question = "Why does the location shown seem incorrect?",
                answer = "CamWeather uses your device's GPS and network location services. For the best accuracy, ensure:\n• GPS is enabled on your device\n• Location permission is granted to CamWeather\n• You're in an area with good GPS signal\n\nIndoor locations or areas with tall buildings may affect GPS accuracy."
            ),
            FAQItem(
                question = "How do I save a location?",
                answer = "To save a location:\n1. Search for the location using the search bar on the home screen\n2. Once the weather for that location is displayed, tap the bookmark icon\n3. The location will be saved to your 'Saved Locations' list\n\nNote: You must be signed in to save locations."
            ),
            FAQItem(
                question = "What do the weather icons mean?",
                answer = "CamWeather uses intuitive icons to represent weather conditions:\n☀️ Clear/Sunny\n⛅ Partly Cloudy\n☁️ Cloudy/Overcast\n🌧️ Rain\n⛈️ Thunderstorm\n❄️ Snow\n🌫️ Fog/Mist\n💨 Windy"
            ),
            FAQItem(
                question = "How do I change temperature units?",
                answer = "To switch between Celsius and Fahrenheit:\n1. Go to Profile > Settings\n2. Under 'Units' section, toggle the Temperature Unit switch\n3. Your preference will be saved automatically"
            ),
            FAQItem(
                question = "Does CamWeather work offline?",
                answer = "CamWeather caches recent weather data so you can view previously loaded forecasts offline. However, to get updated weather information, an internet connection is required. When offline, you'll see the last updated timestamp on the weather data."
            ),
            FAQItem(
                question = "Why is my battery draining faster?",
                answer = "Weather apps use location services which can consume battery. To optimize battery usage:\n• Disable auto-refresh in Settings\n• Use manual location updates instead of continuous GPS\n• Reduce notification frequency\n\nCamWeather is designed to be battery-efficient, but frequent location updates will use more power."
            ),
            FAQItem(
                question = "How do I report incorrect weather data?",
                answer = "If you notice persistent inaccuracies:\n1. Check that your location is correctly detected\n2. Refresh the weather data\n3. If the issue persists, contact us at support@camweather.com with:\n   • Your location\n   • The reported vs actual conditions\n   • Screenshots if possible"
            ),
            FAQItem(
                question = "Is CamWeather free to use?",
                answer = "Yes! CamWeather is completely free to use. All core features including current weather, forecasts, saved locations, and weather maps are available at no cost. We may introduce optional premium features in the future, but the essential weather functionality will always remain free."
            ),
            FAQItem(
                question = "How do I delete my account?",
                answer = "To delete your account and all associated data:\n1. Go to Profile\n2. Scroll down to 'Account' section\n3. Tap 'Delete Account'\n4. Confirm the deletion\n\nNote: This action is permanent and cannot be undone. All saved locations and preferences will be deleted."
            )
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "❓ FAQ",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PurplePrimary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PurplePrimary.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Frequently Asked Questions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                            Text(
                                text = "Find answers to common questions",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // FAQ Items
            items(faqItems) { faq ->
                ExpandableFAQCard(faq = faq)
            }
            
            // Contact Support Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Still have questions?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Our support team is here to help",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { /* Open email or support chat */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PurplePrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contact Support")
                        }
                    }
                }
            }
            
            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ExpandableFAQCard(faq: FAQItem) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = PurplePrimary
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
