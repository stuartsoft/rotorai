package com.stuartsoft.rotorai.data

import android.bluetooth.BluetoothDevice
import com.stuartsoft.rotorai.data.VehicleConnectionState.*
import javax.inject.Inject

class BTVehicleConnector @Inject constructor(private val rotorBTDelegate: RotorBTDelegate ): VehicleConnector() {

    interface RotorBTDelegate {
        fun getBondedDeviceNamesAndAddress() : List<GenericBTDevice>

        fun isBluetoothRadioAvailable() : Boolean

        fun isBluetoothRadioOn() : Boolean
    }

    override fun currentConnectionState(): VehicleConnectionState {

        if (!rotorBTDelegate.isBluetoothRadioAvailable()) {
            return UNAVAILABLE
        }

        if (!rotorBTDelegate.isBluetoothRadioOn()) {
            return OFFLINE
        }

        val numberOfConnectedVehicles = rotorBTDelegate.getBondedDeviceNamesAndAddress().fold(0)
        { acc, device -> if (device.name.contains(RotorUtils.DEFAULT_VEHICLE_NAME)) acc+1 else acc }

        return when(numberOfConnectedVehicles) {
                    1 ->    READY_VEHICLE_CONNECTED
                    0 ->    VEHICLE_NOT_CONNECTED
                    else -> TOO_MANY_VEHICLES_CONNECTED
                }
    }

}