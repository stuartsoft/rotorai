package ai.rotor.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ai.rotor.mobile.ui.welcome.WelcomeViewModel
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