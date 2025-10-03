package com.creed.di

import com.creed.data.remote.KtorClientProvider.createClient
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
    // HTTP Client - keeping this separate from CommonModule to avoid conflicts
    single<HttpClient> {
        createClient(get())
    }

    // Repositories are already defined in CommonModule - removing duplicates
}
