package com.creed.ios.platform

import com.creed.domain.models.LatLng

actual suspend fun getCurrentLocation(onSuccess: (LatLng) -> Unit, onError: (String) -> Unit) {
    // Copilot: Use Moko-Geo for iOS
    onSuccess(LatLng(28.6139, 77.2090)) // Placeholder
}

