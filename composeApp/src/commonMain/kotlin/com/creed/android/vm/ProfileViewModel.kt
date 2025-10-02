package com.creed.android.vm

import com.creed.domain.models.User
import com.creed.usecase.GetUserProfileUseCase
import com.creed.usecase.UpdateUserProfileUseCase
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class ProfileEffect {
    data class ShowSnackBar(val message: String) : ProfileEffect()
    object Logout : ProfileEffect()
}

class ProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase,
    private val updateUserProfile: UpdateUserProfileUseCase
) : BaseViewModel<ProfileState, ProfileEffect>(ProfileState.Idle) {
    fun loadProfile() {
        setState { ProfileState.Loading }
        viewModelScope.launch {
            val result = getUserProfile()
            setState {
                if (result.isSuccess) ProfileState.Success(result.getOrThrow())
                else ProfileState.Error("Failed to load profile")
            }
        }
    }
    fun updateProfile(user: User) {
        setState { ProfileState.Loading }
        viewModelScope.launch {
            val result = updateUserProfile(user)
            setState {
                if (result.isSuccess) ProfileState.Success(result.getOrThrow())
                else ProfileState.Error("Profile update failed")
            }
        }
    }
    fun logout() {
        sendEffect(ProfileEffect.Logout)
    }
}
