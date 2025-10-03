
// androidMain
package com.creed.android.navigation

import androidx.navigation.NavHostController

class AndroidNavigator(private val navController: NavHostController) : Navigator {
    override fun navigate(route: Route, clearBackStack: Boolean) {
        navController.navigate(route.path) {
            if (clearBackStack)
                popUpTo(route.path) { inclusive = true }
        }
    }

    override fun navigate(route: String, clearBackStack: Boolean) {
        navController.navigate(route) {
            if (clearBackStack)
                popUpTo(route) { inclusive = true }
        }
    }

    override fun goBack() {
        navController.popBackStack()
    }
}