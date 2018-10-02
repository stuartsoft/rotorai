package com.stuartsoft.rotorai.app


import com.stuartsoft.rotorai.ui.devsettings.DevSettingsActivity

interface VariantApplicationComponent {
    fun inject(activity: DevSettingsActivity)
}
