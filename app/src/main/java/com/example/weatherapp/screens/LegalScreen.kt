package com.example.weatherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// CamWeather theme colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Terms of Service", "Privacy Policy", "Licenses")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "📜 Legal",
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
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PurplePrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }
        
        // Content
        when (selectedTab) {
            0 -> TermsOfServiceContent()
            1 -> PrivacyPolicyContent()
            2 -> LicensesContent()
        }
    }
}

@Composable
private fun TermsOfServiceContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LegalSectionCard(
                icon = Icons.Default.Gavel,
                title = "Terms of Service",
                lastUpdated = "Last updated: January 2025"
            ) {
                LegalParagraph(
                    title = "1. Acceptance of Terms",
                    content = "By downloading, installing, or using the CamWeather application, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use the application."
                )
                
                LegalParagraph(
                    title = "2. Description of Service",
                    content = "CamWeather provides weather information, forecasts, and related services. The service is provided 'as is' and we make no guarantees about the accuracy, reliability, or availability of weather data."
                )
                
                LegalParagraph(
                    title = "3. User Accounts",
                    content = "Some features require creating an account. You are responsible for maintaining the confidentiality of your account credentials and for all activities under your account. You must provide accurate information when creating an account."
                )
                
                LegalParagraph(
                    title = "4. Acceptable Use",
                    content = "You agree to use CamWeather only for lawful purposes. You may not:\n• Attempt to gain unauthorized access to the service\n• Use the service to distribute malware or spam\n• Reverse engineer or decompile the application\n• Use automated systems to access the service"
                )
                
                LegalParagraph(
                    title = "5. Intellectual Property",
                    content = "All content, trademarks, and data on CamWeather are the property of CamWeather or its licensors. You may not copy, modify, or distribute our content without permission."
                )
                
                LegalParagraph(
                    title = "6. Limitation of Liability",
                    content = "CamWeather shall not be liable for any damages arising from the use of our service, including but not limited to decisions made based on weather forecasts. Weather predictions are inherently uncertain."
                )
                
                LegalParagraph(
                    title = "7. Changes to Terms",
                    content = "We reserve the right to modify these terms at any time. Continued use of the service after changes constitutes acceptance of the new terms."
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PrivacyPolicyContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LegalSectionCard(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                lastUpdated = "Last updated: January 2025"
            ) {
                LegalParagraph(
                    title = "1. Information We Collect",
                    content = "• Location Data: To provide accurate weather forecasts\n• Account Information: Email, name when you register\n• Usage Data: How you interact with the app\n• Device Information: Device type, OS version for compatibility"
                )
                
                LegalParagraph(
                    title = "2. How We Use Your Information",
                    content = "• Provide personalized weather forecasts\n• Improve our services and user experience\n• Send weather alerts and notifications (with your consent)\n• Analyze usage patterns to enhance features"
                )
                
                LegalParagraph(
                    title = "3. Data Sharing",
                    content = "We do not sell your personal information. We may share data with:\n• Service providers who help operate our app\n• Legal authorities when required by law\n• Weather data providers (anonymized location data only)"
                )
                
                LegalParagraph(
                    title = "4. Data Security",
                    content = "We implement industry-standard security measures to protect your data, including encryption in transit and at rest. However, no method of transmission over the internet is 100% secure."
                )
                
                LegalParagraph(
                    title = "5. Your Rights",
                    content = "You have the right to:\n• Access your personal data\n• Request correction of inaccurate data\n• Delete your account and associated data\n• Opt out of marketing communications\n• Export your data in a portable format"
                )
                
                LegalParagraph(
                    title = "6. Cookies and Tracking",
                    content = "We use minimal tracking technologies to improve app performance and user experience. You can control these through your device settings."
                )
                
                LegalParagraph(
                    title = "7. Children's Privacy",
                    content = "CamWeather is not intended for children under 13. We do not knowingly collect information from children under 13 years of age."
                )
                
                LegalParagraph(
                    title = "8. Contact Us",
                    content = "For privacy-related inquiries, contact us at:\nprivacy@camweather.com"
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun LicensesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = PurplePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Open Source Licenses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary
                        )
                        Text(
                            text = "Third-party libraries used in CamWeather",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        item {
            LicenseCard(
                name = "Jetpack Compose",
                author = "Google",
                license = "Apache License 2.0",
                description = "Modern toolkit for building native Android UI"
            )
        }
        
        item {
            LicenseCard(
                name = "Retrofit",
                author = "Square, Inc.",
                license = "Apache License 2.0",
                description = "Type-safe HTTP client for Android"
            )
        }
        
        item {
            LicenseCard(
                name = "OkHttp",
                author = "Square, Inc.",
                license = "Apache License 2.0",
                description = "HTTP client for Android and Java"
            )
        }
        
        item {
            LicenseCard(
                name = "Google Maps SDK",
                author = "Google",
                license = "Google APIs Terms of Service",
                description = "Maps and location services for Android"
            )
        }
        
        item {
            LicenseCard(
                name = "Coil",
                author = "Coil Contributors",
                license = "Apache License 2.0",
                description = "Image loading library for Android"
            )
        }
        
        item {
            LicenseCard(
                name = "Material Design 3",
                author = "Google",
                license = "Apache License 2.0",
                description = "Material Design components for Android"
            )
        }
        
        item {
            LicenseCard(
                name = "Accompanist",
                author = "Google",
                license = "Apache License 2.0",
                description = "Companion libraries for Jetpack Compose"
            )
        }
        
        item {
            LicenseCard(
                name = "DataStore",
                author = "Google",
                license = "Apache License 2.0",
                description = "Data storage solution for Android"
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun LegalSectionCard(
    icon: ImageVector,
    title: String,
    lastUpdated: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lastUpdated,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
private fun LegalParagraph(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PurplePrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun LicenseCard(
    name: String,
    author: String,
    license: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PurplePrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = license,
                        style = MaterialTheme.typography.labelSmall,
                        color = PurplePrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "by $author",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}
