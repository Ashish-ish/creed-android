package com.creed.android.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creed.data.local.KeyValueStorage
import com.creed.data.local.StorageKeys
import com.creed.data.local.getObject
import com.creed.data.location.LocationService
import com.creed.domain.models.Arena
import com.creed.domain.models.LatLng
import com.creed.usecase.FetchArenaDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArenaDetailState(
    val arena: Arena? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ArenaDetailViewModel(
    private val fetchArenaDetailUseCase: FetchArenaDetailUseCase,
    private val locationService: LocationService,
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ArenaDetailState())
    val state: StateFlow<ArenaDetailState> = _state.asStateFlow()

    private val defaultArenaId = "652000000000000000000010"

    fun loadArenaDetail(arenaId: String?) {
        val actualArenaId = defaultArenaId /*arenaId?.takeIf { it.isNotBlank() } ?: defaultArenaId*/

        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Get current location for the API call
                val currentLocation = keyValueStorage.getObject<LatLng>(StorageKeys.LOCATION)

                val result = fetchArenaDetailUseCase.execute(
                    arenaId = actualArenaId,
                    lat = currentLocation?.latitude ?: 19.0760, // Default to Mumbai coordinates
                    lng = currentLocation?.longitude ?: 72.8777
                )

                result.fold(
                    onSuccess = { arena ->
                        _state.value = _state.value.copy(
                            arena = arena,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load arena details"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun retry(arenaId: String?) {
        loadArenaDetail(arenaId)
    }
}
