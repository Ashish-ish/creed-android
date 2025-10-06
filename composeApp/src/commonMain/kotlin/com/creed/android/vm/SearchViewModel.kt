package com.creed.android.vm

import com.creed.domain.models.SearchArena
import com.creed.usecase.FetchSearchArenasUseCase
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val arenas: List<SearchArena>) : SearchState()
    data class Error(val message: String) : SearchState()
}

sealed class SearchEffect {
    data class ShowSnackBar(val message: String) : SearchEffect()
}

class SearchViewModel(
    private val fetchSearchArenas: FetchSearchArenasUseCase
) : BaseViewModel<SearchState, SearchEffect>(SearchState.Idle) {
    fun search(query: String) {
        setState { SearchState.Loading }
        viewModelScope.launch {
            val result = fetchSearchArenas(query)
            setState {
                result.fold(
                    onSuccess = { SearchState.Success(it.arenas) },
                    onFailure = { SearchState.Error(it.message ?: "Search failed") }
                )
            }
        }
    }
}
