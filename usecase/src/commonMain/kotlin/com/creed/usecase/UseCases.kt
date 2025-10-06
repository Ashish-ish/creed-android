package com.creed.usecase

import com.creed.domain.repository.*

class CheckUserExistsUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(firebaseToken: String) = authRepository.checkUserExists(firebaseToken)
}

class LoginUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(firebaseToken: String) = authRepository.loginUser(firebaseToken)
}

class RegisterUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(firebaseToken: String) = authRepository.registerUser(firebaseToken)
}

class GetUserProfileUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getProfile()
}

class UpdateUserProfileUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(profileRequest: com.creed.domain.models.ProfileUpdateRequest) = userRepository.updateProfile(profileRequest)
}

class FetchNearbyArenasUseCase(private val arenaRepository: ArenaRepository) {
    suspend operator fun invoke(lat: Double, lng: Double, radiusKm: Int = 10) = arenaRepository.getNearby(lat, lng, radiusKm)
}

class FetchArenaDetailUseCase(private val arenaRepository: ArenaRepository) {
    suspend operator fun invoke(arenaId: String) = arenaRepository.getArenaDetail(arenaId)
}

class FetchSportsUseCase(private val arenaRepository: ArenaRepository) {
    suspend operator fun invoke(arenaId: String) = arenaRepository.getSports(arenaId)
}

class FetchSlotsUseCase(private val arenaRepository: ArenaRepository) {
    suspend operator fun invoke(arenaId: String, sportId: String, courtId: String, date: String) = arenaRepository.getSlots(arenaId, sportId, courtId, date)
}

class CreateBookingUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(request: com.creed.domain.models.BookingRequest) = bookingRepository.create(request)
}

class FetchBookingsUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(upcoming: Boolean) = bookingRepository.fetchBookings(upcoming)
}

class CancelBookingUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(bookingId: String) = bookingRepository.cancelBooking(bookingId)
}

class FetchWeeklyReportUseCase(private val paymentRepository: PaymentRepository) {
    suspend operator fun invoke() = paymentRepository.fetchWeeklyReport()
}

class FetchSearchArenasUseCase(private val arenaRepository: ArenaRepository) {
    suspend operator fun invoke(query: String, location: String? = null, sport: String? = null, city: String? = null) =
        arenaRepository.searchArenas(query, location, sport, city)
}
