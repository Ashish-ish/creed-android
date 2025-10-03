package com.creed.android.vm

import com.creed.domain.models.User
import com.creed.usecase.AuthenticationUseCase
import com.creed.usecase.LoginResult
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class OtpSent(val phone: String) : LoginState()
    data class UserExists(val user: User) : LoginState()
    object ProfileCompletionRequired : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class LoginEffect {
    data class ShowSnackBar(val message: String) : LoginEffect()
    object NavigateToProfileCompletion : LoginEffect()
    object NavigateToHome : LoginEffect()
}

class LoginViewModel(
    private val authenticationUseCase: AuthenticationUseCase
) : BaseViewModel<LoginState, LoginEffect>(LoginState.Idle) {

    init {
        checkExistingAuth()
    }

    private fun checkExistingAuth() {
        viewModelScope.launch {
            val isLoggedIn = authenticationUseCase.isUserLoggedIn()
            if (isLoggedIn) {
                val user = authenticationUseCase.getCurrentUser()
                sendEffect(LoginEffect.NavigateToHome)
            }
        }
    }

    fun sendOtp(phone: String) {
        setState { LoginState.Loading }
        viewModelScope.launch {
            try {
                val result = authenticationUseCase.requestOtp("+91$phone")
                if (result.isSuccess) {
                    setState { LoginState.OtpSent(phone) }
                } else {
                    setState { LoginState.Error("Failed to send OTP: ${result.exceptionOrNull()?.message}") }
                }
            } catch (e: Exception) {
                setState { LoginState.Error("Failed to send OTP: ${e.message}") }
            }
        }
    }

    fun verifyOtp(phone: String, otp: String) {
        setState { LoginState.Loading }
        viewModelScope.launch {
            try {
                val result = authenticationUseCase.verifyOtpAndLogin("+91$phone", otp)
                if (result.isSuccess) {
                    when (val loginResult = result.getOrThrow()) {
                        is LoginResult.Success -> {
                            val user = loginResult.user
                            if (user != null) {
                                setState { LoginState.UserExists(user) }
                                sendEffect(LoginEffect.NavigateToHome)
                            } else {
                                // User exists but profile fetch failed, might need completion
                                setState { LoginState.ProfileCompletionRequired }
                                sendEffect(LoginEffect.NavigateToProfileCompletion)
                            }
                        }
                        is LoginResult.ProfileCompletionRequired -> {
                            setState { LoginState.ProfileCompletionRequired }
                            sendEffect(LoginEffect.NavigateToProfileCompletion)
                        }
                    }
                } else {
                    setState { LoginState.Error("OTP verification failed: ${result.exceptionOrNull()?.message}") }
                }
            } catch (e: Exception) {
                setState { LoginState.Error("Verification failed: ${e.message}") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authenticationUseCase.logout()
            setState { LoginState.Idle }
        }
    }

    fun clearError() {
        if (state.value is LoginState.Error) {
            setState { LoginState.Idle }
        }
    }

    fun resetToPhoneInput() {
        setState { LoginState.Idle }
    }
}
