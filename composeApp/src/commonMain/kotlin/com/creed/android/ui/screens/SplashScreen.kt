package com.creed.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun SplashScreen(navController: Navigator) {
    val viewModel: SplashViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    // Handle navigation based on state
    LaunchedEffect(state) {
        when (state) {
            is SplashState.NavigateToHome -> {
                navController.navigate(Route.LocationPermission, true)
            }
            is SplashState.NavigateToLogin -> {
                navController.navigate(Route.Login, true)
            }
            is SplashState.Loading -> {
                // Stay on splash screen
            }
        }
    }

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
            )
    ) {
        // Center loader - show when loading
        if (state is SplashState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Creed text at bottom
        Text(
            text = "creed",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            letterSpacing = 2.sp
        )
    }
}
