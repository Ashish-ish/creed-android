package com.creed.data.otp

import kotlinx.coroutines.delay

class OtpDataSourceIos : OtpDataSource {
    override suspend fun requestOtp(phone: String): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
    override suspend fun verifyOtp(phone: String, otp: String): Result<Boolean> {
        delay(500)
        return Result.success(otp == "123456")
    }
}

