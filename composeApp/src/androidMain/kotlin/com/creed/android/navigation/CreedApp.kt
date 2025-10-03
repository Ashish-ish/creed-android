package com.creed.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.creed.android.theme.CreedTheme
import com.creed.android.ui.screens.ArenaDetailScreen
import com.creed.android.ui.screens.LocationPermissionScreen
import com.creed.android.ui.screens.LoginScreen
import com.creed.android.ui.screens.MainScreen
import com.creed.android.ui.screens.OrderConfirmationScreen
import com.creed.android.ui.screens.ProfileCompletionScreen
import com.creed.android.ui.screens.SlotPickerScreen
import com.creed.android.ui.screens.SplashScreen

@Composable
fun CreedApp() {
    CreedTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val navigator = AndroidNavigator(navController)
            CreedNavHost(navController, navigator)
        }
    }
}

@Composable
fun CreedNavHost(navController: NavHostController, navigator: Navigator) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.path
    ) {
        composable(Route.Splash.path) {
            SplashScreen(navigator)
        }
        composable(Route.Login.path) {
            LoginScreen(
                navigator
            )
        }
        composable(Route.ProfileCompletion.path) {
            ProfileCompletionScreen(navigator)
        }
        composable(Route.LocationPermission.path) {
            LocationPermissionScreen(navController)
        }
        composable(Route.MainScreen.path) {
            MainScreen(navigator)
        }
        composable(Route.ArenaDetail.path) { backStackEntry ->
            val arenaId = backStackEntry.arguments?.getString("arenaId") ?: ""
            ArenaDetailScreen(navController, arenaId)
        }
        composable(Route.SlotPicker.path) { backStackEntry ->
            SlotPickerScreen(navController)
        }
        composable(Route.OrderConfirmation.path) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            OrderConfirmationScreen(navController, bookingId)
        }
    }
}
