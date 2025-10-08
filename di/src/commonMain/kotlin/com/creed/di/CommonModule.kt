package com.creed.di


import com.creed.data.repository.*
import com.creed.domain.repository.*
import com.creed.usecase.*
import com.creed.data.otp.OtpDataSource
import com.creed.data.payment.PaymentDataSource
import com.creed.data.location.LocationService
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule: Module = module {
    // KeyValueStorage is platform-specific and should be provided by androidMain/iosMain modules
    // Removed the circular dependency: single<KeyValueStorage> { get() }

    // Use HttpClient from AppModule instead of HttpClientFactory to avoid conflicts
    // Repositories with HttpClient and KeyValueStorage injection
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<ArenaRepository> { ArenaRepositoryImpl(get()) }
    single<BookingRepository> { BookingRepositoryImpl(get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }

    // Firebase Auth DataSource (platform-specific in androidMain/iosMain)
    single<com.creed.data.auth.FirebaseAuthDataSource> { get() }

    // OTP and Payment DataSources (platform-specific in androidMain/iosMain)
    single<OtpDataSource> { get() }
    single<PaymentDataSource> { get() }

    // Use-cases
    factory { RequestOtpUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { CheckUserExistsUseCase(get()) }
    factory { RegisterUserUseCase(get()) }
    factory { LoginUserUseCase(get()) }
    factory { GetUserProfileUseCase(get()) }
    factory { UpdateUserProfileUseCase(get()) }
    factory { FetchNearbyArenasUseCase(get()) }
    factory { FetchArenaDetailUseCase(get()) }
    factory { FetchSportsUseCase(get()) }
    factory { CreateBookingUseCase(get()) }
    factory { FetchBookingsUseCase(get()) }
    factory { CancelBookingUseCase(get()) }
    factory { FetchWeeklyReportUseCase(get()) }
    factory { CreatePaymentOrderUseCase(get()) }
    factory { ConfirmPaymentUseCase(get()) }

    // Comprehensive Authentication Use Case
    factory { AuthenticationUseCase(get(), get(), get()) }

    // Logout monitoring
    factory { LogoutMonitorUseCase(get()) }

    factory { FetchSearchArenasUseCase(get()) }


}
