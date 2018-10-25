package com.stuartsoft.rotorai.data

import android.bluetooth.BluetoothAdapter
import javax.inject.Inject

class RotorBTAdapterWrapper @Inject constructor(private val btAdapter: BluetoothAdapter?): BTVehicleConnector.RotorBTDelegate {
//This is effectively just a wrapper of BluetoothAdapter because
// mocking it directly doesn't work well with mockk

    override fun getBondedDeviceNamesAndAddress(): List<GenericBTDevice> =
        btAdapter?.let {
            it.bondedDevices.map { device -> GenericBTDevice(device.name, device.address) }
        } ?: listOf()


    override fun isBluetoothRadioAvailable(): Boolean {
        return btAdapter != null
    }

    override fun isBluetoothRadioOn(): Boolean {
        return btAdapter?.isEnabled ?: false
    }
}