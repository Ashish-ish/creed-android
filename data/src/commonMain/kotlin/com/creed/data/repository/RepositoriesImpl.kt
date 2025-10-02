package com.creed.data.repository

import com.creed.data.remote.dto.*
import com.creed.data.remote.mapper.*
import com.creed.domain.models.*
import com.creed.domain.repository.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class AuthRepositoryImpl(private val httpClient: HttpClient) : AuthRepository {
    override suspend fun requestOtp(phone: String): Result<Unit> = runCatching {
        httpClient.post("/api/users/check") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("phone" to phone))
        }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<FirebaseUser> = runCatching {
        // Copilot: Platform-specific Firebase verification handled in composeApp
        throw NotImplementedError("Platform-specific")
    }

    override suspend fun registerUser(firebaseToken: String): Result<AuthTokens> = runCatching {
        val response: HttpResponse = httpClient.post("/api/users/register") {
            header("Firebase-Token", firebaseToken)
        }
        // Copilot: Parse response to AuthTokens
        AuthTokens("authToken", "refreshToken", "userId") // Placeholder
    }

    override suspend fun loginUser(firebaseToken: String): Result<AuthTokens> = runCatching {
        val response: HttpResponse = httpClient.post("/api/users/login") {
            header("Firebase-Token", firebaseToken)
        }
        AuthTokens("authToken", "refreshToken", "userId") // Placeholder
    }
}

class UserRepositoryImpl(private val httpClient: HttpClient) : UserRepository {
    override suspend fun getProfile(): Result<User> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        // val dto: UserDto = httpClient.get("/api/users/profile").body()
        // dto.toDomain()

        // Mock user data for development
        User(
            id = "1",
            phoneNumber = "+91 9876543210",
            email = "alex@example.com",
            firstName = "Alex",
            lastName = "Johnson",
            gender = "male",
            dateOfBirth = "1990-01-01"
        )
    }

    override suspend fun updateProfile(user: User): Result<User> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        // val dto: UserDto = httpClient.patch("/api/users/profile") {
        //     contentType(ContentType.Application.Json)
        //     setBody(user.toDto())
        // }.body()
        // dto.toDomain()

        // Mock implementation - return the updated user
        user
    }
}

class ArenaRepositoryImpl(private val httpClient: HttpClient) : ArenaRepository {
    override suspend fun getNearby(lat: Double, lng: Double, radiusKm: Int): Result<List<Arena>> = runCatching {
        val dtos: List<ArenaDto> = httpClient.get("/api/arenas/nearby") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("radiusKm", radiusKm)
        }.body()
        dtos.map { it.toDomain() }
    }
    override suspend fun getArenaDetail(arenaId: String): Result<Arena> = runCatching {
        val dto: ArenaDto = httpClient.get("/api/arenas/$arenaId").body()
        dto.toDomain()
    }
    override suspend fun getSports(arenaId: String): Result<List<Sport>> = runCatching {
        val dtos: List<SportDto> = httpClient.get("/api/arenas/$arenaId/sports").body()
        dtos.map { it.toDomain() }
    }
    override suspend fun getSlots(arenaId: String, sportId: String, courtId: String, date: String): Result<List<Slot>> = runCatching {
        val dtos: List<SlotDto> = httpClient.get("/api/arenas/$arenaId/sports/$sportId/courts/$courtId/slots") {
            parameter("date", date)
        }.body()
        dtos.map { it.toDomain() }
    }

    // Home screen specific methods with mock data for development
    override suspend fun getHomeSummary(): Result<HomeSummary> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        HomeSummary(
            upcomingCount = 2,
            favouritesCount = 2,
            thisMonthCount = 3
        )
    }

    override suspend fun getFeaturedArenas(): Result<List<FeaturedArena>> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        listOf(
            FeaturedArena(
                id = "1",
                name = "ABC Sector 51",
                location = "Sector 51, Noida",
                imageUrl = "",
                rating = 4.8f,
                isFavorite = true,
                sports = listOf("Swimming", "Badminton", "Tennis"),
                distance = "2.5 km away",
                priceRange = "₹200-500",
                additionalSportsCount = 1
            ),
            FeaturedArena(
                id = "2",
                name = "SportZone Central",
                location = "Sector 18, Noida",
                imageUrl = "",
                rating = 4.6f,
                isFavorite = false,
                sports = listOf("Swimming", "Basketball", "Badminton"),
                distance = "3.2 km away",
                priceRange = "₹150-400",
                additionalSportsCount = 1
            )
        )
    }

    override suspend fun getAvailableSports(): Result<List<SportCategory>> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        listOf(
            SportCategory("1", "Swimming", "🏊"),
            SportCategory("2", "Badminton", "🏸"),
            SportCategory("3", "Tennis", "🎾"),
            SportCategory("4", "Cricket", "🏏"),
            SportCategory("5", "Paddle", "🏓")
        )
    }

    override suspend fun getUpcomingBookings(): Result<List<UpcomingBooking>> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        listOf(
            UpcomingBooking(
                id = "1",
                title = "ABC Sector 51",
                sport = "Swimming",
                date = "Jan 15, 2025",
                time = "7:00 AM"
            )
        )
    }

    override suspend fun getRecentActivity(): Result<List<RecentActivity>> = runCatching {
        // TODO: Replace with actual API call when backend is ready
        listOf(
            RecentActivity(
                id = "1",
                title = "ABC Sector 51",
                sport = "Swimming",
                date = "Jan 15, 2025",
                time = "7:00 AM",
                price = "₹300",
                status = ActivityStatus.UPCOMING
            )
        )
    }
}

class BookingRepositoryImpl(private val httpClient: HttpClient) : BookingRepository {
    override suspend fun create(request: BookingRequest): Result<Booking> = runCatching {
        val dto: BookingDto = httpClient.post("/api/bookings") {
            contentType(ContentType.Application.Json)
            setBody(request.toDto())
        }.body()
        dto.toDomain()
    }
    override suspend fun fetchBookings(upcoming: Boolean): Result<List<Booking>> = runCatching {
        val dtos: List<BookingDto> = httpClient.get("/api/bookings") {
            parameter("upcoming", upcoming)
        }.body()
        dtos.map { it.toDomain() }
    }
    override suspend fun cancelBooking(bookingId: String): Result<Unit> = runCatching {
        httpClient.delete("/api/bookings/$bookingId")
    }
}

class PaymentRepositoryImpl(private val httpClient: HttpClient) : PaymentRepository {
    override suspend fun createRazorPayOrder(amountCents: Int): Result<String> = runCatching {
        // Copilot: Call RazorPay order API
        "orderId" // Placeholder
    }
    override suspend fun fetchWeeklyReport(): Result<WeeklyReport> = runCatching {
        WeeklyReport() // Placeholder
    }
}
