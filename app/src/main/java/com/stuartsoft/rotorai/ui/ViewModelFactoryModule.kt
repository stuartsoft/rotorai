package com.stuartsoft.rotorai.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.stuartsoft.rotorai.ui.main.MainViewModel
// GENERATOR - MORE IMPORTS //
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelFactoryModule: VariantViewModelFactoryModule() {
    @Binds internal abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindsMainViewModel(mainViewModel: MainViewModel): ViewModel

    // GENERATOR - MORE VIEW MODELS //
}