package com.creed.data.location

import com.creed.domain.models.LatLng
import kotlinx.coroutines.flow.StateFlow

enum class LocationPermissionStatus {
    NOT_DETERMINED,
    GRANTED,
    DENIED,
    RESTRICTED
}

data class LocationState(
    val permissionStatus: LocationPermissionStatus = LocationPermissionStatus.NOT_DETERMINED,
    val currentLocation: LatLng? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

expect class LocationService {
    val locationState: StateFlow<LocationState>
    
    suspend fun requestPermission()
    suspend fun getCurrentLocation()
    fun checkPermissionStatus(): LocationPermissionStatus
}