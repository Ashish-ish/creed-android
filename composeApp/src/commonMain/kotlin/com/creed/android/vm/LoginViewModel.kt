package com.creed.android.vm

import com.creed.android.platform.signInWithPhoneNumber
import com.creed.android.platform.verifyCode
import com.creed.domain.models.User
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class OtpSent(val phone: String) : LoginState()
    data class OtpVerified(val firebaseToken: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class LoginEffect {
    data class ShowSnackBar(val message: String) : LoginEffect()
    object NavigateToProfileCompletion : LoginEffect()
    object NavigateToHome : LoginEffect()
}

class LoginViewModel : BaseViewModel<LoginState, LoginEffect>(LoginState.Idle) {
    fun sendOtp(phone: String) {
        setState { LoginState.Loading }
        viewModelScope.launch {
            try {
                signInWithPhoneNumber(
                    phone = "+91$phone",
                    onCodeSent = {
                        setState { LoginState.OtpSent(phone) }
                    },
                    onVerificationCompleted = { firebaseToken ->
                        // Auto-verification completed
                        setState { LoginState.OtpVerified(firebaseToken) }
                        // TODO: Call backend API to register/login with firebaseToken
                        sendEffect(LoginEffect.NavigateToProfileCompletion)
                    },
                    onError = { error ->
                        setState { LoginState.Error("Failed to send OTP: $error") }
                    }
                )
            } catch (e: Exception) {
                setState { LoginState.Error("Failed to send OTP: ${e.message}") }
            }
        }
    }

    fun verifyOtp(otp: String) {
        setState { LoginState.Loading }
        viewModelScope.launch {
            try {
                verifyCode(
                    code = otp,
                    onSuccess = { firebaseToken ->
                        setState { LoginState.OtpVerified(firebaseToken) }
                        // TODO: Call backend API to register/login with firebaseToken
                        // For now, navigate to profile completion
                        sendEffect(LoginEffect.NavigateToProfileCompletion)
                    },
                    onError = { error ->
                        setState { LoginState.Error("Invalid OTP: $error") }
                    }
                )
            } catch (e: Exception) {
                setState { LoginState.Error("Verification failed: ${e.message}") }
            }
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
