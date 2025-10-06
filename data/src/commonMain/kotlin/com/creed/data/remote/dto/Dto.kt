package com.creed.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserDto(
    val id: String?,
    val phoneNumber: String?,
    val email: String? = "",
    val firstName: String?,
    val lastName: String?,
    val gender: String?,
    val dateOfBirth: String?,
    val role: String? = "user"
)

@Serializable
data class AddressDto(
    val street: String,
    val city: String,
    val state: String,
    val zip: String
)

@Serializable
data class LatLngDto(val lat: Double, val lng: Double)

@Serializable
data class ArenaDto(
    val id: String,
    val name: String,
    val subtitle: String = "",
    val description: String = "",
    val address: AddressDto,
    val location: LatLngDto,
    val images: List<String>
)

@Serializable
data class SportDto(
    val id: String,
    val arenaId: String,
    val name: String,
    val description: String = "",
    val rules: String = ""
)

@Serializable
data class DayScheduleDto(val open: String, val close: String)

@Serializable
data class CourtDto(
    val id: String,
    val sportId: String,
    val arenaId: String,
    val name: String,
    val ratePerHour: Int,
    val availability: Map<String, DayScheduleDto>
)

@Serializable
data class SlotDto(
    val start: String,
    val end: String,
    val isBooked: Boolean
)

@Serializable
data class EquipmentDto(
    val id: String,
    val arenaId: String,
    val sportId: String? = null,
    val name: String,
    val description: String = "",
    val ratePerHour: Int,
    val quantity: Int
)

@Serializable
data class EquipmentQuantityDto(val equipmentId: String, val quantity: Int)

@Serializable
data class BookingRequestDto(
    val arenaId: String,
    val sportId: String,
    val courtId: String,
    val date: String,
    val start: String,
    val end: String,
    val equipment: List<EquipmentQuantityDto>
)

@Serializable
data class BookingDto(
    val id: String,
    val arena: ArenaDto,
    val sport: SportDto,
    val court: CourtDto,
    val date: String,
    val start: String,
    val end: String,
    val equipment: List<EquipmentQuantityDto>
)

// Authentication DTOs
@Serializable
data class AuthTokensDto(
    val userId: String,
    val authToken: String,
    val refreshToken: String
)

@Serializable
data class UserExistsResponseDto(
    val exists: Boolean
)

@Serializable
data class ProfileUpdateRequestDto(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val dateOfBirth: String
)

@Serializable
data class SearchResponseDto(
    val success: Boolean,
    val data: SearchDataDto
)

@Serializable
data class SearchDataDto(
    val arenas: List<SearchArenaDto>,
    val pagination: PaginationDto,
    val searchParams: SearchParamsDto
)

@Serializable
data class SearchArenaDto(
    val _id: String,
    val name: String,
    val subtitle: String,
    val imageUrl: String,
    val address: SearchAddressDto,
    val rating: RatingDto,
    val priceRange: PriceRangeDto,
    val availableSports: List<AvailableSportDto>,
    val isFeatured: Boolean
)

@Serializable
data class SearchAddressDto(
    val coordinates: CoordinatesDto,
    val fullAddress: String,
    val area: String,
    val city: String,
    val state: String,
    val pincode: String,
    val landmark: String
)

@Serializable
data class CoordinatesDto(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
data class RatingDto(
    val average: Double,
    val count: Int
)

@Serializable
data class PriceRangeDto(
    val min: Int,
    val max: Int,
    val currency: String
)

@Serializable
data class AvailableSportDto(
    val _id: String,
    val sportId: String,
    val sportName: String,
    val sportIcon: String,
    val isBookingEnabled: Boolean
)

@Serializable
data class PaginationDto(
    val current: Int,
    val pages: Int,
    val total: Int
)

@Serializable
data class SearchParamsDto(
    val keyword: String?,
    val location: String?,
    val sport: String?,
    val city: String?
)
