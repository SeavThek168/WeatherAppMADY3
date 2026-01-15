package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.weatherapp.auth.AuthViewModel
import com.example.weatherapp.navigation.Screen
import com.example.weatherapp.screens.*
import com.example.weatherapp.ui.theme.WeatherAppTheme

// App colors
private val PurplePrimary = Color(0xFF667eea)
private val PurpleSecondary = Color(0xFF764ba2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                CamWeatherApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamWeatherApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Shared AuthViewModel for the app
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()
    
    // Saved locations state
    var savedLocations by remember { 
        mutableStateOf(
            listOf(
                SavedLocation("Phnom Penh", "Cambodia", 11.5564, 104.9282, true),
                SavedLocation("Siem Reap", "Cambodia", 13.3633, 103.8564, false),
                SavedLocation("Battambang", "Cambodia", 13.1023, 103.1962, false)
            )
        ) 
    }
    
    // Determine if we should show bottom nav
    val showBottomNav = currentDestination?.route in Screen.bottomNavItems.map { it.route }
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                CamWeatherBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    authViewModel = authViewModel
                )
            }
            
            // Register Screen (redirect to Login since it's combined now)
            composable(Screen.Register.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    authViewModel = authViewModel
                )
            }
            
            // Home/Weather Screen (Main Tab)
            composable(Screen.Home.route) {
                WeatherScreen(
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.route)
                    }
                )
            }
            
            // Legacy Weather route for compatibility
            composable(Screen.Weather.route) {
                WeatherScreen(
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.route)
                    }
                )
            }
            
            // Saved Locations Screen
            composable(Screen.SavedLocations.route) {
                SavedLocationsScreen(
                    savedLocations = savedLocations,
                    onLocationClick = { location ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onAddLocation = {
                        navController.navigate(Screen.Map.route)
                    },
                    onDeleteLocation = { location ->
                        savedLocations = savedLocations.filter { it.name != location.name }
                    },
                    onSetDefault = { location ->
                        savedLocations = savedLocations.map { 
                            it.copy(isDefault = it.name == location.name)
                        }
                    },
                    isGuest = !authState.isLoggedIn,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            
            // Map Screen
            composable(Screen.Map.route) {
                MapScreen(
                    onLocationSelected = { lat, lon, locationName ->
                        val newLocation = SavedLocation(
                            name = locationName,
                            country = "Cambodia",
                            lat = lat,
                            lon = lon,
                            isDefault = false
                        )
                        if (!savedLocations.any { it.name == locationName }) {
                            savedLocations = savedLocations + newLocation
                        }
                        navController.navigate(Screen.Home.route)
                    },
                    onClose = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Profile Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToFAQ = {
                        navController.navigate(Screen.FAQ.route)
                    },
                    onNavigateToLegal = {
                        navController.navigate(Screen.Legal.route)
                    },
                    onNavigateToAbout = {
                        navController.navigate(Screen.About.route)
                    },
                    authViewModel = authViewModel
                )
            }
            
            // Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // FAQ Screen
            composable(Screen.FAQ.route) {
                FAQScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Legal Screen
            composable(Screen.Legal.route) {
                LegalScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // About Screen
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun CamWeatherBottomBar(
    navController: androidx.navigation.NavController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = PurplePrimary,
        tonalElevation = 8.dp
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurplePrimary,
                    selectedTextColor = PurplePrimary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = PurplePrimary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// Data class for saved locations
data class SavedLocation(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isDefault: Boolean = false
)