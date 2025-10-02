package com.creed.android.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creed.domain.models.*
import com.creed.domain.repository.ArenaRepository
import com.creed.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val arenaRepository: ArenaRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadHomeData()
            is HomeEvent.OnSummaryClick -> handleSummaryClick(event.type)
            is HomeEvent.OnSportChipClick -> handleSportClick(event.sportId)
            is HomeEvent.OnArenaClick -> handleArenaClick(event.arenaId)
            is HomeEvent.OnRescheduleClick -> handleReschedule(event.bookingId)
            is HomeEvent.OnCancelClick -> handleCancel(event.bookingId)
            is HomeEvent.OnViewAllUpcoming -> handleViewAllUpcoming()
            is HomeEvent.OnViewAllFeatured -> handleViewAllFeatured()
            is HomeEvent.OnViewAllRecent -> handleViewAllRecent()
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                // Load user profile
                val userResult = userRepository.getProfile()
                val user = userResult.getOrNull()

                // Load all home screen data in parallel
                val summaryResult = arenaRepository.getHomeSummary()
                val featuredResult = arenaRepository.getFeaturedArenas()
                val sportsResult = arenaRepository.getAvailableSports()
                val upcomingResult = arenaRepository.getUpcomingBookings()
                val recentResult = arenaRepository.getRecentActivity()

                // Convert domain models to UI models
                val summary = summaryResult.getOrNull()?.let { domainSummary ->
                    SummaryState(
                        upcomingCount = domainSummary.upcomingCount,
                        favouritesCount = domainSummary.favouritesCount,
                        thisMonthCount = domainSummary.thisMonthCount,
                        showViewAll = true
                    )
                } ?: SummaryState()

                val upcoming = upcomingResult.getOrNull()?.map { domainUpcoming ->
                    UpcomingUi(
                        id = domainUpcoming.id,
                        title = domainUpcoming.title,
                        sport = domainUpcoming.sport,
                        date = domainUpcoming.date,
                        time = domainUpcoming.time,
                        showViewAll = true
                    )
                } ?: emptyList()

                val sports = sportsResult.getOrNull()?.map { domainSport ->
                    SportChipUi(
                        id = domainSport.id,
                        name = domainSport.name,
                        icon = domainSport.icon,
                        showViewAll = false
                    )
                } ?: emptyList()

                val featured = featuredResult.getOrNull()?.map { domainFeatured ->
                    FeaturedArenaUi(
                        id = domainFeatured.id,
                        name = domainFeatured.name,
                        location = domainFeatured.location,
                        imageUrl = domainFeatured.imageUrl,
                        rating = domainFeatured.rating,
                        isFavorite = domainFeatured.isFavorite,
                        sports = domainFeatured.sports,
                        distance = domainFeatured.distance,
                        priceRange = domainFeatured.priceRange,
                        additionalSportsCount = domainFeatured.additionalSportsCount,
                        showViewAll = true
                    )
                } ?: emptyList()

                val recent = recentResult.getOrNull()?.map { domainRecent ->
                    RecentUi(
                        id = domainRecent.id,
                        title = domainRecent.title,
                        sport = domainRecent.sport,
                        date = domainRecent.date,
                        time = domainRecent.time,
                        price = domainRecent.price,
                        status = when (domainRecent.status) {
                            ActivityStatus.UPCOMING -> RecentStatus.UPCOMING
                            ActivityStatus.COMPLETED -> RecentStatus.COMPLETED
                            ActivityStatus.CANCELLED -> RecentStatus.CANCELLED
                        },
                        showViewAll = true
                    )
                } ?: emptyList()

                _state.value = _state.value.copy(
                    greeting = getGreeting(),
                    user = user,
                    summary = summary,
                    upcoming = upcoming,
                    sports = sports,
                    featured = featured,
                    recent = recent,
                    isLoading = false
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun handleSummaryClick(type: SummaryType) {
        // TODO: Navigate to specific summary screen based on type
    }

    private fun handleSportClick(sportId: String) {
        // TODO: Navigate to sport-specific arena list
    }

    private fun handleArenaClick(arenaId: String) {
        // TODO: Navigate to arena detail screen
    }

    private fun handleReschedule(bookingId: String) {
        // TODO: Handle booking reschedule
    }

    private fun handleCancel(bookingId: String) {
        // TODO: Handle booking cancellation
    }

    private fun handleViewAllUpcoming() {
        // TODO: Navigate to all upcoming bookings
    }

    private fun handleViewAllFeatured() {
        // TODO: Navigate to all featured arenas
    }

    private fun handleViewAllRecent() {
        // TODO: Navigate to all recent activities
    }

    @OptIn(ExperimentalTime::class)
    fun getGreeting(): String {
        val now = Clock.System.now()
        val hour = now.toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when (hour) {
            in 5..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
    }
}
