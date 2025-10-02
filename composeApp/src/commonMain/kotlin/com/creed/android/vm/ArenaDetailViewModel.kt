package com.creed.android.vm

import com.creed.domain.models.Arena
import com.creed.domain.models.Sport
import com.creed.usecase.FetchArenaDetailUseCase
import com.creed.usecase.FetchSportsUseCase
import kotlinx.coroutines.launch

sealed class ArenaDetailState {
    object Idle : ArenaDetailState()
    object Loading : ArenaDetailState()
    data class Success(val arena: Arena, val sports: List<Sport>) : ArenaDetailState()
    data class Error(val message: String) : ArenaDetailState()
}

sealed class ArenaDetailEffect {
    data class ShowSnackBar(val message: String) : ArenaDetailEffect()
}

class ArenaDetailViewModel(
    private val fetchArenaDetail: FetchArenaDetailUseCase,
    private val fetchSports: FetchSportsUseCase
) : BaseViewModel<ArenaDetailState, ArenaDetailEffect>(ArenaDetailState.Idle) {
    fun load(arenaId: String) {
        setState { ArenaDetailState.Loading }
        viewModelScope.launch {
            val arenaResult = fetchArenaDetail(arenaId)
            val sportsResult = fetchSports(arenaId)
            setState {
                if (arenaResult.isSuccess && sportsResult.isSuccess)
                    ArenaDetailState.Success(arenaResult.getOrThrow(), sportsResult.getOrThrow())
                else ArenaDetailState.Error("Failed to load arena details")
            }
        }
    }
}
