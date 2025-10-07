package com.creed.android.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creed.data.location.LocationPermissionStatus
import com.creed.data.location.LocationService
import com.creed.domain.models.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationPermissionState(
    val isLoading: Boolean = false,
    val permissionStatus: LocationPermissionStatus = LocationPermissionStatus.NOT_DETERMINED,
    val currentLocation: LatLng? = null,
    val error: String? = null,
    val shouldNavigateToHome: Boolean = false
)

class LocationPermissionViewModel(
    private val locationService: LocationService
) : ViewModel() {

    private val _state = MutableStateFlow(LocationPermissionState())
    val state: StateFlow<LocationPermissionState> = _state.asStateFlow()

    init {
        checkInitialPermissionStatus()
        observeLocationState()
    }

    private fun checkInitialPermissionStatus() {
        val permissionStatus = locationService.checkPermissionStatus()
        _state.value = _state.value.copy(permissionStatus = permissionStatus)

        if (permissionStatus == LocationPermissionStatus.GRANTED) {
            requestCurrentLocation()
        }
    }

    private fun observeLocationState() {
        viewModelScope.launch {
            locationService.locationState.collect { locationState ->
                _state.value = _state.value.copy(
                    isLoading = locationState.isLoading,
                    permissionStatus = locationState.permissionStatus,
                    currentLocation = locationState.currentLocation,
                    error = locationState.error
                )

                // Auto-navigate when location is successfully retrieved
                if (locationState.permissionStatus == LocationPermissionStatus.GRANTED &&
                    locationState.currentLocation != null &&
                    !locationState.isLoading) {
                    _state.value = _state.value.copy(shouldNavigateToHome = true)
                }
            }
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            try {
                locationService.requestPermission()
                locationService.getCurrentLocation()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to get location: ${e.message}"
                )
            }
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            locationService.requestPermission()
        }
    }

    fun requestCurrentLocation() {
        viewModelScope.launch {
            try {
                locationService.getCurrentLocation()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to get location: ${e.message}"
                )
            }
        }
    }

    fun skipLocationPermission() {
        _state.value = _state.value.copy(shouldNavigateToHome = true)
    }

    fun clearNavigationFlag() {
        _state.value = _state.value.copy(shouldNavigateToHome = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}