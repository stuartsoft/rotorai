package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import android.os.Parcelable
import androidx.databinding.Bindable
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.VehicleConnectionState.*
import com.stuartsoft.rotorai.ui.BaseViewModel
import com.stuartsoft.rotorai.ui.SingleLiveEvent
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val btvc: BTVehicleConnector)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()

    @Bindable
    fun getHeaderMsg(): String? {
        return app.getString(when (btvc.currentConnectionState()) {
            UNAVAILABLE -> R.string.ui_welcome_doesnt_have_bt
            OFFLINE -> R.string.ui_welcome_header_enable_bt
            else -> R.string.ui_welcome_header_select_vehicle
        })
    }

    @Bindable
    fun getWelcomeScreenStep(): WelcomeScreenStep {
        return if(btvc.currentConnectionState() == READY_VEHICLE_CONNECTED){
            WelcomeScreenStep.CONNECTED
        } else {
            WelcomeScreenStep.SELECT_VEHICLE
        }
    }

    @Bindable
    fun isEnableBTLinkVisible(): Boolean {
        return btvc.currentConnectionState() == OFFLINE
    }

    override fun setupViewModel() {
    }

    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                if (extraz.containsKey(EXTRA_STATE)) {
                    notifyChange()
                    if (extraz.getInt(EXTRA_STATE) == STATE_ON) {
                        btvc.startDiscovery()
                    }
                }
                if (extraz.containsKey(EXTRA_DEVICE)) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(EXTRA_DEVICE)
                    btvc.inspectNewDevice(device)
                }
            }
        }
    }

    fun onClickNeedsBT() {
        shouldShowBTDialog.value = true
    }

    enum class WelcomeScreenStep(val i: Int) {
        SELECT_VEHICLE(0),
        CONNECTED(1)
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
