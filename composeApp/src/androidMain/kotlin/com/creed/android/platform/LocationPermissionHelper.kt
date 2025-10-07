package com.creed.android.platform

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * IMPORTANT: This class must be instantiated in Activity.onCreate() 
 * or provided via DI (Koin) as a single instance.
 * DO NOT create instances during Composition!
 */
class LocationPermissionHelper(
    activity: ComponentActivity
) {
    
    private var currentCallback: ((Boolean) -> Unit)? = null
    
    // Register launcher once during initialization (must happen in onCreate or earlier)
    private val permissionLauncher: ActivityResultLauncher<Array<String>> = 
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            
            currentCallback?.invoke(fineLocationGranted || coarseLocationGranted)
            currentCallback = null
        }
    
    fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        currentCallback = onResult
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
