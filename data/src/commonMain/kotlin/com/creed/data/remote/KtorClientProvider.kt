package com.creed.data.remote

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
    fun createClient(tokenProvider: suspend () -> String?): HttpClient {
        return HttpClient(providePlatformEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.HEADERS
            }
            defaultRequest {
                // Copilot: Token must be injected via plugin/interceptor, not here (suspend not allowed)
            }
            install(AuthHeaderPlugin) {
                this.tokenProvider = tokenProvider
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
