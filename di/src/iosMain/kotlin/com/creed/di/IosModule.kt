package com.creed.di

import com.creed.data.location.LocationService
import org.koin.dsl.module
import com.creed.data.auth.FirebaseAuthDataSource
import com.creed.data.auth.FirebaseAuthDataSourceIos
import org.koin.core.module.Module

val iosModule: Module = module {
    single<FirebaseAuthDataSource> { FirebaseAuthDataSourceIos() }
    single<LocationService> { LocationService() }
}
