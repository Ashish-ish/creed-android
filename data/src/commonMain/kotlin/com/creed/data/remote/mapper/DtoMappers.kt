package com.creed.data.remote.mapper

import com.creed.data.remote.dto.*
import com.creed.domain.models.*

fun UserDto.toDomain() = User(id, phoneNumber, email, firstName, lastName, gender, dateOfBirth, role)
fun AddressDto.toDomain() = Address(street, city, state, zip)
fun LatLngDto.toDomain() = LatLng(lat, lng)
fun ArenaDto.toDomain(maxDepth: Int = 1, currentDepth: Int = 0): Arena = Arena(
    id = id,
    name = name,
    subtitle = subtitle,
    description = description,
    address = address,
    location = location?.toDomain(),
    images = images,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    sports = sports.map { it.toDomain() },
    amenities = amenities,
    nearbyArenas = if (currentDepth < maxDepth) {
        nearbyArenas.map { it.toDomain(maxDepth, currentDepth + 1) }
    } else {
        emptyList() // stop recursion here
    },
    priceRange = priceRange
)
fun SportDto.toDomain() = Sport(id, arenaId, name, description, rules, image)
fun DayScheduleDto.toDomain() = DaySchedule(open, close)
fun CourtDto.toDomain() = Court(id, sportId, arenaId, name, ratePerHour, availability.mapValues { it.value.toDomain() })
fun SlotDto.toDomain() = Slot(start, end, isBooked)
fun EquipmentDto.toDomain() = Equipment(id, arenaId, sportId, name, description, ratePerHour, quantity)
fun EquipmentQuantityDto.toDomain() = EquipmentQuantity(equipmentId, quantity)
fun BookingRequestDto.toDomain() = BookingRequest(arenaId, sportId, courtId, date, start, end, equipment.map { it.toDomain() })
fun BookingDto.toDomain() = Booking(id, arena.toDomain(), sport.toDomain(), court.toDomain(), date, start, end, equipment.map { it.toDomain() })

// Reverse mappers as needed
fun User.toDto() = UserDto(id, phoneNumber, email, firstName, lastName, gender, dateOfBirth, role)
fun Address.toDto() = AddressDto(street, city, state, zip)
fun LatLng.toDto() = LatLngDto(latitude, longitude)
fun Arena.toDto(maxDepth: Int = 1, currentDepth: Int = 0): ArenaDto = ArenaDto(
    id = id,
    name = name,
    subtitle = subtitle,
    description = description,
    address = address,
    location = location?.toDto(),
    images = images,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    sports = sports.map { it.toDto() },
    amenities = amenities,
    nearbyArenas = if (currentDepth < maxDepth) {
        nearbyArenas.map { it.toDto(maxDepth, currentDepth + 1) }
    } else {
        emptyList()
    },
    priceRange = priceRange
)
fun Sport.toDto() = SportDto(id, arenaId, name, description, rules)
fun DaySchedule.toDto() = DayScheduleDto(open, close)
fun Court.toDto() = CourtDto(id, sportId, arenaId, name, ratePerHour, availability.mapValues { it.value.toDto() })
fun Slot.toDto() = SlotDto(start, end, isBooked)
fun Equipment.toDto() = EquipmentDto(id, arenaId, sportId, name, description, ratePerHour, quantity)
fun EquipmentQuantity.toDto() = EquipmentQuantityDto(equipmentId, quantity)
fun BookingRequest.toDto() = BookingRequestDto(arenaId, sportId, courtId, date, start, end, equipment.map { it.toDto() })
fun Booking.toDto() = BookingDto(id, arena.toDto(), sport.toDto(), court.toDto(), date, start, end, equipment.map { it.toDto() })

// Authentication mappers
fun AuthTokensDto.toDomain() = AuthTokens(userId, authToken, refreshToken)
fun UserExistsResponseDto.toDomain() = UserExistsResponse(exists)
fun ProfileUpdateRequestDto.toDomain() = ProfileUpdateRequest(firstName, lastName, gender, dateOfBirth)

fun AuthTokens.toDto() = AuthTokensDto(userId, authToken, refreshToken)
fun UserExistsResponse.toDto() = UserExistsResponseDto(exists)
fun ProfileUpdateRequest.toDto() = ProfileUpdateRequestDto(firstName, lastName, gender, dateOfBirth)

// Search-related mappers
fun SearchDataDto.toDomain() = SearchResponse(
    arenas = arenas.map { it.toDomain() },
    pagination = pagination.toDomain(),
    searchParams = searchParams.toDomain()
)

fun SearchArenaDto.toDomain() = SearchArena(
    id = _id,
    name = name,
    subtitle = subtitle,
    imageUrl = imageUrl,
    address = address.toDomain(),
    rating = rating.toDomain(),
    priceRange = priceRange.toDomain(),
    availableSports = availableSports.map { it.toDomain() },
    isFeatured = isFeatured
)

fun SearchAddressDto.toDomain() = SearchAddress(
    fullAddress = fullAddress,
    area = area,
    city = city,
    state = state,
    pincode = pincode,
    landmark = landmark,
    coordinates = coordinates.coordinates
)

fun RatingDto.toDomain() = ArenaRating(
    average = average,
    count = count
)

fun PriceRangeDto.toDomain() = ArenaPriceRange(
    min = min,
    max = max,
    currency = currency
)

fun AvailableSportDto.toDomain() = AvailableSport(
    sportId = sportId,
    sportName = sportName,
    sportIcon = sportIcon,
    isBookingEnabled = isBookingEnabled
)

fun PaginationDto.toDomain() = SearchPagination(
    current = current,
    pages = pages,
    total = total
)

fun SearchParamsDto.toDomain() = SearchParams(
    keyword = keyword,
    location = location,
    sport = sport,
    city = city
)
