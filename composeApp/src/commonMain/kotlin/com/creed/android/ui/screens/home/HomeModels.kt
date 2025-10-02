package com.creed.android.ui.screens.home

import com.creed.domain.models.User

// Events
sealed class HomeEvent {
    object Refresh : HomeEvent()
    data class OnSummaryClick(val type: SummaryType) : HomeEvent()
    data class OnSportChipClick(val sportId: String) : HomeEvent()
    data class OnArenaClick(val arenaId: String) : HomeEvent()
    data class OnRescheduleClick(val bookingId: String) : HomeEvent()
    data class OnCancelClick(val bookingId: String) : HomeEvent()
    object OnViewAllUpcoming : HomeEvent()
    object OnViewAllFeatured : HomeEvent()
    object OnViewAllRecent : HomeEvent()
}

// State
data class HomeState(
    val greeting: String = "",
    val user: User? = null,
    val summary: SummaryState = SummaryState(),
    val upcoming: List<UpcomingUi> = emptyList(),
    val sports: List<SportChipUi> = emptyList(),
    val featured: List<FeaturedArenaUi> = emptyList(),
    val recent: List<RecentUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// UI Models
data class SummaryState(
    val upcomingCount: Int = 0,
    val favouritesCount: Int = 0,
    val thisMonthCount: Int = 0,
    val showViewAll: Boolean = false
)

data class UpcomingUi(
    val id: String,
    val title: String,
    val sport: String,
    val date: String,
    val time: String,
    val showViewAll: Boolean = false
)

data class SportChipUi(
    val id: String,
    val name: String,
    val icon: String,
    val showViewAll: Boolean = false
)

data class FeaturedArenaUi(
    val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val rating: Float,
    val isFavorite: Boolean,
    val sports: List<String>,
    val distance: String,
    val priceRange: String,
    val additionalSportsCount: Int = 0,
    val showViewAll: Boolean = false
)

data class RecentUi(
    val id: String,
    val title: String,
    val sport: String,
    val date: String,
    val time: String,
    val price: String,
    val status: RecentStatus,
    val showViewAll: Boolean = false
)

enum class SummaryType {
    UPCOMING, FAVOURITES, THIS_MONTH
}

enum class RecentStatus {
    UPCOMING, COMPLETED, CANCELLED
}
