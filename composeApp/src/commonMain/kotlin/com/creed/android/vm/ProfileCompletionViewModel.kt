package com.creed.android.vm

import com.creed.android.vm.BaseViewModel

import com.creed.domain.models.User
import com.creed.usecase.UpdateUserProfileUseCase
import kotlinx.coroutines.launch
sealed class ProfileCompletionEffect {
    object NavigateToLocationPermission : ProfileCompletionEffect()
    data class ShowSnackBar(val message: String) : ProfileCompletionEffect()
}

class ProfileCompletionViewModel(
    private val updateUserProfile: UpdateUserProfileUseCase
) : BaseViewModel<ProfileCompletionState, ProfileCompletionEffect>(ProfileCompletionState.Idle) {
    fun submitProfile(user: User) {
        setState { ProfileCompletionState.Loading }
        viewModelScope.launch {
            val result = updateUserProfile(user)
            setState {
                if (result.isSuccess) ProfileCompletionState.Success(result.getOrThrow())
                else ProfileCompletionState.Error("Profile update failed")
            }
            if (result.isSuccess) sendEffect(ProfileCompletionEffect.NavigateToLocationPermission)
        }
    }
}

sealed class ProfileCompletionState {
    object Idle : ProfileCompletionState()
    object Loading : ProfileCompletionState()
    data class Success(val user: User) : ProfileCompletionState()
    data class Error(val message: String) : ProfileCompletionState()
}
