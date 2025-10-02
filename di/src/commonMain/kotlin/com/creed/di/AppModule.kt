package com.creed.di

import com.creed.data.repository.*
import com.creed.domain.repository.*
import io.ktor.client.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    // HTTP Client
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                level = LogLevel.HEADERS
            }
            install(DefaultRequest) {
                url("https://api.creed.com") // TODO: Replace with actual API base URL
                // TODO: Add Authorization header from token provider
            }
        }
    }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ArenaRepository> { ArenaRepositoryImpl(get()) }
    single<BookingRepository> { BookingRepositoryImpl(get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
}
