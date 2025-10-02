package com.creed.android.platform

/**
 * Expected declarations for Firebase Auth functionality
 * Implemented natively on each platform
 */
expect suspend fun signInWithPhoneNumber(
    phone: String,
    onCodeSent: () -> Unit,
    onVerificationCompleted: (String) -> Unit,
    onError: (String) -> Unit
)

expect suspend fun verifyCode(
    code: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
)

expect fun logAnalyticsEvent(name: String, params: Map<String, Any>)

expect fun recordCrashlyticsException(throwable: Throwable)

expect fun startPerformanceTrace(name: String): PlatformTrace

expect fun setCrashlyticsUserId(id: String)

expect class PlatformTrace {
    fun stop()
}
