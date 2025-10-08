package com.creed.data.remote

import com.creed.data.local.KeyValueStorage
import com.creed.data.local.StorageKeys
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun providePlatformEngine(): HttpClientEngine
expect fun provideTokenProvider(): suspend () -> String?

object KtorClientProvider {
    fun createClient(keyValueStorage: KeyValueStorage): HttpClient {
        return HttpClient(providePlatformEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url("http://10.0.2.2:3000/")
            }
            install(AuthHeaderPlugin) {
                this.tokenProvider = { keyValueStorage.getString(StorageKeys.AUTH_TOKEN) }
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, _ ->
                    // Copilot: Map 4xx/5xx to custom exceptions
                    throw cause
                }
            }
        }
    }
}

class AuthHeaderPluginConfig {
    var tokenProvider: (suspend () -> String?)? = null
}

val AuthHeaderPlugin = createClientPlugin("AuthHeaderPlugin", ::AuthHeaderPluginConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    onRequest { request, _ ->
        if (tokenProvider != null) {
            val token = tokenProvider.invoke()
            val path = request.url.encodedPath
            if (!path.endsWith("/api/users/check") &&
                !path.endsWith("/api/users/register") &&
                !path.endsWith("/api/users/login") &&
                !token.isNullOrBlank()
            ) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
