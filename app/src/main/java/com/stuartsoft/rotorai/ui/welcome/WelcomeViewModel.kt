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
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.GenericBTDevice
import com.stuartsoft.rotorai.data.VehicleConnectionState.*
import com.stuartsoft.rotorai.ui.BaseViewModel
import com.stuartsoft.rotorai.ui.SingleLiveEvent
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val btvc: BTVehicleConnector)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()
    var shouldAskForLocationDialog = SingleLiveEvent<Boolean>()

    private val discoveredDevices = mutableListOf<GenericBTDevice>()

    @Bindable
    fun getDiscoveredDevices() = discoveredDevices

    @Bindable("getHeaderMsg")
    fun isSearching() = btvc.isInDiscoveryMode()

    @Bindable
    fun getHeaderMsg(): String? {
        return app.getString(when(getWelcomeScreenStep()){
            WelcomeScreenStep.BT_UNAVAILABLE -> R.string.UI_WELCOME_BT_UNAVAILABLE
            WelcomeScreenStep.ENABLE_LOCATION -> R.string.UI_WELCOME_ENABLE_LOCATION_PERMISSION
            WelcomeScreenStep.ENABLE_BT_RADIO -> R.string.UI_WELCOME_ENABLE_BT_RADIO
            WelcomeScreenStep.SELECT_VEHICLE -> R.string.UI_WELCOME_SELECT_VEHICLE
            WelcomeScreenStep.CONNECTED -> R.string.UI_WELCOME_CONNECTED
        })
    }

    @Bindable
    fun getWelcomeScreenStep(): WelcomeScreenStep {
        return when(btvc.currentConnectionState()) {
            UNAVAILABLE -> WelcomeScreenStep.BT_UNAVAILABLE
            OFFLINE -> if (!isLocationPermissionEnabled()) WelcomeScreenStep.ENABLE_LOCATION else WelcomeScreenStep.ENABLE_BT_RADIO
            VEHICLE_NOT_CONNECTED -> if (!isLocationPermissionEnabled()) WelcomeScreenStep.ENABLE_LOCATION else WelcomeScreenStep.SELECT_VEHICLE
            else -> WelcomeScreenStep.CONNECTED
        }
    }

    override fun setupViewModel() {
        if (isLocationPermissionEnabled() && btvc.currentConnectionState() == VEHICLE_NOT_CONNECTED){
            btvc.startDiscovery()
        }
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
                    val genericBTDevice = GenericBTDevice(device)
                    if (genericBTDevice.name != ""){
                        discoveredDevices.add(GenericBTDevice(device))
                        notifyPropertyChanged(BR.discoveredDevices)
                        Timber.d("STULOG NEW DEVICE")
                    }
                }
            }
        }
    }

    fun onClickNeedsBT() {
        shouldShowBTDialog.value = true
    }

    fun onClickNeedsLocation() {
        shouldAskForLocationDialog.value = true
    }

    fun isLocationPermissionEnabled() = ContextCompat.checkSelfPermission(app.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    enum class WelcomeScreenStep(val i: Int) {
        BT_UNAVAILABLE(0),
        ENABLE_LOCATION(1),
        ENABLE_BT_RADIO(2),
        SELECT_VEHICLE(3),
        CONNECTED(4)
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
