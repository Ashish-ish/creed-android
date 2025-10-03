package com.creed.android.vm

import com.creed.usecase.AuthenticationUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

sealed class SplashState {
    object Loading : SplashState()
    object NavigateToHome : SplashState()
    object NavigateToLogin : SplashState()
}

class SplashViewModel(
    private val authenticationUseCase: AuthenticationUseCase
) : BaseViewModel<SplashState, Nothing>(SplashState.Loading) {

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            // Check if user is logged in via KeyValueStorage
            val isLoggedIn = authenticationUseCase.isUserLoggedIn()

            if (isLoggedIn) {
                // User is logged in, check if they have a complete profile
                val currentUser = authenticationUseCase.getCurrentUser()
                if (currentUser != null) {
                    // User has complete profile, navigate to home
                    setState { SplashState.NavigateToHome }
                } else {
                    // User is logged in but profile might be incomplete, navigate to login to handle
                    setState { SplashState.NavigateToLogin }
                }
            } else {
                // User is not logged in, navigate to login
                setState { SplashState.NavigateToLogin }
            }
        }
    }
}
