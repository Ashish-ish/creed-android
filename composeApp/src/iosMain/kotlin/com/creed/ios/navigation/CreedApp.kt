package com.creed.ios.navigation

import androidx.compose.runtime.Composable
import com.creed.android.navigation.Route
import com.creed.android.ui.screens.*
import com.creed.ios.ui.screens.MainScreen

@Composable
fun CreedApp(navigator: IOSNavigator) {
    when (val route = navigator.currentRoute) {
        Route.Splash -> SplashScreen(navigator)
        Route.Login -> LoginScreen(navigator)
        Route.ProfileCompletion -> ProfileCompletionScreen(navigator)
        Route.MainScreen -> MainScreen(navigator)
        Route.ArenaDetail -> ArenaDetailScreen(navigator, arenaId = "sample")
        Route.SlotPicker -> SlotPickerScreen(navigator, arenaId = "sample", sportId = "1")
        Route.OrderConfirmation -> OrderConfirmationScreen(navigator, bookingId = "sample")
        else -> SplashScreen(navigator)
    }
}