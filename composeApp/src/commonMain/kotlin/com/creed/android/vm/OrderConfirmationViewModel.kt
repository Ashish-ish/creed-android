package com.creed.android.vm

import com.creed.domain.models.Booking
import com.creed.domain.models.BookingRequest
import com.creed.usecase.CreateBookingUseCase
import com.creed.usecase.CreatePaymentOrderUseCase
import com.creed.usecase.ConfirmPaymentUseCase
import kotlinx.coroutines.launch

sealed class OrderConfirmationState {
    object Idle : OrderConfirmationState()
    object Loading : OrderConfirmationState()
    data class Success(val booking: Booking) : OrderConfirmationState()
    data class Error(val message: String) : OrderConfirmationState()
}

sealed class OrderConfirmationEffect {
    data class ShowSnackBar(val message: String) : OrderConfirmationEffect()
    object NavigateToBookings : OrderConfirmationEffect()
}

class OrderConfirmationViewModel(
    private val createBooking: CreateBookingUseCase,
    private val createPaymentOrder: CreatePaymentOrderUseCase,
    private val confirmPayment: ConfirmPaymentUseCase
) : BaseViewModel<OrderConfirmationState, OrderConfirmationEffect>(OrderConfirmationState.Idle) {
    fun confirmBooking(request: BookingRequest) {
        setState { OrderConfirmationState.Loading }
        viewModelScope.launch {
            val result = createBooking(request)
            setState {
                if (result.isSuccess) OrderConfirmationState.Success(result.getOrThrow())
                else OrderConfirmationState.Error("Booking failed")
            }
            if (result.isSuccess) sendEffect(OrderConfirmationEffect.NavigateToBookings)
        }
    }

    fun startPayment(amountCents: Int) {
        setState { OrderConfirmationState.Loading }
        viewModelScope.launch {
            val orderResult = createPaymentOrder(amountCents)
            if (orderResult.isSuccess) {
                val orderId = orderResult.getOrThrow()
                val confirmResult = confirmPayment(orderId)
                setState {
                    if (confirmResult.isSuccess && confirmResult.getOrThrow()) OrderConfirmationState.Success(/* Copilot: fill with booking */Booking("", /* ... */ null!!, null!!, null!!, "", "", "", emptyList()))
                    else OrderConfirmationState.Error("Payment failed")
                }
            } else {
                setState { OrderConfirmationState.Error("Order creation failed") }
            }
        }
    }
}
