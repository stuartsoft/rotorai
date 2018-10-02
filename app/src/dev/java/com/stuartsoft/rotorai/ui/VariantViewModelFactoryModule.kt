package com.stuartsoft.rotorai.ui

import android.arch.lifecycle.ViewModel
import com.stuartsoft.rotorai.ui.devsettings.DevSettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class VariantViewModelFactoryModule {
    @Binds
    @IntoMap
    @ViewModelKey(DevSettingsViewModel::class)
    abstract fun bindsDevSettingsViewModel(devSettingsViewModel: DevSettingsViewModel): ViewModel
}