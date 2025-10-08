package com.creed.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.creed.android.theme.CreedTheme
import com.creed.android.ui.screens.ArenaDetailScreen
import com.creed.android.ui.screens.BookingsScreen
import com.creed.android.ui.screens.LocationPermissionScreen
import com.creed.android.ui.screens.LoginScreen
import com.creed.android.ui.screens.MainScreen
import com.creed.android.ui.screens.OrderConfirmationScreen
import com.creed.android.ui.screens.ProfileCompletionScreen
import com.creed.android.ui.screens.SlotPickerScreen
import com.creed.android.ui.screens.SplashScreen
import com.creed.android.vm.LogoutMonitorViewModel
import org.koin.compose.koinInject

@Composable
fun CreedApp() {
    CreedTheme {
        val navController = rememberNavController()
        val navigator = AndroidNavigator(navController)
        CreedNavHost(navController, navigator)
    }
}

@Composable
fun CreedNavHost(navController: NavHostController, navigator: Navigator) {
    // Global logout monitoring - observes AUTH_TOKEN changes
    val logoutMonitorVM: LogoutMonitorViewModel = koinInject()

    // Handle logout events globally
    LaunchedEffect(Unit) {
        logoutMonitorVM.logoutEvent.collect {
            // When auth token becomes null, clear navigation stack and go to login
            navController.navigate(Route.Login.path) {
                // Clear entire back stack
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.Splash.path,
        modifier = Modifier.fillMaxSize()
    ) {
        // Screens that should handle their own insets (like ArenaDetail with transparent top bar)
        composable(Route.ArenaDetail.path) { backStackEntry ->
            val arenaId = backStackEntry.arguments?.getString("arenaId") ?: ""
            ArenaDetailScreen(navigator, arenaId)
        }

        // Screens that need system bar padding
        composable(Route.Splash.path) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                SplashScreen(navigator)
            }
        }
        composable(Route.Login.path) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                LoginScreen(navigator)
            }
        }
        composable(Route.ProfileCompletion.path) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                ProfileCompletionScreen(navigator)
            }
        }
        composable(Route.LocationPermission.path) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                LocationPermissionScreen(navigator)
            }
        }
        composable(Route.MainScreen.path) {
            // MainScreen handles its own insets with bottom navigation
            MainScreen(navController)
        }
        composable(Route.SlotPicker.path) { backStackEntry ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                SlotPickerScreen(navigator)
            }
        }
        composable(Route.OrderConfirmation.path) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                OrderConfirmationScreen(navigator, bookingId)
            }
        }
        composable(Route.Bookings.path) { backStackEntry ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                BookingsScreen(navigator)
            }
        }
    }
}
