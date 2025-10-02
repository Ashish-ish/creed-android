// iosMain
package com.creed.ios.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route

class IOSNavigator : Navigator {
    val backStack: SnapshotStateList<Route> = mutableStateListOf(Route.Splash)

    override fun navigate(route: Route) {
        backStack.add(route)
    }

    override fun goBack() {
        if (backStack.size > 1) backStack.removeLast()
    }

    val currentRoute: Route
        get() = backStack.last()
}