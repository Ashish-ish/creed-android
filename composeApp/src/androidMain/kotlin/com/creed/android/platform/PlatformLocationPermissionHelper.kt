package com.creed.android.platform

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

@Composable
actual fun rememberPlatformPermissionHelper(): PlatformLocationPermissionHelper {
    // Get the helper from Koin - it was created in Activity.onCreate()
    val helper = koinInject<LocationPermissionHelper>()
    return AndroidLocationPermissionHelper(helper)
}

private class AndroidLocationPermissionHelper(
    private val helper: LocationPermissionHelper
) : PlatformLocationPermissionHelper {

    override fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        helper.requestLocationPermission(onResult)
    }
}