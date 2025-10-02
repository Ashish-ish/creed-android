package com.creed.android

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform