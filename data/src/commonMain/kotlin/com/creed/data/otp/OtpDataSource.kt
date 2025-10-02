package com.creed.data.otp

interface OtpDataSource {
    suspend fun requestOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<Boolean>
}

