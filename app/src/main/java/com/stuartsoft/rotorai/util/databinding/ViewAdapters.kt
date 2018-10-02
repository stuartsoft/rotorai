package com.stuartsoft.rotorai.util.databinding

import android.databinding.BindingAdapter
import android.view.View

object ViewAdapters {
    @JvmStatic
    @BindingAdapter("visible")
    fun setVisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }
}
