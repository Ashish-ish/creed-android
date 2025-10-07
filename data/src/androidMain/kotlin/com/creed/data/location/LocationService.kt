package com.creed.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.creed.domain.models.LatLng
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class LocationService(private val context: Context) {
    
    private val _locationState = MutableStateFlow(LocationState())
    actual val locationState: StateFlow<LocationState> = _locationState.asStateFlow()
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L
    ).apply {
        setMinUpdateIntervalMillis(5000L)
        setMaxUpdateDelayMillis(10000L)
    }.build()

    actual fun checkPermissionStatus(): LocationPermissionStatus {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return when {
            fineLocationGranted || coarseLocationGranted -> LocationPermissionStatus.GRANTED
            else -> {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val isLocationEnabled = locationManager?.let {
                    it.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                } ?: false

                if (!isLocationEnabled) {
                    LocationPermissionStatus.RESTRICTED
                } else {
                    LocationPermissionStatus.DENIED
                }
            }
        }
    }

    actual suspend fun requestPermission() {
        val status = checkPermissionStatus()
        _locationState.value = _locationState.value.copy(
            permissionStatus = status
        )
    }
    
    actual suspend fun getCurrentLocation() {
        val permissionStatus = checkPermissionStatus()

        if (permissionStatus != LocationPermissionStatus.GRANTED) {
            _locationState.value = _locationState.value.copy(
                permissionStatus = permissionStatus,
                error = "Location permission not granted. Please enable location permissions in settings.",
                isLoading = false
            )
            return
        }
        
        _locationState.value = _locationState.value.copy(
            isLoading = true,
            error = null
        )

        try {
            val location = getLastKnownLocation()

            if (location != null) {
                _locationState.value = _locationState.value.copy(
                    currentLocation = location,
                    permissionStatus = LocationPermissionStatus.GRANTED,
                    isLoading = false,
                    error = null
                )
            } else {
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = "Unable to get current location. Please try again."
                )
            }
        } catch (e: SecurityException) {
            _locationState.value = _locationState.value.copy(
                isLoading = false,
                error = "Location permission denied: ${e.message}",
                permissionStatus = LocationPermissionStatus.DENIED
            )
        } catch (e: Exception) {
            _locationState.value = _locationState.value.copy(
                isLoading = false,
                error = e.message ?: "Unknown error occurred while fetching location"
            )
        }
    }
    
    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocation(): LatLng? = suspendCancellableCoroutine { continuation ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(LatLng(location.latitude, location.longitude))
                    } else {
                        // Last location not available, request fresh location
                        requestFreshLocation { freshLocation ->
                            continuation.resume(freshLocation)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
                .addOnCanceledListener {
                    continuation.resume(null)
                }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    @Suppress("MissingPermission")
    private fun requestFreshLocation(callback: (LatLng?) -> Unit) {
        try {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        callback(LatLng(location.latitude, location.longitude))
                        fusedLocationClient.removeLocationUpdates(this)
                    } ?: callback(null)
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        callback(null)
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        } catch (e: SecurityException) {
            callback(null)
        } catch (e: Exception) {
            callback(null)
        }
    }
}