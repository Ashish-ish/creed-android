package com.creed.android.vm

import com.creed.android.vm.BaseViewModel
import com.creed.domain.models.User
import com.creed.domain.models.ProfileUpdateRequest
import com.creed.usecase.AuthenticationUseCase
import kotlinx.coroutines.launch

sealed class ProfileCompletionEffect {
    object NavigateToLocationPermission : ProfileCompletionEffect()
    object NavigateToHome : ProfileCompletionEffect()
    data class ShowSnackBar(val message: String) : ProfileCompletionEffect()
}

class ProfileCompletionViewModel(
    private val authenticationUseCase: AuthenticationUseCase
) : BaseViewModel<ProfileCompletionState, ProfileCompletionEffect>(ProfileCompletionState.Idle) {

    fun submitProfile(
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String
    ) {
        setState { ProfileCompletionState.Loading }
        viewModelScope.launch {
            try {
                val profileRequest = ProfileUpdateRequest(
                    firstName = firstName,
                    lastName = lastName,
                    gender = gender,
                    dateOfBirth = dateOfBirth
                )

                val result = authenticationUseCase.completeProfile(profileRequest)
                if (result.isSuccess) {
                    setState { ProfileCompletionState.Success(result.getOrThrow()) }
                    sendEffect(ProfileCompletionEffect.NavigateToHome)
                } else {
                    setState { ProfileCompletionState.Error("Profile completion failed: ${result.exceptionOrNull()?.message}") }
                }
            } catch (e: Exception) {
                setState { ProfileCompletionState.Error("Profile completion failed: ${e.message}") }
            }
        }
    }

    fun clearError() {
        if (state.value is ProfileCompletionState.Error) {
            setState { ProfileCompletionState.Idle }
        }
    }
}

sealed class ProfileCompletionState {
    object Idle : ProfileCompletionState()
    object Loading : ProfileCompletionState()
    data class Success(val user: User) : ProfileCompletionState()
    data class Error(val message: String) : ProfileCompletionState()
}
