package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.creed.android.navigation.BottomNavItem

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.HOME.route) {
                HomeScreen(navController = navController)
            }
            composable(BottomNavItem.SEARCH.route) {
                SearchScreen(navController = navController)
            }
            composable(BottomNavItem.BOOKINGS.route) {
                BookingsScreen(navController = navController)
            }
            composable(BottomNavItem.PROFILE.route) {
                ProfileScreen(navController = navController)
            }
        }
    }
}
