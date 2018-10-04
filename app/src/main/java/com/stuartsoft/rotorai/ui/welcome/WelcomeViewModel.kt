package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.databinding.Bindable
import android.os.Parcelable
import com.stuartsoft.rotorai.ui.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(
        private val app: Application)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    var bluetoothRadioIsOn = false

    @Parcelize
    class State() : Parcelable

    override fun setupViewModel() {

    }

    fun isNeedsBluetoothRadio() = !bluetoothRadioIsOn

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
