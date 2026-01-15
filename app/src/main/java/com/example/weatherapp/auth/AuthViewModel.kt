package com.example.weatherapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for authentication screens
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel for handling authentication logic
 */
class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // Check initial auth state
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = user != null,
                    user = user
                )
            }
        }
    }
    
    /**
     * Sign up with email, password and display name
     */
    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.signUp(email, password, displayName)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = result.user,
                        successMessage = "Account created successfully!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Sign in with email and password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = result.user,
                        successMessage = "Welcome back!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Sign out
     */
    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }
    
    /**
     * Update display name
     */
    fun updateDisplayName(newName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.updateDisplayName(newName)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.user,
                        successMessage = "Name updated successfully!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Update password
     */
    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.updatePassword(newPassword)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Password updated successfully!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Password reset email sent!"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
