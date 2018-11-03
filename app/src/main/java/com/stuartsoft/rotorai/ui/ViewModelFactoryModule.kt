package com.stuartsoft.rotorai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stuartsoft.rotorai.ui.welcome.WelcomeViewModel
// GENERATOR - MORE IMPORTS //
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelFactoryModule: VariantViewModelFactoryModule() {
    @Binds internal abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    abstract fun bindsWelcomeViewModel(welcomeViewModel: WelcomeViewModel): ViewModel

    // GENERATOR - MORE VIEW MODELS //
}