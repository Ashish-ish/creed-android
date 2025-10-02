package com.creed.android.platform

import com.creed.domain.models.LatLng

/**
 * Expected declarations for location services
 * Implemented natively on each platform using Moko-Geo or platform-specific APIs
 */
expect suspend fun getCurrentLocation(
    onSuccess: (LatLng) -> Unit,
    onError: (String) -> Unit
)
