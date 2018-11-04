package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
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
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val btvc: BTVehicleConnector)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()
    var shouldAskForLocationDialog = SingleLiveEvent<Boolean>()

    @Inject
    lateinit var btDiscoveredDevices: MutableList<GenericBTDevice>

    @Bindable("getWelcomeScreenStep")
    fun getDiscoveredDevices() = btDiscoveredDevices

    @Bindable("getWelcomeScreenStep")
    fun isSearching() = btvc.isInDiscoveryMode()

    @Bindable("getWelcomeScreenStep")
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
            beginSearchingForDevices()
        }
    }

    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                if (extraz.containsKey(EXTRA_STATE)) {
                    if (extraz.getInt(EXTRA_STATE) == STATE_ON) {
                        beginSearchingForDevices()
                    }
                    else {
                        notifyPropertyChanged(BR.welcomeScreenStep)
                    }
                }
                if (extraz.containsKey(EXTRA_DEVICE)) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(EXTRA_DEVICE)
                    val genericBTDevice = GenericBTDevice(device)
                    if (genericBTDevice.name != ""){
                        btDiscoveredDevices.add(GenericBTDevice(device))
                        notifyChange()
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

    fun onRequestPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        notifyPropertyChanged(BR.welcomeScreenStep)
        for (i in 0..permissions.size-1) {
            if (permissions[i].equals(android.Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[i] == 0) {
                beginSearchingForDevices()
            }
        }
    }

    //----- HELPERS BELOW THIS LINE -----

    @VisibleForTesting
    fun beginSearchingForDevices() {
        if (btvc.currentConnectionState() == VEHICLE_NOT_CONNECTED) {
            btDiscoveredDevices = mutableListOf()
            btvc.startDiscovery()
            notifyPropertyChanged(BR.welcomeScreenStep)
        }
    }

    enum class WelcomeScreenStep(val i: Int) {
        BT_UNAVAILABLE(0),
        ENABLE_LOCATION(1),
        ENABLE_BT_RADIO(2),
        SELECT_VEHICLE(3),
        CONNECTED(4)
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS

        val REQUEST_TURN_BT_ON = 1
        val REQUEST_ENABLE_LOCATION_PERMISSION = 2
    }
}
