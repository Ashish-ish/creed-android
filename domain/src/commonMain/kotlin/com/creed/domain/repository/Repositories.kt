package com.creed.domain.repository

import com.creed.domain.models.*

interface AuthRepository {
    suspend fun requestOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<FirebaseUser>
    suspend fun registerUser(firebaseToken: String): Result<AuthTokens>
    suspend fun loginUser(firebaseToken: String): Result<AuthTokens>
}

interface UserRepository {
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(user: User): Result<User>
}

interface ArenaRepository {
    suspend fun getNearby(lat: Double, lng: Double, radiusKm: Int = 10): Result<List<Arena>>
    suspend fun getArenaDetail(arenaId: String): Result<Arena>
    suspend fun getSports(arenaId: String): Result<List<Sport>>
    suspend fun getSlots(arenaId: String, sportId: String, courtId: String, date: String): Result<List<Slot>>

    // Home screen specific methods
    suspend fun getHomeSummary(): Result<HomeSummary>
    suspend fun getFeaturedArenas(): Result<List<FeaturedArena>>
    suspend fun getAvailableSports(): Result<List<SportCategory>>
    suspend fun getUpcomingBookings(): Result<List<UpcomingBooking>>
    suspend fun getRecentActivity(): Result<List<RecentActivity>>
}

interface BookingRepository {
    suspend fun create(request: BookingRequest): Result<Booking>
    suspend fun fetchBookings(upcoming: Boolean): Result<List<Booking>>
    suspend fun cancelBooking(bookingId: String): Result<Unit>
}

interface PaymentRepository {
    suspend fun createRazorPayOrder(amountCents: Int): Result<String>
    suspend fun fetchWeeklyReport(): Result<WeeklyReport>
}

// Copilot: Placeholder for FirebaseUser, AuthTokens, WeeklyReport
class FirebaseUser // Copilot: platform-specific
class AuthTokens(val authToken: String, val refreshToken: String, val userId: String)
class WeeklyReport // Copilot: define as needed
