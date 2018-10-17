package com.stuartsoft.rotorai.data

import android.bluetooth.BluetoothDevice
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BTVehicleConnectorTest {

    @MockK
    private val mockRotorBTDelegate : BTVehicleConnector.RotorBTDelegate = mockk()

    private val connector = BTVehicleConnector(mockRotorBTDelegate)

    @Before
    fun setUp() {

    }

    @Test
    fun bluetoothNotSupported() {
        every { mockRotorBTDelegate.isBluetoothRadioAvailable() } returns false

        assertEquals(VehicleConnectionState.UNAVAILABLE, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOff() {
        every { mockRotorBTDelegate.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTDelegate.isBluetoothRadioOn() } returns false

        assertEquals(VehicleConnectionState.OFFLINE, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOn() {
        every { mockRotorBTDelegate.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTDelegate.isBluetoothRadioOn() } returns true
        every { mockRotorBTDelegate.getBondedDeviceNamesAndAddress() } returns listOf()

        assertEquals(VehicleConnectionState.VEHICLE_NOT_CONNECTED, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOnButConnectedToWrongDevice() {
        every { mockRotorBTDelegate.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTDelegate.isBluetoothRadioOn() } returns true
        every { mockRotorBTDelegate.getBondedDeviceNamesAndAddress() } returns listOf(buildMockBTDevice("lmao", "1234"))

        assertEquals(VehicleConnectionState.VEHICLE_NOT_CONNECTED, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOnAndConnectedToVehicle() {
        every { mockRotorBTDelegate.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTDelegate.isBluetoothRadioOn() } returns true
        every { mockRotorBTDelegate.getBondedDeviceNamesAndAddress() } returns listOf(buildMockBTDevice(RotorUtils.DEFAULT_VEHICLE_NAME, "1234"))

        assertEquals(VehicleConnectionState.READY_VEHICLE_CONNECTED, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOnTooManyConnectedVehicles() {
        every { mockRotorBTDelegate.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTDelegate.isBluetoothRadioOn() } returns true
        every { mockRotorBTDelegate.getBondedDeviceNamesAndAddress() } returns
                listOf(buildMockBTDevice(RotorUtils.DEFAULT_VEHICLE_NAME, "1234"),
                        buildMockBTDevice(RotorUtils.DEFAULT_VEHICLE_NAME+"1", "5678"))

        assertEquals(VehicleConnectionState.TOO_MANY_VEHICLES_CONNECTED, connector.currentConnectionState())
    }

    //----- HELPER METHODS BELOW HERE -----


    private fun buildMockBTDevice(name: String, address: String): Pair<String, String> {
        return Pair(name, address)
    }




}