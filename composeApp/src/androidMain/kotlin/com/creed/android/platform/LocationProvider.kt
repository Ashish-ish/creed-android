package com.creed.android.platform

import android.content.Context
import com.creed.domain.models.LatLng
import dev.icerock.moko.geo.LocationTracker
//import dev.icerock.moko.geo.LocationTrackerFactory

actual suspend fun getCurrentLocation(onSuccess: (LatLng) -> Unit, onError: (String) -> Unit) {
    // Copilot: Use Moko-Geo for Android
    try {
        // Placeholder: Return dummy location
        onSuccess(LatLng(28.6139, 77.2090))
    } catch (e: Exception) {
        onError(e.message ?: "Location error")
    }
}

