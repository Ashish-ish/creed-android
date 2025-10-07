package com.creed.android.platform

import androidx.compose.runtime.Composable

interface PlatformLocationPermissionHelper {
    fun requestLocationPermission(onResult: (Boolean) -> Unit)
}

@Composable
expect fun rememberPlatformPermissionHelper(): PlatformLocationPermissionHelper