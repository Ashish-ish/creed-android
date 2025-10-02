package com.creed.data.remote

import com.creed.data.local.KeyValueStorage
import io.ktor.client.HttpClient

object HttpClientFactory {
    fun create(keyValueStorage: KeyValueStorage): HttpClient {
        return KtorClientProvider.createClient {
            keyValueStorage.getString("AUTH_TOKEN")
        }
    }
}
