package com.example.weatherapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Weather : Screen("weather")
    object Profile : Screen("profile")
    object Map : Screen("map")
}
