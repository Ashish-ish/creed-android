package com.creed.data.payment

import kotlinx.coroutines.delay

class PaymentDataSourceIos : PaymentDataSource {
    override suspend fun createOrder(amountCents: Int): Result<String> {
        delay(500)
        return Result.success("ORDER123456")
    }
    override suspend fun confirmPayment(orderId: String): Result<Boolean> {
        delay(500)
        return Result.success(true)
    }
}

