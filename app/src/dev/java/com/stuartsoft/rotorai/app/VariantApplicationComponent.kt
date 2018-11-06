package ai.rotor.mobile.app


import ai.rotor.mobile.ui.devsettings.DevSettingsActivity

interface VariantApplicationComponent {
    fun inject(activity: DevSettingsActivity)
}
