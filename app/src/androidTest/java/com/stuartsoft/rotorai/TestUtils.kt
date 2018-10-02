package com.stuartsoft.rotorai

import android.support.test.InstrumentationRegistry
import com.stuartsoft.rotorai.app.MainApplication

object TestUtils {
    fun getAppUnderTest(): MainApplication {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        return targetContext.applicationContext as MainApplication
    }
}
