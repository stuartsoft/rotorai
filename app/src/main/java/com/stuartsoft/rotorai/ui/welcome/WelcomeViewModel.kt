package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.databinding.Bindable
import android.os.Parcelable
import android.support.annotation.VisibleForTesting
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.data.RotorUtils
import com.stuartsoft.rotorai.ui.BaseViewModel
import com.stuartsoft.rotorai.ui.SingleLiveEvent
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val bluetoothAdapter: BluetoothAdapter)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()

    @Bindable
    lateinit var welcomeScreenStep: WelcomeScreenStep

    override fun setupViewModel() {

    }

    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                if (extraz.containsKey(EXTRA_STATE)) {
                }
            }
        }
    }

    fun onClickNeedsBT() {
        shouldShowBTDialog.value = true
    }

    fun forceRefreshAllBindings() {
        notifyChange()
    }

    enum class WelcomeScreenStep(val i: Int) {
        SELECT_VEHICLE(2),
        CONNECTED(3)
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
