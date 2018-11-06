package ai.rotor.mobile.monitoring.model

import timber.log.Timber

class NoOpTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // No op
    }
}
