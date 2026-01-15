package com.example.weatherapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * User data model stored in Firestore
 */
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Authentication result wrapper
 */
sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Repository for handling Firebase Authentication and Firestore user data
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    init {
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean = auth.currentUser != null
    
    /**
     * Get current user
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    /**
     * Sign up with email and password
     */
    suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            // Update display name
            user?.let {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                it.updateProfile(profileUpdates).await()
                
                // Save user profile to Firestore
                saveUserProfile(UserProfile(
                    uid = it.uid,
                    email = email,
                    displayName = displayName
                ))
            }
            
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }
    
    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }
    
    /**
     * Sign out
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Update user display name
     */
    suspend fun updateDisplayName(newName: String): AuthResult {
        return try {
            val user = auth.currentUser ?: return AuthResult.Error("Not logged in")
            
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            user.updateProfile(profileUpdates).await()
            
            // Update in Firestore too
            firestore.collection("users")
                .document(user.uid)
                .update("displayName", newName)
                .await()
            
            // Force refresh
            user.reload().await()
            _currentUser.value = auth.currentUser
            
            AuthResult.Success(auth.currentUser)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update name")
        }
    }
    
    /**
     * Update user password
     */
    suspend fun updatePassword(newPassword: String): AuthResult {
        return try {
            val user = auth.currentUser ?: return AuthResult.Error("Not logged in")
            user.updatePassword(newPassword).await()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update password")
        }
    }
    
    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(null)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to send reset email")
        }
    }
    
    /**
     * Save user profile to Firestore
     */
    private suspend fun saveUserProfile(profile: UserProfile) {
        try {
            firestore.collection("users")
                .document(profile.uid)
                .set(profile)
                .await()
        } catch (e: Exception) {
            // Log error but don't fail the auth
            android.util.Log.e("AuthRepository", "Failed to save profile: ${e.message}")
        }
    }
    
    /**
     * Get user profile from Firestore
     */
    suspend fun getUserProfile(): UserProfile? {
        return try {
            val user = auth.currentUser ?: return null
            val doc = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
