package com.creed.data.payment

interface PaymentDataSource {
    suspend fun createOrder(amountCents: Int): Result<String> // returns orderId
    suspend fun confirmPayment(orderId: String): Result<Boolean>
}

