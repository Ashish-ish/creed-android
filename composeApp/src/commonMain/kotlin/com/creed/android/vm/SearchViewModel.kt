package com.creed.android.vm

import com.creed.domain.models.Arena
import com.creed.usecase.FetchNearbyArenasUseCase
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val arenas: List<Arena>) : SearchState()
    data class Error(val message: String) : SearchState()
}

sealed class SearchEffect {
    data class ShowSnackBar(val message: String) : SearchEffect()
}

class SearchViewModel(
    private val fetchNearbyArenas: FetchNearbyArenasUseCase
) : BaseViewModel<SearchState, SearchEffect>(SearchState.Idle) {
    fun search(query: String) {
        setState { SearchState.Loading }
        viewModelScope.launch {
            // Copilot: For demo, reuse fetchNearbyArenas with dummy lat/lng
            val result = fetchNearbyArenas(0.0, 0.0)
            setState {
                if (result.isSuccess) SearchState.Success(result.getOrThrow())
                else SearchState.Error("Search failed")
            }
        }
    }
}
