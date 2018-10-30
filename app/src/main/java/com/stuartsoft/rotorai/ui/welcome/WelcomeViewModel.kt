package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.core.content.ContextCompat
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
            READY_VEHICLE_CONNECTED -> R.string.UI_WELCOME_CONNECTED
            OFFLINE -> R.string.UI_WELCOME_ENABLE_BT_RADIO
            VEHICLE_NOT_CONNECTED -> if (!isLocationPermissionEnabled()) R.string.UI_WELCOME_ENABLE_LOCATION_PERMISSION else R.string.UI_WELCOME_SELECT_VEHICLE
            else -> R.string.UI_WELCOME_BT_UNAVAILABLE
        })
    }

    @Bindable
    fun getWelcomeScreenStep(): WelcomeScreenStep {
        return when(btvc.currentConnectionState()){
            UNAVAILABLE -> WelcomeScreenStep.BT_UNAVAILABLE
            OFFLINE -> WelcomeScreenStep.ENABLE_BT_RADIO
            VEHICLE_NOT_CONNECTED -> if (!isLocationPermissionEnabled()) WelcomeScreenStep.ENABLE_LOCATION else WelcomeScreenStep.SELECT_VEHICLE
            else -> WelcomeScreenStep.CONNECTED
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

    fun isLocationPermissionEnabled() = ContextCompat.checkSelfPermission(app.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    enum class WelcomeScreenStep {
        BT_UNAVAILABLE,
        ENABLE_LOCATION,
        ENABLE_BT_RADIO,
        SELECT_VEHICLE,
        CONNECTED
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
