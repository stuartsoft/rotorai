package ai.rotor.mobile.app

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

import ai.rotor.mobile.ui.RiseAndShine

import timber.log.Timber.Tree

/**
 * Specific to the debug variant.
 */
class MainApplicationInitializer(
        application: Application,
        logger: Tree)
    : BaseApplicationInitializer(application, logger) {

    override fun initialize() {
        super.initialize()
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                RiseAndShine.riseAndShine(activity)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }
}
