package com.stuartsoft.rotorai

import com.stuartsoft.rotorai.app.MainApplication

class TestMainApplication : MainApplication() {

    override fun initializeApplication() {
        // Don't initialize the application
    }

    override fun isTesting() = true
}
