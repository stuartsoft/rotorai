package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.*
import android.content.Context
import android.content.Intent
import android.databinding.Bindable
import android.os.Parcelable
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.ui.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    var bluetoothRadioIsOn : Boolean = false

    @Parcelize
    class State() : Parcelable

    override fun setupViewModel() {

    }

    @Bindable
    fun isNeedsBluetoothRadio() = !bluetoothRadioIsOn
    
    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                bluetoothRadioIsOn = extraz.getInt(EXTRA_STATE) == STATE_ON
                notifyPropertyChanged(BR.needsBluetoothRadio)
            }
        }
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
