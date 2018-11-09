package ai.rotor.mobile.data

import android.bluetooth.BluetoothDevice
import ai.rotor.mobile.data.VehicleConnectionState.*
import ai.rotor.mobile.ui.welcome.WelcomeViewModel.Companion.simulatorDevice
import javax.inject.Inject

class BTVehicleConnector @Inject constructor(private val rotorBTAdapterWrapper: RotorBTAdapterWrapper ): VehicleConnector() {

    override fun currentConnectionState(): VehicleConnectionState {

        if (!rotorBTAdapterWrapper.isBluetoothRadioAvailable()) {
            return UNAVAILABLE
        }

        if (!rotorBTAdapterWrapper.isBluetoothRadioOn()) {
            return OFFLINE
        }

        val numberOfConnectedVehicles = rotorBTAdapterWrapper.getBondedDeviceNamesAndAddress().fold(0)
        { acc, device -> if (device.name.contains(RotorUtils.DEFAULT_VEHICLE_NAME)) acc+1 else acc }

        return when(numberOfConnectedVehicles) {
                    1 ->    READY_VEHICLE_CONNECTED
                    0 ->    VEHICLE_NOT_CONNECTED
                    else -> TOO_MANY_VEHICLES_CONNECTED
                }

    }

    fun startDiscovery() {
        rotorBTAdapterWrapper.startDiscovery()
    }

    fun stopDiscovery() {
        rotorBTAdapterWrapper.stopDiscovery()
    }

    fun isInDiscoveryMode(): Boolean {
        return rotorBTAdapterWrapper.isInDiscoveryMode()
    }

    fun connectTo(genericBTDevice: GenericBTDevice, callback: (didSucceed: Boolean)-> Unit) {
        if(genericBTDevice.name.contains(Regex("(RTR.\\d)"))) {
            callback(true)
        }
        else if (simulatorDevice.equals(genericBTDevice)){
            callback(true)
        }
        else {
            callback(false)
        }
    }

}