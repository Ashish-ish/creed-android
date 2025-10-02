
// androidMain
package com.creed.android.navigation

import androidx.navigation.NavHostController

class AndroidNavigator(private val navController: NavHostController) : Navigator {
    override fun navigate(route: Route) {
        navController.navigate(route.path)
    }

    override fun goBack() {
        navController.popBackStack()
    }
}