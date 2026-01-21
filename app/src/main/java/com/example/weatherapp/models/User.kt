package com.example.weatherapp.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val savedLocations: List<String> = emptyList(),
    val isGuest: Boolean = false
)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isGuest: Boolean = true
)
