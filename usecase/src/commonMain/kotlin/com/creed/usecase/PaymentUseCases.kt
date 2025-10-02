package com.creed.usecase

import com.creed.data.payment.PaymentDataSource

class CreatePaymentOrderUseCase(private val paymentDataSource: PaymentDataSource) {
    suspend operator fun invoke(amountCents: Int) = paymentDataSource.createOrder(amountCents)
}

class ConfirmPaymentUseCase(private val paymentDataSource: PaymentDataSource) {
    suspend operator fun invoke(orderId: String) = paymentDataSource.confirmPayment(orderId)
}

