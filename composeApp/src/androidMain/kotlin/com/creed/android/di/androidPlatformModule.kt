package com.creed.android.di

import com.creed.android.platform.LocationPermissionHelper
import org.koin.dsl.module

/**
 * Platform-specific Android module
 * Register LocationPermissionHelper as a single instance per Activity
 */
val androidPlatformModule = module {
    // This will be provided from MainActivity
    single<LocationPermissionHelper> {
        error("LocationPermissionHelper must be provided from Activity") 
    }
}