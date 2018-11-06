package ai.rotor.mobile.modules

import ai.rotor.mobile.monitoring.LoggingOnlyCrashReporter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CrashReporterModule {
    @Singleton
    @Provides
    fun crashReporter() = LoggingOnlyCrashReporter()
}
