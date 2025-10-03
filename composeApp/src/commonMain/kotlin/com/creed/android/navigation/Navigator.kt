// commonMain
package com.creed.android.navigation

interface Navigator {
    fun navigate(route: Route, clearBackStack: Boolean = false)
    fun goBack()
}