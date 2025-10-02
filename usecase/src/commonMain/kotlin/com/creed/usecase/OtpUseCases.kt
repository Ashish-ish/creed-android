package com.creed.usecase

import com.creed.data.otp.OtpDataSource

class RequestOtpUseCase(private val otpDataSource: OtpDataSource) {
    suspend operator fun invoke(phone: String) = otpDataSource.requestOtp(phone)
}

class VerifyOtpUseCase(private val otpDataSource: OtpDataSource) {
    suspend operator fun invoke(phone: String, otp: String) = otpDataSource.verifyOtp(phone, otp)
}

