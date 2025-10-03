package com.creed.android.navigation

sealed class Route(val path: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object ProfileCompletion : Route("profile")
    object LocationPermission : Route("location")
    object MainScreen : Route("main")
    object Home : Route("home")
    object Search : Route("search")
    object Bookings : Route("bookings")
    object Profile : Route("profileTab")
    object ArenaDetail : Route("arena/{arenaId}") {
        fun create(arenaId: String) = Dynamic("arena/$arenaId")
    }
    object SlotPicker : Route("arena/{arenaId}/sport/{sportId}/slots") {
        fun create(arenaId: String, sportId: String) = Dynamic("arena/$arenaId/sport/$sportId/slots")
    }
    object OrderConfirmation : Route("confirmation/{bookingId}") {
        fun create(bookingId: String) = Dynamic("confirmation/$bookingId")
    }

    data class Dynamic(private val fullPath: String) : Route(fullPath)
}

// Bottom tab routes
enum class BottomNavItem(val route: String, val title: String) {
    HOME("home", "Home"),
    SEARCH("search", "Search"),
    BOOKINGS("bookings", "Bookings"),
    PROFILE("profileTab", "Profile")
}
