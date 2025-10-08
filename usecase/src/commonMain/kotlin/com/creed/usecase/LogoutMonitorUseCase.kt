package com.creed.usecase

import com.creed.data.local.KeyValueStorage
import com.creed.data.local.StorageKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Use case that monitors authentication state and triggers logout when auth token becomes null
 */
class LogoutMonitorUseCase(
    private val keyValueStorage: KeyValueStorage
) {

    /**
     * Observes auth token changes and emits true when user should be logged out (token becomes null)
     */
    fun observeLogoutState(): Flow<Boolean> {
        return keyValueStorage.getStringValueUpdates(StorageKeys.AUTH_TOKEN)
            .map { authToken ->
                // Emit true when token becomes null (should logout)
                authToken == null
            }
            .distinctUntilChanged() // Only emit when the logout state actually changes
    }

    /**
     * Clear user session data when logging out
     */
    suspend fun clearUserSession() {
        keyValueStorage.remove(StorageKeys.AUTH_TOKEN)
        keyValueStorage.remove(StorageKeys.REFRESH_TOKEN)
        keyValueStorage.remove(StorageKeys.FIREBASE_TOKEN)
        keyValueStorage.remove(StorageKeys.USER_ID)
        keyValueStorage.remove(StorageKeys.USER_JSON)
        // Keep location data as it's not user-specific
    }
}
