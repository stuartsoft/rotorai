package com.stuartsoft.rotorai.app

import android.content.Context
import com.stuartsoft.rotorai.R

open class VariantSettings(private val context: Context) {
    val baseUrl: String = context.getString(R.string.default_base_url)
}
