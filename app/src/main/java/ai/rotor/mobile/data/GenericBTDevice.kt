package ai.rotor.mobile.data

import android.bluetooth.BluetoothDevice
import java.util.*

//basically another wrapper because mocking bluetooth stuff in Android sux
data class GenericBTDevice(val name: String, val address: String, val uuid: UUID?) {
    constructor(device: BluetoothDevice) : this(
            device.name ?: "",
            device.address ?: "",
            if (device.uuids != null && device.uuids.isNotEmpty()) device.uuids.first().uuid else null)


    companion object {
        val SIMULATOR_NAME = "4x4 Simulator"
        val SIMULATOR_MAC = "10:20:30:40:50:60"
        val SIMULATOR_UUID_STRING = "4a14c657-e073-4432-a633-487233362fb2"
        val SIMULATOR_UUID = UUID.fromString(SIMULATOR_UUID_STRING)
    }
}