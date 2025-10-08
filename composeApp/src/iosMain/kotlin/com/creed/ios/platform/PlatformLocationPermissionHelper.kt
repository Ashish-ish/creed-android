package com.creed.ios.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.creed.android.platform.PlatformLocationPermissionHelper

@Composable
actual fun rememberPlatformPermissionHelper(): PlatformLocationPermissionHelper {
    // Create and remember the helper instance
    val helper = remember { LocationPermissionHelper() }
    return remember(helper) { IosLocationPermissionHelper(helper) }
}

private class IosLocationPermissionHelper(
    private val helper: LocationPermissionHelper
) : PlatformLocationPermissionHelper {

    override fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        helper.requestLocationPermission(onResult)
    }
}