package com.stuartsoft.rotorai.modules

import com.stuartsoft.rotorai.monitoring.LoggingOnlyCrashReporter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CrashReporterModule {
    @Singleton
    @Provides
    fun crashReporter() = LoggingOnlyCrashReporter()
}
