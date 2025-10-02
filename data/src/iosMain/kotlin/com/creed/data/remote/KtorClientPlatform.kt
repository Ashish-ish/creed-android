package com.creed.data.remote

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun providePlatformEngine(): HttpClientEngine = Darwin.create()

actual fun provideTokenProvider(): suspend () -> String? = {
    // Copilot: Replace with actual NSUserDefaults/KeyValueStorage fetch
    null
}

