package com.creed.ios.platform

import androidx.compose.runtime.Composable
import com.creed.android.platform.PlatformLocationPermissionHelper


@Composable
actual fun rememberPlatformPermissionHelper(): PlatformLocationPermissionHelper {
    return IosLocationPermissionHelper()
}

private class IosLocationPermissionHelper : PlatformLocationPermissionHelper {
    override fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        println("⚠️ iOS location permission not implemented yet")
        onResult(false)
    }
}