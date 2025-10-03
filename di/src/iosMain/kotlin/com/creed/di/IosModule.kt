package com.creed.di

import com.creed.data.auth.FirebaseAuthDataSource
import com.creed.data.auth.FirebaseAuthDataSourceIos
import org.koin.core.module.Module
import org.koin.dsl.module

val iosModule: Module = module {
    single<FirebaseAuthDataSource> { FirebaseAuthDataSourceIos() }
}
