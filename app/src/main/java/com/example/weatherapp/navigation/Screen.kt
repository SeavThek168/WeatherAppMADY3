package com.example.weatherapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    // Splash & Auth Screens (no bottom nav)
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Main Screens with Bottom Navigation
    object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    object SavedLocations : Screen(
        route = "saved_locations",
        title = "Saved",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    )
    object Map : Screen(
        route = "map",
        title = "Map",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    )
    object Profile : Screen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    
    // Secondary Screens (no bottom nav item but accessible)
    object Settings : Screen("settings")
    object Legal : Screen("legal")
    object FAQ : Screen("faq")
    object About : Screen("about")
    
    // Legacy route for compatibility
    object Weather : Screen("weather")
    
    companion object {
        val bottomNavItems = listOf(Home, SavedLocations, Map, Profile)
    }
}
