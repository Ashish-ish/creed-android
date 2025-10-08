package com.creed.ios.platform

import platform.CoreLocation.CLLocationManager

/**
 * iOS Location Permission Helper
 * Handles iOS-specific permission requests
 */
class LocationPermissionHelper {
    
    private val locationManager = CLLocationManager()
    private var pendingCallback: ((Boolean) -> Unit)? = null
    
    fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        pendingCallback = onResult
        
        val currentStatus = CLLocationManager.authorizationStatus()
        
        when (currentStatus) {
            platform.CoreLocation.kCLAuthorizationStatusNotDetermined -> {
                // Request permission - result will come via delegate
                locationManager.requestWhenInUseAuthorization()
                // Note: In a real app, you'd set up a delegate to get the result
                // For now, we'll return false and let LocationService handle it
                onResult(false)
            }
            platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse,
            platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways -> {
                onResult(true)
            }
            else -> {
                onResult(false)
            }
        }
        
        pendingCallback = null
    }
}