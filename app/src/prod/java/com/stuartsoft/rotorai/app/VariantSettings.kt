package ai.rotor.mobile.app

import android.content.Context
import ai.rotor.mobile.R

open class VariantSettings(private val context: Context) {
    val baseUrl: String = context.getString(R.string.default_base_url)
}
