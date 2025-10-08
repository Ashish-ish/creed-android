package com.creed.android.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creed.usecase.LogoutMonitorUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Global ViewModel that monitors logout state and triggers navigation to login when needed
 */
class LogoutMonitorViewModel(
    private val logoutMonitorUseCase: LogoutMonitorUseCase
) : ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    init {
        monitorLogoutState()
    }

    private fun monitorLogoutState() {
        viewModelScope.launch {
            logoutMonitorUseCase.observeLogoutState().collect { shouldLogout ->
                if (shouldLogout) {
                    // Clear user session data
                    logoutMonitorUseCase.clearUserSession()
                    // Emit logout event for navigation
                    _logoutEvent.emit(Unit)
                }
            }
        }
    }
}
