package ai.rotor.mobile.modules

import ai.rotor.mobile.monitoring.CrashlyticsCrashReporter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CrashReporterModule {
    @Singleton
    @Provides
    fun provideCrashReporter() = CrashlyticsCrashReporter()
}
