package com.creed.di

import com.creed.data.local.KeyValueStorage
import com.creed.data.local.IosKeyValueStorage
import com.creed.data.otp.OtpDataSource
import com.creed.data.otp.OtpDataSourceIos
import com.creed.data.payment.PaymentDataSource
import com.creed.data.payment.PaymentDataSourceIos
import org.koin.dsl.module

val iosModule = module {
    single<KeyValueStorage> { IosKeyValueStorage() }
    single<OtpDataSource> { OtpDataSourceIos() }
    single<PaymentDataSource> { PaymentDataSourceIos() }
}
