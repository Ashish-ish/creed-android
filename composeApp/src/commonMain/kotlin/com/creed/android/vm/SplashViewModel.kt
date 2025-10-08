package com.creed.android.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creed.data.location.LocationService
import com.creed.data.location.LocationPermissionStatus
import com.creed.usecase.AuthenticationUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashState {
    object Loading : SplashState()
    object NavigateToLogin : SplashState()
    object NavigateToLocationPermission : SplashState()
    object NavigateToHome : SplashState()
}

class SplashViewModel(
    private val authenticationUseCase: AuthenticationUseCase,
    private val locationService: LocationService
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            // Show splash for minimum 2 seconds
            delay(2000)

            try {
                val isLoggedIn = authenticationUseCase.isUserLoggedIn()

                if (isLoggedIn) {
                    // User is logged in, check location permission
                    val permissionStatus = locationService.checkPermissionStatus()
                    if (permissionStatus == LocationPermissionStatus.GRANTED) {
                        _state.value = SplashState.NavigateToHome
                    } else {
                        _state.value = SplashState.NavigateToLocationPermission
                    }
                } else {
                    // User not logged in
                    _state.value = SplashState.NavigateToLogin
                }
            } catch (e: Exception) {
                // If any error occurs, navigate to login
                _state.value = SplashState.NavigateToLogin
            }
        }
    }
}
