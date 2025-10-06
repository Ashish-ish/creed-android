package com.creed.data.repository

import com.creed.data.remote.dto.*
import com.creed.data.remote.mapper.*
import com.creed.data.local.KeyValueStorage
import com.creed.data.local.StorageKeys
import com.creed.data.auth.FirebaseAuthDataSource
import com.creed.data.local.putObject
import com.creed.domain.models.*
import com.creed.domain.repository.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val keyValueStorage: KeyValueStorage,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun requestOtp(phone: String): Result<Unit> = runCatching {
        suspendCancellableCoroutine { continuation ->
            CoroutineScope(continuation.context).launch {
                firebaseAuthDataSource.signInWithPhoneNumber(
                    phone = phone,
                    onCodeSent = {
                        continuation.resume(Unit)
                    },
                    onVerificationCompleted = { token ->
                        // Auto verification completed, we'll handle this in verifyOtp
                        continuation.resume(Unit)
                    },
                    onError = { error ->
                        throw Exception("OTP request failed: $error")
                    }
                )
            }
        }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<FirebaseUser> = runCatching {
        suspendCancellableCoroutine { continuation ->
            CoroutineScope(continuation.context).launch {
                firebaseAuthDataSource.verifyCode(
                    code = otp,
                    onSuccess = { firebaseToken ->
                        val firebaseUser = FirebaseUser(
                            uid = "firebase_uid", // Firebase will provide this
                            token = firebaseToken
                        )
                        continuation.resume(firebaseUser)
                    },
                    onError = { error ->
                        throw Exception("OTP verification failed: $error")
                    }
                )
            }
        }
    }

    override suspend fun checkUserExists(firebaseToken: String): Result<UserExistsResponse> = runCatching {
        val response: UserExistsResponseDto = httpClient.get("/api/users/check") {
            header("Firebase-Token", firebaseToken)
        }.body()
        response.toDomain()
    }

    override suspend fun registerUser(firebaseToken: String): Result<AuthTokens> = runCatching {
        val response: AuthTokensDto = httpClient.post("/api/users/register") {
            header("Firebase-Token", firebaseToken)
        }.body()

        val authTokens = response.toDomain()

        // Save tokens to local storage
        keyValueStorage.putString(StorageKeys.FIREBASE_TOKEN, firebaseToken)
        keyValueStorage.putString(StorageKeys.AUTH_TOKEN, authTokens.authToken)
        keyValueStorage.putString(StorageKeys.REFRESH_TOKEN, authTokens.refreshToken)
        keyValueStorage.putString(StorageKeys.USER_ID, authTokens.userId)

        authTokens
    }

    override suspend fun loginUser(firebaseToken: String): Result<AuthTokens> = runCatching {
        val response: AuthTokensDto = httpClient.post("/api/users/login") {
            header("Firebase-Token", firebaseToken)
        }.body()

        val authTokens = response.toDomain()

        // Save tokens to local storage
        keyValueStorage.putString(StorageKeys.FIREBASE_TOKEN, firebaseToken)
        keyValueStorage.putString(StorageKeys.AUTH_TOKEN, authTokens.authToken)
        keyValueStorage.putString(StorageKeys.REFRESH_TOKEN, authTokens.refreshToken)
        keyValueStorage.putString(StorageKeys.USER_ID, authTokens.userId)

        authTokens
    }
}

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val keyValueStorage: KeyValueStorage
) : UserRepository {

    override suspend fun getProfile(): Result<User> = runCatching {
        val response: User = httpClient.get("/api/users/profile").body()

        // Save user profile to local storage
        keyValueStorage.putObject(StorageKeys.USER_JSON,response)

        response
    }

    override suspend fun updateProfile(profileRequest: ProfileUpdateRequest): Result<User> = runCatching {
        val response: User = httpClient.patch("/api/users/profile") {
            contentType(ContentType.Application.Json)
            setBody(profileRequest)
        }.body()

        // Save updated user profile to local storage
        keyValueStorage.putObject(StorageKeys.USER_JSON,response)

        response
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

    override suspend fun searchArenas(query: String, location: String?, sport: String?, city: String?): Result<SearchResponse> = runCatching {
        val response: SearchResponseDto = httpClient.get("/api/arenas/search") {
            parameter("q", query)
            location?.let { parameter("location", it) }
            sport?.let { parameter("sport", it) }
            city?.let { parameter("city", it) }
        }.body()
        response.data.toDomain()
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
        WeeklyReport(totalBookings = 1, totalRevenue = 1000.0, popularSports = listOf()) // Placeholder
    }
}
