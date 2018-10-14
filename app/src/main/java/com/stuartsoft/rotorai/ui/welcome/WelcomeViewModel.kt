package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.content.Context
import android.content.Intent
import android.databinding.Bindable
import android.os.Parcelable
import android.util.Log
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.ui.BaseViewModel
import com.stuartsoft.rotorai.ui.SingleLiveEvent
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val bluetoothAdapter: BluetoothAdapter)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()

    override fun setupViewModel() {

    }

    @Bindable
    fun isNeedsBluetoothRadio() = !bluetoothAdapter.isEnabled

    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                notifyPropertyChanged(BR.needsBluetoothRadio)
            }
        }
    }

    fun onClickNeedsBT() {
        shouldShowBTDialog.value = true
    }

    fun forceRefreshAllBindings() {
        notifyChange()
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
