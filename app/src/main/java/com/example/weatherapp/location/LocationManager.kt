package com.example.weatherapp.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Location manager for handling device location
 */
class LocationManager(private val context: Context) {
    
    companion object {
        private const val TAG = "LocationManager"
        private const val LOCATION_TIMEOUT_MS = 10000L // 10 seconds timeout
    }
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        Log.d(TAG, "Permission check - Fine: $hasFine, Coarse: $hasCoarse")
        return hasFine || hasCoarse
    }
    
    /**
     * Get best available location with multiple strategies
     * Tries: Current location -> Last known -> Location updates
     */
    @SuppressLint("MissingPermission")
    suspend fun getBestLocation(): Location? {
        if (!hasLocationPermission()) {
            Log.w(TAG, "No location permission granted")
            return null
        }
        
        Log.d(TAG, "Getting best location...")
        
        // Strategy 1: Try to get current location with timeout
        val currentLocation = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            try {
                getCurrentLocation()
            } catch (e: Exception) {
                Log.w(TAG, "getCurrentLocation failed: ${e.message}")
                null
            }
        }
        
        if (currentLocation != null) {
            Log.d(TAG, "Got current location: ${currentLocation.latitude}, ${currentLocation.longitude}")
            return currentLocation
        }
        
        // Strategy 2: Try last known location
        val lastKnown = try {
            getLastKnownLocation()
        } catch (e: Exception) {
            Log.w(TAG, "getLastKnownLocation failed: ${e.message}")
            null
        }
        
        if (lastKnown != null) {
            Log.d(TAG, "Got last known location: ${lastKnown.latitude}, ${lastKnown.longitude}")
            return lastKnown
        }
        
        // Strategy 3: Request location update
        Log.d(TAG, "Trying location update request...")
        return withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            requestSingleLocationUpdate()
        }
    }
    
    /**
     * Get current device location
     * Returns null if permissions not granted or location unavailable
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                Log.d(TAG, "getCurrentLocation success: $location")
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "getCurrentLocation failed", exception)
                // Resume with null instead of throwing to allow fallback
                continuation.resume(null)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
    
    /**
     * Get last known location (faster but may be stale)
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    Log.d(TAG, "getLastKnownLocation success: $location")
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "getLastKnownLocation failed", exception)
                    continuation.resume(null)
                }
        }
    }
    
    /**
     * Request a single location update (useful when other methods fail)
     */
    @SuppressLint("MissingPermission")
    private suspend fun requestSingleLocationUpdate(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L // 1 second interval
            ).setMaxUpdates(1).build()
            
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val location = result.lastLocation
                    Log.d(TAG, "requestSingleLocationUpdate success: $location")
                    continuation.resume(location)
                }
            }
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnFailureListener { exception ->
                Log.e(TAG, "requestSingleLocationUpdate failed", exception)
                continuation.resume(null)
            }
            
            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }
}
