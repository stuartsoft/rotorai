package ai.rotor.mobile.ui.welcome

import ai.rotor.commonstuff.GenericBTDevice
import ai.rotor.commonstuff.GenericBTDevice.Companion.SIMULATOR_MAC
import ai.rotor.commonstuff.GenericBTDevice.Companion.SIMULATOR_NAME
import ai.rotor.commonstuff.GenericBTDevice.Companion.SIMULATOR_UUID
import android.app.Application
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import ai.rotor.mobile.BR
import ai.rotor.mobile.R
import ai.rotor.mobile.data.BTVehicleConnector
import ai.rotor.mobile.data.VehicleConnectionState.*
import ai.rotor.mobile.ui.BaseViewModel
import ai.rotor.mobile.ui.SingleLiveEvent
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val btvc: BTVehicleConnector,
        initialListOfItems: MutableList<GenericBTDevice> = mutableListOf())
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()
    var shouldAskForLocationDialog = SingleLiveEvent<Boolean>()

    private var btDiscoveredDevices: MutableList<GenericBTDevice> = initialListOfItems

    var shouldShowSimulatorInList: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.discoveredDevices)
        }

    @Bindable("getWelcomeScreenStep")
    fun getDiscoveredDevices(): MutableList<GenericBTDevice> {
        val list : MutableList<GenericBTDevice> = mutableListOf()
        if (shouldShowSimulatorInList) {
            list.add(simulatorDevice)
        }
        list.addAll(btDiscoveredDevices)
        return list
    }


    @Bindable("getWelcomeScreenStep")
    fun isSearching() = btvc.currentConnectionState() == VEHICLE_NOT_CONNECTED

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
                        if (!btDiscoveredDevices.contains(genericBTDevice)) {
                            btDiscoveredDevices.add(GenericBTDevice(device))
                            notifyPropertyChanged(BR.discoveredDevices)
                        }
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

    fun onClickConnectToSimulator() {
        btDeviceClicked(simulatorDevice)
    }

    fun btDeviceClicked(item: GenericBTDevice) {
        Timber.d("STUDEBUG - BT Device clicked " + item.name)
        Toast.makeText(app.applicationContext, "Connecting to " + item.name, Toast.LENGTH_SHORT).show()
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
            btvc.stopDiscovery()
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

        val simulatorDevice = GenericBTDevice(SIMULATOR_NAME, SIMULATOR_MAC, SIMULATOR_UUID)
    }
}
