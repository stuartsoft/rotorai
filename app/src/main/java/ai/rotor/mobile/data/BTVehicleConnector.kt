package ai.rotor.mobile.data

import ai.rotor.commonstuff.GenericBTDevice
import ai.rotor.commonstuff.RotorUtils
import ai.rotor.commonstuff.RotorUtils.Companion.VEHICLE_NAME_REGEX
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
        { acc, device -> if (device.name.contains(RotorUtils.VEHICLE_NAME_REGEX)) acc+1 else acc }

        return when(numberOfConnectedVehicles) {
                    1 ->    READY_VEHICLE_CONNECTED
                    else ->    VEHICLE_NOT_CONNECTED
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

    fun isValidBTDeviceToConnectTo(genericBTDevice: GenericBTDevice) = when {
        genericBTDevice.name.contains(VEHICLE_NAME_REGEX) -> true
        simulatorDevice == genericBTDevice -> true
        else -> false
    }


}