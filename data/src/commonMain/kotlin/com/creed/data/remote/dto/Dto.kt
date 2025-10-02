package com.creed.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val phoneNumber: String,
    val email: String = "",
    val firstName: String,
    val lastName: String,
    val gender: String,
    val dateOfBirth: String,
    val role: String = "user"
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

