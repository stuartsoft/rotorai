package ai.rotor.mobile.data

import ai.rotor.commonstuff.GenericBTDevice
import android.bluetooth.BluetoothAdapter
import javax.inject.Inject

class RotorBTAdapterWrapper @Inject constructor(private val btAdapter: BluetoothAdapter?) {
//This is effectively just a wrapper of BluetoothAdapter because
// mocking it directly doesn't work well with mockk

    fun getBondedDeviceNamesAndAddress(): List<GenericBTDevice> =
        btAdapter?.let {
            it.bondedDevices.map { device -> GenericBTDevice(device) }
        } ?: listOf()


    fun isBluetoothRadioAvailable(): Boolean {
        return btAdapter != null
    }

    fun isBluetoothRadioOn(): Boolean {
        return btAdapter?.isEnabled ?: false
    }

    fun startDiscovery() {
        btAdapter?.startDiscovery()
    }

    fun stopDiscovery() {
        btAdapter?.cancelDiscovery()
    }

    fun isInDiscoveryMode(): Boolean {
        return btAdapter?.isDiscovering ?: false
    }

}