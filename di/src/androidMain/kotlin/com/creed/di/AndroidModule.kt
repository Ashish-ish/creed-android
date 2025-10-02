package com.creed.di

import android.content.Context
import com.creed.data.local.AndroidKeyValueStorage
import com.creed.data.local.KeyValueStorage
import com.creed.data.otp.OtpDataSource
import com.creed.data.otp.OtpDataSourceAndroid
import com.creed.data.payment.PaymentDataSource
import com.creed.data.payment.PaymentDataSourceAndroid
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    single<KeyValueStorage> { AndroidKeyValueStorage(context) }
    single<OtpDataSource> { OtpDataSourceAndroid() }
    single<PaymentDataSource> { PaymentDataSourceAndroid() }
}
