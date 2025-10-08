package com.creed.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.vm.SplashState
import com.creed.android.vm.SplashViewModel
import org.koin.compose.koinInject

@Composable
fun SplashScreen(navigator: Navigator) {
    val viewModel: SplashViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    // Handle navigation based on state
    LaunchedEffect(state) {
        when (state) {
            is SplashState.NavigateToLogin -> {
                navigator.navigate(Route.Login, clearBackStack = true)
            }
            is SplashState.NavigateToLocationPermission -> {
                navigator.navigate(Route.LocationPermission, clearBackStack = true)
            }
            is SplashState.NavigateToHome -> {
                navigator.navigate(Route.MainScreen, clearBackStack = true)
            }
            else -> { /* Still loading */ }
        }
    }

    // Splash UI with gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo/title
            Text(
                text = "CREED",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Sports Ground Booking",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Loading indicator
            if (state is SplashState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
