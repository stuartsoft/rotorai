package ai.rotor.mobile

import ai.rotor.mobile.TestUtils.getAppUnderTest
import ai.rotor.mobile.app.AndroidModule
import ai.rotor.mobile.app.AppModule
import ai.rotor.mobile.app.ApplicationComponent
import ai.rotor.mobile.app.VariantModule
import ai.rotor.mobile.data.DataModule
import ai.rotor.mobile.modules.CrashReporterModule
import ai.rotor.mobile.monitoring.LoggerModule
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
