package com.creed.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String?=null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val firstName: String?=null,
    val lastName: String?=null,
    val gender: String?=null, // "male" | "female" | "other"
    val dateOfBirth: String?=null, // yyyy-MM-dd
    val role: String? = "user"
)

@Serializable
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zip: String
)
@Serializable
data class Arena(
    val id: String,
    val name: String,
    val subtitle: String = "",
    val description: String = "",
    val address: Address,
    val location: LatLng,
    val images: List<String>
)

@Serializable
data class Sport(
    val id: String,
    val arenaId: String,
    val name: String,
    val description: String = "",
    val rules: String = ""
)

@Serializable
data class DaySchedule(val open: String, val close: String)

@Serializable
data class Court(
    val id: String,
    val sportId: String,
    val arenaId: String,
    val name: String,
    val ratePerHour: Int,
    val availability: Map<String, DaySchedule>
)

@Serializable
data class Slot(
    val start: String, // "HH:mm"
    val end: String,
    val isBooked: Boolean
)

@Serializable
data class Equipment(
    val id: String,
    val arenaId: String,
    val sportId: String? = null,
    val name: String,
    val description: String = "",
    val ratePerHour: Int,
    val quantity: Int
)

@Serializable
data class EquipmentQuantity(val equipmentId: String, val quantity: Int)

@Serializable
data class BookingRequest(
    val arenaId: String,
    val sportId: String,
    val courtId: String,
    val date: String, // yyyy-MM-dd
    val start: String,
    val end: String,
    val equipment: List<EquipmentQuantity>
)

@Serializable
data class Booking(
    val id: String,
    val arena: Arena,
    val sport: Sport,
    val court: Court,
    val date: String,
    val start: String,
    val end: String,
    val equipment: List<EquipmentQuantity>
)

// Home screen specific models
@Serializable
data class HomeSummary(
    val upcomingCount: Int,
    val favouritesCount: Int,
    val thisMonthCount: Int
)

@Serializable
data class FeaturedArena(
    val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val rating: Float,
    val isFavorite: Boolean,
    val sports: List<String>,
    val distance: String,
    val priceRange: String,
    val additionalSportsCount: Int = 0
)

@Serializable
data class SportCategory(
    val id: String,
    val name: String,
    val icon: String
)

@Serializable
data class UpcomingBooking(
    val id: String,
    val title: String,
    val sport: String,
    val date: String,
    val time: String
)

@Serializable
data class RecentActivity(
    val id: String,
    val title: String,
    val sport: String,
    val date: String,
    val time: String,
    val price: String,
    val status: ActivityStatus
)

@Serializable
enum class ActivityStatus {
    UPCOMING, COMPLETED, CANCELLED
}

// Authentication models
@Serializable
data class AuthTokens(
    val userId: String,
    val authToken: String,
    val refreshToken: String
)

@Serializable
data class UserExistsResponse(
    val exists: Boolean
)

@Serializable
data class ProfileUpdateRequest(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val dateOfBirth: String
)

// Firebase user representation
data class FirebaseUser(
    val uid: String,
    val token: String
)

// Search-related models
@Serializable
data class SearchResponse(
    val arenas: List<SearchArena>,
    val pagination: SearchPagination,
    val searchParams: SearchParams
)

@Serializable
data class SearchArena(
    val id: String,
    val name: String,
    val subtitle: String,
    val imageUrl: String,
    val address: SearchAddress,
    val rating: ArenaRating,
    val priceRange: ArenaPriceRange,
    val availableSports: List<AvailableSport>,
    val isFeatured: Boolean
)

@Serializable
data class SearchAddress(
    val fullAddress: String,
    val area: String,
    val city: String,
    val state: String,
    val pincode: String,
    val landmark: String,
    val coordinates: List<Double>
)

@Serializable
data class ArenaRating(
    val average: Double,
    val count: Int
)

@Serializable
data class ArenaPriceRange(
    val min: Int,
    val max: Int,
    val currency: String
)

@Serializable
data class AvailableSport(
    val sportId: String,
    val sportName: String,
    val sportIcon: String,
    val isBookingEnabled: Boolean
)

@Serializable
data class SearchPagination(
    val current: Int,
    val pages: Int,
    val total: Int
)

@Serializable
data class SearchParams(
    val keyword: String?,
    val location: String?,
    val sport: String?,
    val city: String?
)
