package com.stuartsoft.rotorai

import com.stuartsoft.rotorai.TestUtils.getAppUnderTest
import com.stuartsoft.rotorai.app.AndroidModule
import com.stuartsoft.rotorai.app.AppModule
import com.stuartsoft.rotorai.app.ApplicationComponent
import com.stuartsoft.rotorai.app.VariantModule
import com.stuartsoft.rotorai.data.DataModule
import com.stuartsoft.rotorai.modules.CrashReporterModule
import com.stuartsoft.rotorai.monitoring.LoggerModule
import it.cosenonjaviste.daggermock.DaggerMockRule

class MainApplicationDaggerMockRule : DaggerMockRule<ApplicationComponent>(
        ApplicationComponent::class.java,
        VariantModule(),
        AndroidModule(getAppUnderTest()),
        AppModule(0),
        LoggerModule(),
        CrashReporterModule(),
        DataModule()) {
    init {
        set { component -> getAppUnderTest().component = component }
    }
}
