package com.creed.data.location

import com.creed.domain.models.LatLng
import kotlinx.cinterop.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class LocationService {

    private val _locationState = MutableStateFlow(LocationState())
    actual val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    private val locationManager = CLLocationManager()
    private val delegate: LocationManagerDelegate

    init {
        delegate = LocationManagerDelegate(
            onLocationUpdate = { location ->
                _locationState.value = _locationState.value.copy(
                    currentLocation = location,
                    isLoading = false,
                    error = null,
                    permissionStatus = LocationPermissionStatus.GRANTED
                )
            },
            onError = { error ->
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = error
                )
            },
            onAuthorizationChange = { status ->
                _locationState.value = _locationState.value.copy(
                    permissionStatus = status
                )
            }
        )

        locationManager.delegate = delegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = kCLDistanceFilterNone
    }

    actual fun checkPermissionStatus(): LocationPermissionStatus {
        val authStatus = CLLocationManager.authorizationStatus()
        return when (authStatus) {
            kCLAuthorizationStatusNotDetermined -> LocationPermissionStatus.NOT_DETERMINED
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> LocationPermissionStatus.GRANTED
            kCLAuthorizationStatusRestricted -> LocationPermissionStatus.RESTRICTED
            kCLAuthorizationStatusDenied -> LocationPermissionStatus.DENIED
            else -> LocationPermissionStatus.NOT_DETERMINED
        }
    }

    actual suspend fun requestPermission() {
        val currentStatus = checkPermissionStatus()

        when (currentStatus) {
            LocationPermissionStatus.NOT_DETERMINED -> {
                locationManager.requestWhenInUseAuthorization()
            }
            LocationPermissionStatus.DENIED -> {
                _locationState.value = _locationState.value.copy(
                    permissionStatus = LocationPermissionStatus.DENIED,
                    error = "Location permission denied. Please enable in Settings."
                )
            }
            LocationPermissionStatus.RESTRICTED -> {
                _locationState.value = _locationState.value.copy(
                    permissionStatus = LocationPermissionStatus.RESTRICTED,
                    error = "Location access is restricted on this device."
                )
            }
            LocationPermissionStatus.GRANTED -> {
                _locationState.value = _locationState.value.copy(
                    permissionStatus = LocationPermissionStatus.GRANTED
                )
            }
        }
    }

    actual suspend fun getCurrentLocation() {
        val permissionStatus = checkPermissionStatus()

        if (permissionStatus != LocationPermissionStatus.GRANTED) {
            _locationState.value = _locationState.value.copy(
                permissionStatus = permissionStatus,
                error = when (permissionStatus) {
                    LocationPermissionStatus.NOT_DETERMINED -> "Location permission not requested yet."
                    LocationPermissionStatus.DENIED -> "Location permission denied. Please enable in Settings."
                    LocationPermissionStatus.RESTRICTED -> "Location access is restricted."
                    else -> "Location permission not available."
                },
                isLoading = false
            )
            return
        }

        if (!CLLocationManager.locationServicesEnabled()) {
            _locationState.value = _locationState.value.copy(
                error = "Location services are disabled. Please enable them in Settings.",
                isLoading = false
            )
            return
        }

        _locationState.value = _locationState.value.copy(
            isLoading = true,
            error = null
        )

        // Request a single location update
        locationManager.requestLocation()
    }

    @OptIn(ExperimentalForeignApi::class)
    private class LocationManagerDelegate(
        private val onLocationUpdate: (LatLng) -> Unit,
        private val onError: (String) -> Unit,
        private val onAuthorizationChange: (LocationPermissionStatus) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {

        override fun locationManager(
            manager: CLLocationManager,
            didUpdateLocations: List<*>
        ) {
            val locations = didUpdateLocations as List<CLLocation>
            val location = locations.lastOrNull()

            location?.let {
                val coordinate = it.coordinate.useContents {
                    LatLng(
                        latitude = this.latitude,
                        longitude = this.longitude
                    )
                }
                onLocationUpdate(coordinate)
            }
        }

        override fun locationManager(
            manager: CLLocationManager,
            didFailWithError: NSError
        ) {
            val errorMessage = when (didFailWithError.code) {
                kCLErrorDenied.toLong() -> "Location access denied by user"
                kCLErrorLocationUnknown.toLong() -> "Location currently unknown, trying again..."
                kCLErrorNetwork.toLong() -> "Network error while fetching location"
                else -> didFailWithError.localizedDescription
            }
            onError(errorMessage)
        }

        override fun locationManager(
            manager: CLLocationManager,
            didChangeAuthorizationStatus: CLAuthorizationStatus
        ) {
            val status = mapAuthorizationStatus(didChangeAuthorizationStatus)
            onAuthorizationChange(status)
        }

        @Suppress("CONFLICTING_OVERLOADS")
        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            val status = mapAuthorizationStatus(manager.authorizationStatus)
            onAuthorizationChange(status)
        }

        private fun mapAuthorizationStatus(status: CLAuthorizationStatus): LocationPermissionStatus {
            return when (status) {
                kCLAuthorizationStatusNotDetermined -> LocationPermissionStatus.NOT_DETERMINED
                kCLAuthorizationStatusAuthorizedWhenInUse,
                kCLAuthorizationStatusAuthorizedAlways -> LocationPermissionStatus.GRANTED
                kCLAuthorizationStatusRestricted -> LocationPermissionStatus.RESTRICTED
                kCLAuthorizationStatusDenied -> LocationPermissionStatus.DENIED
                else -> LocationPermissionStatus.NOT_DETERMINED
            }
        }
    }
}