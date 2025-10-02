package com.creed.di

import com.creed.data.local.KeyValueStorage
import com.creed.data.remote.HttpClientFactory
import com.creed.data.repository.*
import com.creed.domain.repository.*
import com.creed.usecase.*
import com.creed.data.otp.OtpDataSource
import com.creed.data.payment.PaymentDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule: Module = module {
    single<KeyValueStorage> { get() } // Platform-specific provided in androidMain/iosMain

    // HttpClient created via factory in data module
    single {
        val keyValueStorage: KeyValueStorage = get()
        HttpClientFactory.create(keyValueStorage)
    }

    // Repositories with HttpClient injection
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ArenaRepository> { ArenaRepositoryImpl(get()) }
    single<BookingRepository> { BookingRepositoryImpl(get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }

    // OTP and Payment DataSources (platform-specific in androidMain/iosMain)
    single<OtpDataSource> { get() }
    single<PaymentDataSource> { get() }

    // Use-cases
    factory { RequestOtpUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { RegisterUserUseCase(get()) }
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
}
