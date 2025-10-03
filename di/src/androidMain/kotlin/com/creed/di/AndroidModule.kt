package com.creed.di

import com.creed.data.auth.FirebaseAuthDataSource
import com.creed.data.auth.FirebaseAuthDataSourceAndroid
import com.creed.data.auth.ActivityManager
import com.creed.data.local.AndroidKeyValueStorage
import com.creed.data.local.KeyValueStorage
import org.koin.core.module.Module
import org.koin.dsl.module

val androidModule: Module = module {
    // Provide platform-specific KeyValueStorage implementation
    single<KeyValueStorage> { AndroidKeyValueStorage(get()) }

    // Provide Firebase Auth DataSource
    single<FirebaseAuthDataSource> {
        FirebaseAuthDataSourceAndroid(get())
    }

    // Provide ActivityManager interface for MainActivity to set current activity
//    single<ActivityManager> {
//        get<FirebaseAuthDataSource>() as FirebaseAuthDataSourceAndroid
//    }
}
