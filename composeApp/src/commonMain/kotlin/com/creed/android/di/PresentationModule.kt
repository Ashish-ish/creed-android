package com.creed.android.di

import com.creed.android.vm.HomeViewModel
import com.creed.android.vm.LoginViewModel
import com.creed.android.vm.SplashViewModel
import com.creed.android.vm.ProfileCompletionViewModel
import com.creed.android.vm.LocationPermissionViewModel
import com.creed.android.vm.ArenaDetailViewModel
import com.creed.android.vm.SlotPickerViewModel
import com.creed.android.vm.OrderConfirmationViewModel
import com.creed.android.vm.BookingsViewModel
import com.creed.android.vm.ProfileViewModel
import com.creed.android.vm.SearchViewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModels as regular factory instances (not Android ViewModels)
    factory { LoginViewModel(get()) }
    factory { SplashViewModel(get()) }
    factory { ProfileCompletionViewModel(get()) }
    factory { LocationPermissionViewModel(get()) }
    factory { ArenaDetailViewModel(get(), get()) }
    factory { SlotPickerViewModel(get()) }
    factory { OrderConfirmationViewModel(get(), get(), get()) }
    factory { BookingsViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { SearchViewModel(get()) }
    factory { HomeViewModel(get(), get()) }
}
