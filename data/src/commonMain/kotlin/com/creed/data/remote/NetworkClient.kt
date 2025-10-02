package com.creed.data.remote

import com.creed.data.local.KeyValueStorage

interface NetworkClient {
    suspend fun <T> get(url: String, parameters: Map<String, Any> = emptyMap()): T
    suspend fun <T> post(url: String, body: Any? = null, headers: Map<String, String> = emptyMap()): T
    suspend fun <T> patch(url: String, body: Any? = null): T
    suspend fun delete(url: String)
}

class KtorNetworkClient(private val keyValueStorage: KeyValueStorage) : NetworkClient {
    private val httpClient by lazy {
        KtorClientProvider.createClient {
            keyValueStorage.getString("AUTH_TOKEN")
        }
    }

    override suspend fun <T> get(url: String, parameters: Map<String, Any>): T {
        TODO("Implement with httpClient.get")
    }

    override suspend fun <T> post(url: String, body: Any?, headers: Map<String, String>): T {
        TODO("Implement with httpClient.post")
    }

    override suspend fun <T> patch(url: String, body: Any?): T {
        TODO("Implement with httpClient.patch")
    }

    override suspend fun delete(url: String) {
        TODO("Implement with httpClient.delete")
    }
}
