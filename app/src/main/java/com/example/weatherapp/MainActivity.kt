package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.models.AuthState
import com.example.weatherapp.models.User
import com.example.weatherapp.navigation.Screen
import com.example.weatherapp.screens.*
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var authState by remember { 
        mutableStateOf(
            AuthState(
                isAuthenticated = false,
                user = null,
                isGuest = true
            )
        ) 
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.fillMaxSize()
    ) {
        // Splash Screen - First impression, 2-3 seconds
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Simulate successful login
                    authState = AuthState(
                        isAuthenticated = true,
                        user = User(
                            id = "1",
                            name = "John Doe",
                            email = "john@example.com",
                            savedLocations = listOf("New York", "Los Angeles")
                        ),
                        isGuest = false
                    )
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onSkipLogin = {
                    // Continue as guest
                    authState = AuthState(
                        isAuthenticated = false,
                        user = null,
                        isGuest = true
                    )
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Simulate successful registration
                    authState = AuthState(
                        isAuthenticated = true,
                        user = User(
                            id = "1",
                            name = "New User",
                            email = "newuser@example.com",
                            savedLocations = emptyList()
                        ),
                        isGuest = false
                    )
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSkipRegistration = {
                    // Continue as guest
                    authState = AuthState(
                        isAuthenticated = false,
                        user = null,
                        isGuest = true
                    )
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Weather Screen
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
        
        // Map Screen
        composable(Screen.Map.route) {
            MapScreen(
                onLocationSelected = { lat, lon, locationName ->
                    // Navigate back to weather with selected location
                    navController.popBackStack()
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                user = authState.user,
                isGuest = authState.isGuest,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onLogout = {
                    // Logout and return to login
                    authState = AuthState(
                        isAuthenticated = false,
                        user = null,
                        isGuest = true
                    )
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Weather.route) { inclusive = true }
                    }
                }
            )
        }
    }
}