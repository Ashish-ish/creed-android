package com.creed.data.location

import kotlinx.cinterop.ExperimentalForeignApi

actual class PlatformLocationPermissionHelper {

    actual fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        // TODO: Implement using CLLocationManager in Swift/Objective-C interop
        println("⚠️ iOS location permission not implemented yet")
        onResult(false)
    }
}