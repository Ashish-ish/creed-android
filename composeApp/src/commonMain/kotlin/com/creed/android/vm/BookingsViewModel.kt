package com.creed.android.vm

import com.creed.domain.models.Booking
import com.creed.usecase.FetchBookingsUseCase
import kotlinx.coroutines.launch

sealed class BookingsState {
    object Idle : BookingsState()
    object Loading : BookingsState()
    data class Success(val bookings: List<Booking>) : BookingsState()
    data class Error(val message: String) : BookingsState()
}

sealed class BookingsEffect {
    data class ShowSnackBar(val message: String) : BookingsEffect()
}

class BookingsViewModel(
    private val fetchBookings: FetchBookingsUseCase
) : BaseViewModel<BookingsState, BookingsEffect>(BookingsState.Idle) {
    fun loadBookings(upcoming: Boolean) {
        setState { BookingsState.Loading }
        viewModelScope.launch {
            val result = fetchBookings(upcoming)
            setState {
                if (result.isSuccess) BookingsState.Success(result.getOrThrow())
                else BookingsState.Error("Failed to load bookings")
            }
        }
    }
}
