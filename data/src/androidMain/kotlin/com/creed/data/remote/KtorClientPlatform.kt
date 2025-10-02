package com.creed.data.remote

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun providePlatformEngine(): HttpClientEngine = OkHttp.create()

actual fun provideTokenProvider(): suspend () -> String? = {
    // Copilot: Replace with actual DataStore/KeyValueStorage fetch
    null
}

