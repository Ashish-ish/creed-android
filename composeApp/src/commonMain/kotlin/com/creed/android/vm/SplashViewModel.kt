package com.creed.android.vm

import com.creed.usecase.GetUserProfileUseCase
import kotlinx.coroutines.launch

sealed class SplashState {
    object Idle : SplashState()
    object Loading : SplashState()
    data class Success(val hasToken: Boolean) : SplashState()
    data class Error(val message: String) : SplashState()
}

class SplashViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : BaseViewModel<SplashState, Unit>(SplashState.Idle) {
    fun checkAuthToken() {
        setState { SplashState.Loading }
        viewModelScope.launch {
            val result = getUserProfileUseCase()
            setState {
                when {
                    result.isSuccess -> SplashState.Success(true)
                    else -> SplashState.Success(false)
                }
            }
        }
    }
}

// Copilot: Repeat similar pattern for LoginViewModel, ProfileCompletionViewModel, etc.
