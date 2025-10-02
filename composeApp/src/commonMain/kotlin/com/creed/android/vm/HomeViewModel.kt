package com.creed.android.vm

import com.creed.domain.models.Arena
import com.creed.usecase.FetchNearbyArenasUseCase
import kotlinx.coroutines.launch

sealed class HomeState {
    object Idle : HomeState()
    object Loading : HomeState()
    data class Success(val arenas: List<Arena>) : HomeState()
    data class Error(val message: String) : HomeState()
}

sealed class HomeEffect {
    data class ShowSnackBar(val message: String) : HomeEffect()
}

class HomeViewModel(
    private val fetchNearbyArenas: FetchNearbyArenasUseCase
) : BaseViewModel<HomeState, HomeEffect>(HomeState.Idle) {
    fun refresh(lat: Double, lng: Double) {
        setState { HomeState.Loading }
        viewModelScope.launch {
            val result = fetchNearbyArenas(lat, lng)
            setState {
                if (result.isSuccess) HomeState.Success(result.getOrThrow())
                else HomeState.Error("Failed to load arenas")
            }
        }
    }
}
