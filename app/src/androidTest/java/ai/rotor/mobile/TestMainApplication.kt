package ai.rotor.mobile

import ai.rotor.mobile.app.MainApplication

class TestMainApplication : MainApplication() {

    override fun initializeApplication() {
        // Don't initialize the application
    }

    override fun isTesting() = true
}
