package com.stuartsoft.rotorai.app

import com.stuartsoft.rotorai.data.DataModule
import com.stuartsoft.rotorai.modules.CrashReporterModule
import com.stuartsoft.rotorai.monitoring.LoggerModule
import com.stuartsoft.rotorai.ui.ViewModelFactoryModule
import com.stuartsoft.rotorai.ui.main.MainActivity
import com.stuartsoft.rotorai.ui.welcome.WelcomeActivity
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

    fun inject(activity: MainActivity)
    fun inject(activity: WelcomeActivity)
    // GENERATOR - MORE ACTIVITIES //
}