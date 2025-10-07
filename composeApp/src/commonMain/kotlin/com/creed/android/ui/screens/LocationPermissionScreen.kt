package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.extensions.toFixed
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.platform.rememberPlatformPermissionHelper
import com.creed.android.vm.LocationPermissionViewModel
import com.creed.android.vm.LocationPermissionState
import com.creed.data.location.LocationPermissionStatus
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPermissionScreen(
    navigator: Navigator,
    viewModel: LocationPermissionViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // This now safely gets the helper from Koin (created in Activity.onCreate)
    val permissionHelper = rememberPlatformPermissionHelper()

    // Handle navigation
    LaunchedEffect(state.shouldNavigateToHome) {
        if (state.shouldNavigateToHome) {
            viewModel.clearNavigationFlag()
            navigator.navigate(Route.MainScreen, true)
        }
    }

    // Handle error messages
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LocationPermissionContent(
                state = state,
                onRequestPermission = {
                    permissionHelper.requestLocationPermission { granted ->
                        if (granted) {
                            viewModel.onPermissionGranted()
                        } else {
                            viewModel.onPermissionDenied()
                        }
                    }
                },
                onSkip = { viewModel.skipLocationPermission() },
                onRetry = { viewModel.requestCurrentLocation() }
            )
        }
    }
}

@Composable
private fun LocationPermissionContent(
    state: LocationPermissionState,
    onRequestPermission: () -> Unit,
    onSkip: () -> Unit,
    onRetry: () -> Unit
) {
    // Location icon
    Text(
        text = when (state.permissionStatus) {
            LocationPermissionStatus.GRANTED -> if (state.currentLocation != null) "✅" else "📍"
            LocationPermissionStatus.DENIED -> "🚫"
            LocationPermissionStatus.RESTRICTED -> "⛔"
            else -> "📍"
        },
        fontSize = 80.sp,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // Title
    Text(
        text = when {
            state.isLoading -> "Getting Your Location..."
            state.permissionStatus == LocationPermissionStatus.GRANTED && state.currentLocation != null -> "Location Access Granted!"
            state.permissionStatus == LocationPermissionStatus.DENIED -> "Permission Required"
            state.permissionStatus == LocationPermissionStatus.RESTRICTED -> "Location Restricted"
            state.error != null -> "Something Went Wrong"
            else -> "Enable Location Access"
        },
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Center
    )

    // Description
    Text(
        text = when {
            state.isLoading -> "We're getting your current location to find nearby sports grounds."
            state.permissionStatus == LocationPermissionStatus.GRANTED && state.currentLocation != null ->
                "Great! We can now show you sports grounds near your location."
            state.permissionStatus == LocationPermissionStatus.DENIED ->
                "Location permission was denied. You can still search for grounds by city name, but nearby recommendations won't be available."
            state.permissionStatus == LocationPermissionStatus.RESTRICTED ->
                "Location access is restricted on this device."
            state.error != null -> state.error ?: "An error occurred"
            else -> "We need your location to find sports grounds near you and provide personalized recommendations."
        },
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    )

    // Loading indicator
    if (state.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }

    // Location info
    if (state.permissionStatus == LocationPermissionStatus.GRANTED && state.currentLocation != null) {
        Text(
            text = "Location: ${state.currentLocation.latitude.toFixed(6)}, " +
                    "${state.currentLocation.longitude.toFixed(6)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    // Action buttons
    when (state.permissionStatus) {
        LocationPermissionStatus.NOT_DETERMINED -> {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Allow Location Access")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSkip) {
                Text("Skip for Now")
            }
        }

        LocationPermissionStatus.DENIED,
        LocationPermissionStatus.RESTRICTED -> {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Try Again")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSkip) {
                Text("Continue Without Location")
            }
        }

        LocationPermissionStatus.GRANTED -> {
            if (state.currentLocation == null && !state.isLoading) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Current Location")
                }
            }
        }
    }
}