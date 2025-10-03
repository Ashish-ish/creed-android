package com.creed.ios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.creed.android.navigation.BottomNavItem
import com.creed.android.navigation.Route
import com.creed.android.ui.screens.*
import com.creed.ios.navigation.IOSNavigator

@Composable
fun MainScreen(navigator: IOSNavigator) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Text(
                                text = when (item) {
                                    BottomNavItem.HOME -> "🏠"
                                    BottomNavItem.SEARCH -> "🔍"
                                    BottomNavItem.BOOKINGS -> "📅"
                                    BottomNavItem.PROFILE -> "👤"
                                }
                            )
                        },
                        label = { Text(item.title) },
                        selected = selectedTab == item,
                        onClick = { selectedTab = item }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomNavItem.HOME -> HomeScreen(navigator = navigator)
                BottomNavItem.SEARCH -> SearchScreen(navigator = navigator)
                BottomNavItem.BOOKINGS -> BookingsScreen(navigator = navigator)
                BottomNavItem.PROFILE -> ProfileScreen(navigator = navigator)
            }
        }
    }
}
