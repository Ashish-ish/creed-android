package com.creed.data.remote

import com.creed.data.local.KeyValueStorage
import com.creed.data.local.StorageKeys
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

expect fun providePlatformEngine(): HttpClientEngine
expect fun provideTokenProvider(): suspend () -> String?

// ---------------------------
// Token refresh response model
// ---------------------------
@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(val token: String?, val refreshToken: String?)

// ---------------------------
// Client Provider
// ---------------------------
object KtorClientProvider {
    fun createClient(keyValueStorage: KeyValueStorage): HttpClient {
        return HttpClient(providePlatformEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        println("💬 KtorLog: $message")
                    }
                }
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url("http://10.0.2.2:3000/") // For Android emulator
            }
            install(AuthHeaderPluginRenew) {
                this.tokenProvider = { keyValueStorage.getString(StorageKeys.AUTH_TOKEN) }
                this.logoutHandler = { keyValueStorage.remove(StorageKeys.AUTH_TOKEN) }

                // 🔁 Token renewal logic
                this.renewToken = { oldToken ->
                    val refreshToken = keyValueStorage.getString(StorageKeys.REFRESH_TOKEN)
                    //if (refreshToken.isNullOrBlank()) return@renewToken

                    val client = HttpClient(providePlatformEngine()) {
                        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                    }

                    try {
                        val response = client.post("http://10.0.2.2:3000/api/users/refresh-token") {
                            setBody(RefreshTokenRequest(refreshToken.orEmpty()))
                        }

                        if (response.status == HttpStatusCode.OK) {
                            val data = response.body<RefreshTokenResponse>()
                            if (!data.token.isNullOrBlank()) {
                                keyValueStorage.putString(StorageKeys.AUTH_TOKEN, data.token)
                                data.refreshToken?.let {
                                    keyValueStorage.putString(StorageKeys.REFRESH_TOKEN, it)
                                }
                                data.token
                            }
                        }
                    } catch (e: Exception) {
                        println("Token refresh failed: ${e.message}")
                        keyValueStorage.remove(StorageKeys.AUTH_TOKEN)
                    } finally {
                        client.close()
                    }
                    null
                }

                this.fetchLatestToken = { keyValueStorage.getString(StorageKeys.AUTH_TOKEN) }
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, _ ->
                    throw cause
                }
            }
        }
    }
}

// ---------------------------
// Auth Plugin Config
// ---------------------------
class AuthHeaderPluginConfig {
    var tokenProvider: (suspend () -> String?)? = null
    var renewToken: (suspend (String) -> String?)? = null
    var logoutHandler: (suspend () -> Unit)? = null
    var fetchLatestToken: (suspend () -> String?)? = null
}

// ---------------------------
// Auth Header Plugin with Auto-Renew
// ---------------------------
@OptIn(ExperimentalEncodingApi::class, ExperimentalTime::class)
val AuthHeaderPluginRenew = createClientPlugin("AuthHeaderPluginRenew", ::AuthHeaderPluginConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    val renewToken = pluginConfig.renewToken
    val logoutHandler = pluginConfig.logoutHandler
    val fetchLatestToken = pluginConfig.fetchLatestToken
    val refreshMutex = Mutex()

    suspend fun decodeJwtExpiry(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payloadJson = Base64.UrlSafe.decode(parts[1]).decodeToString()
            val json = Json.parseToJsonElement(payloadJson) as JsonObject
            json["exp"]?.jsonPrimitive?.longOrNull?.times(1000)
        } catch (e: Exception) {
            null
        }
    }

    onRequest { request, _ ->
        val rawToken = tokenProvider?.invoke()
        if (rawToken.isNullOrBlank()) return@onRequest

        var validToken = rawToken
        val expiryTime = decodeJwtExpiry(rawToken)
        val isExpired = expiryTime?.let { Clock.System.now().toEpochMilliseconds() > it } ?: false

        if (isExpired && renewToken != null) {
            refreshMutex.withLock {
                try {
                    val latestToken = fetchLatestToken?.invoke()
                    val stillExpired = latestToken == null ||
                            (decodeJwtExpiry(latestToken)?.let {
                                Clock.System.now().toEpochMilliseconds() > it
                            } ?: true)

                    if (stillExpired) {
                        val newToken = renewToken.invoke(rawToken)
                        if (!newToken.isNullOrBlank()) {
                            validToken = newToken
                        } else {
                            logoutHandler?.invoke()
                            return@withLock
                        }
                    } else {
                        validToken = latestToken!!
                    }
                } catch (e: Exception) {
                    logoutHandler?.invoke()
                    return@withLock
                }
            }
        }

        val path = request.url.encodedPath
        val noAuthEndpoints = listOf(
            "/api/users/check",
            "/api/users/register",
            "/api/users/login",
            "/api/users/refresh-token"
        )

        if (!noAuthEndpoints.any { path.endsWith(it) }) {
            request.headers.append(HttpHeaders.Authorization, "Bearer $validToken")
        }
    }

    onResponse { response ->
        if (response.status == HttpStatusCode.Unauthorized) {
            logoutHandler?.invoke()
        }
    }
}