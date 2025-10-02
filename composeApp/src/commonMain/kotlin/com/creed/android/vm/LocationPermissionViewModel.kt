package com.creed.android.vm

sealed class LocationPermissionState {
    object Idle : LocationPermissionState()
    object Requesting : LocationPermissionState()
    object Granted : LocationPermissionState()
    object Denied : LocationPermissionState()
}

sealed class LocationPermissionEffect {
    object NavigateToHome : LocationPermissionEffect()
    data class ShowSnackBar(val message: String) : LocationPermissionEffect()
}

class LocationPermissionViewModel : BaseViewModel<LocationPermissionState, LocationPermissionEffect>(LocationPermissionState.Idle) {
    fun onPermissionResult(granted: Boolean) {
        setState { if (granted) LocationPermissionState.Granted else LocationPermissionState.Denied }
        if (granted) sendEffect(LocationPermissionEffect.NavigateToHome)
        else sendEffect(LocationPermissionEffect.ShowSnackBar("Location permission denied"))
    }
}

