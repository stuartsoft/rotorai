package ai.rotor.mobile.app

import ai.rotor.mobile.data.DataModule
import ai.rotor.mobile.modules.CrashReporterModule
import ai.rotor.mobile.monitoring.LoggerModule
import ai.rotor.mobile.ui.ViewModelFactoryModule
import ai.rotor.mobile.ui.welcome.WelcomeActivity
// GENERATOR - MORE IMPORTS //
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
        VariantModule::class,
        AndroidModule::class,
        AppModule::class,
        LoggerModule::class,
        CrashReporterModule::class,
        DataModule::class,
        ViewModelFactoryModule::class])
interface ApplicationComponent : VariantApplicationComponent {
    fun inject(application: MainApplication)

    fun inject(activity: WelcomeActivity)
    // GENERATOR - MORE ACTIVITIES //
}