package com.creed.usecase

import com.creed.data.local.KeyValueStorage
import com.creed.data.local.StorageKeys
import com.creed.data.local.getObject
import com.creed.domain.models.*
import com.creed.domain.repository.AuthRepository
import com.creed.domain.repository.UserRepository

/**
 * Comprehensive authentication use case that handles the complete login flow:
 * 1. Check if user is already logged in
 * 2. Firebase OTP verification
 * 3. Check if user exists on backend
 * 4. Register or login user
 * 5. Profile completion if needed
 */
class AuthenticationUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val keyValueStorage: KeyValueStorage
) {

    /**
     * Check if user is already authenticated by looking for stored auth token
     */
    suspend fun isUserLoggedIn(): Boolean {
        val authToken = keyValueStorage.getString(StorageKeys.AUTH_TOKEN)
        return !authToken.isNullOrEmpty()
    }

    /**
     * Get current user profile from local storage
     */
    suspend fun getCurrentUser(): User? {
        return keyValueStorage.getObject<User>(StorageKeys.USER_JSON)
    }

    /**
     * Complete login flow starting with OTP request
     */
    suspend fun requestOtp(phoneNumber: String): Result<Unit> {
        return authRepository.requestOtp(phoneNumber)
    }

    /**
     * Verify OTP and handle the complete authentication flow
     */
    suspend fun verifyOtpAndLogin(phoneNumber: String, otp: String): Result<LoginResult> {
        return try {
            // Step 1: Verify OTP with Firebase
            val firebaseUserResult = authRepository.verifyOtp(phoneNumber, otp)
            if (firebaseUserResult.isFailure) {
                return Result.failure(firebaseUserResult.exceptionOrNull() ?: Exception("OTP verification failed"))
            }

            val firebaseUser = firebaseUserResult.getOrThrow()

            // Step 2: Check if user exists on backend
            val userExistsResult = authRepository.checkUserExists(firebaseUser.token)
            if (userExistsResult.isFailure) {
                return Result.failure(userExistsResult.exceptionOrNull() ?: Exception("Failed to check user existence"))
            }

            val userExists = userExistsResult.getOrThrow().exists

            // Step 3: Register or Login based on user existence
            val authTokensResult = if (userExists) {
                // User exists, login
                authRepository.loginUser(firebaseUser.token)
            } else {
                // User doesn't exist, register
                authRepository.registerUser(firebaseUser.token)
            }

            if (authTokensResult.isFailure) {
                return Result.failure(authTokensResult.exceptionOrNull() ?: Exception("Authentication failed"))
            }

            // Step 4: Determine if profile completion is needed
            val loginResult = if (userExists) {
                // User exists, try to get their profile
                val profileResult = userRepository.getProfile()
                if (profileResult.isSuccess) {
                    LoginResult.Success(profileResult.getOrThrow())
                } else {
                    LoginResult.Success(null) // Profile fetch failed, might need completion
                }
            } else {
                // New user, needs profile completion
                LoginResult.ProfileCompletionRequired
            }

            Result.success(loginResult)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Complete user profile after registration
     */
    suspend fun completeProfile(profileRequest: ProfileUpdateRequest): Result<User> {
        return userRepository.updateProfile(profileRequest)
    }

    /**
     * Logout user by clearing all stored data
     */
    suspend fun logout() {
        keyValueStorage.remove(StorageKeys.FIREBASE_TOKEN)
        keyValueStorage.remove(StorageKeys.AUTH_TOKEN)
        keyValueStorage.remove(StorageKeys.REFRESH_TOKEN)
        keyValueStorage.remove(StorageKeys.USER_ID)
        keyValueStorage.remove(StorageKeys.USER_JSON)
    }
}

/**
 * Result of the login process
 */
sealed class LoginResult {
    data class Success(val user: User?) : LoginResult()
    object ProfileCompletionRequired : LoginResult()
}
