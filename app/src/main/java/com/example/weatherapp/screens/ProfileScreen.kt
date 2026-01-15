package com.example.weatherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.models.User

// CamWeather theme colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

@Composable
fun ProfileScreen(
    user: User?,
    isGuest: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToFAQ: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(rememberScrollState())
    ) {
        // Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PurplePrimary, PurpleSecondary)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo/Title
                Text(
                    text = "🇰🇭",
                    fontSize = 32.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "CamWeather",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGuest) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Guest",
                            modifier = Modifier.size(48.dp),
                            tint = PurplePrimary
                        )
                    } else {
                        Text(
                            text = user?.name?.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary,
                            fontSize = 36.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (isGuest) "Guest User" else user?.name ?: "User",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                if (!isGuest && user?.email != null) {
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Guest Mode Card
        if (isGuest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Guest Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Sign in to save locations and sync data",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE65100).copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Login Button
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign In / Create Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Account Section
        if (!isGuest) {
            ProfileSectionCard(title = "Account") {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update your information",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Weather alerts & updates",
                    onClick = { }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // App Settings Section
        ProfileSectionCard(title = "Settings") {
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Preferences",
                subtitle = "Units, language & more",
                onClick = onNavigateToSettings
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.Palette,
                title = "Appearance",
                subtitle = "Theme & display options",
                onClick = onNavigateToSettings
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Support Section
        ProfileSectionCard(title = "Support") {
            ProfileMenuItem(
                icon = Icons.Default.HelpOutline,
                title = "FAQ",
                subtitle = "Frequently asked questions",
                onClick = onNavigateToFAQ
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.Email,
                title = "Contact Us",
                subtitle = "Get help & support",
                onClick = { }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.BugReport,
                title = "Report a Problem",
                subtitle = "Help us improve",
                onClick = { }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Legal Section
        ProfileSectionCard(title = "Legal") {
            ProfileMenuItem(
                icon = Icons.Default.Description,
                title = "Terms of Service",
                subtitle = "Usage terms & conditions",
                onClick = onNavigateToLegal
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = onNavigateToLegal
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.Gavel,
                title = "Licenses",
                subtitle = "Open source licenses",
                onClick = onNavigateToLegal
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // About Section
        ProfileSectionCard(title = "About") {
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "About CamWeather",
                subtitle = "Version 1.0.0",
                onClick = onNavigateToAbout
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.Star,
                title = "Rate App",
                subtitle = "Share your feedback",
                onClick = { }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon = Icons.Default.Share,
                title = "Share App",
                subtitle = "Tell friends about CamWeather",
                onClick = { }
            )
        }
        
        // Logout Button
        if (!isGuest) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Footer
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "CamWeather © 2026",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Text(
            text = "Made with ❤️ in Cambodia",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PurplePrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PurplePrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color.Gray.copy(alpha = 0.5f)
        )
    }
}
