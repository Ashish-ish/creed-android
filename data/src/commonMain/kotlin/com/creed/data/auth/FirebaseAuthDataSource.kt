package com.creed.data.auth

import com.creed.domain.models.FirebaseUser

/**
 * Firebase authentication data source interface for platform-specific implementations
 */
interface FirebaseAuthDataSource {
    suspend fun signInWithPhoneNumber(
        phone: String,
        onCodeSent: () -> Unit,
        onVerificationCompleted: (String) -> Unit,
        onError: (String) -> Unit
    )

    suspend fun verifyCode(
        code: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )
}
