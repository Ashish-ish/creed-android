package com.creed.android.vm

import com.creed.domain.models.Slot
import com.creed.usecase.FetchSlotsUseCase
import kotlinx.coroutines.launch

sealed class SlotPickerState {
    object Idle : SlotPickerState()
    object Loading : SlotPickerState()
    data class Success(val slots: List<Slot>) : SlotPickerState()
    data class Error(val message: String) : SlotPickerState()
}

sealed class SlotPickerEffect {
    data class ShowSnackBar(val message: String) : SlotPickerEffect()
}

class SlotPickerViewModel(
    private val fetchSlots: FetchSlotsUseCase
) : BaseViewModel<SlotPickerState, SlotPickerEffect>(SlotPickerState.Idle) {
    fun fetchSlots(arenaId: String, sportId: String, courtId: String, date: String) {
        setState { SlotPickerState.Loading }
        viewModelScope.launch {
            val result = fetchSlots.invoke(arenaId, sportId, courtId, date)
            setState {
                if (result.isSuccess) SlotPickerState.Success(result.getOrThrow())
                else SlotPickerState.Error("Failed to load slots")
            }
        }
    }
}
