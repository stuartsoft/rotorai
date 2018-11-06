package ai.rotor.mobile.data

import android.bluetooth.BluetoothDevice

//basically another wrapper because mocking bluetooth stuff in Android sux
data class GenericBTDevice(val name: String, val address: String) {
    constructor(device: BluetoothDevice) : this(device.name ?: "", device.address ?: "") {}
}