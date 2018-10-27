package com.stuartsoft.rotorai.data

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BTVehicleConnectorTest {

    @MockK
    private val mockRotorBTAdapterWrapper : RotorBTAdapterWrapper = mockk()

    private val connector = BTVehicleConnector(mockRotorBTAdapterWrapper)

    @Before
    fun setUp() {

    }

    @Test
    fun bluetoothNotSupported() {
        every { mockRotorBTAdapterWrapper.isBluetoothRadioAvailable() } returns false

        assertEquals(VehicleConnectionState.UNAVAILABLE, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOff() {
        every { mockRotorBTAdapterWrapper.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTAdapterWrapper.isBluetoothRadioOn() } returns false

        assertEquals(VehicleConnectionState.OFFLINE, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOn() {
        every { mockRotorBTAdapterWrapper.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTAdapterWrapper.isBluetoothRadioOn() } returns true
        every { mockRotorBTAdapterWrapper.getBondedDeviceNamesAndAddress() } returns listOf()

        assertEquals(VehicleConnectionState.VEHICLE_NOT_CONNECTED, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOnButConnectedToWrongDevice() {
        every { mockRotorBTAdapterWrapper.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTAdapterWrapper.isBluetoothRadioOn() } returns true
        every { mockRotorBTAdapterWrapper.getBondedDeviceNamesAndAddress() } returns
                listOf(buildMockBTDevice("lmao", "1234"))

        assertEquals(VehicleConnectionState.VEHICLE_NOT_CONNECTED, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOnAndConnectedToVehicle() {
        every { mockRotorBTAdapterWrapper.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTAdapterWrapper.isBluetoothRadioOn() } returns true
        every { mockRotorBTAdapterWrapper.getBondedDeviceNamesAndAddress() } returns
                listOf(buildMockBTDevice(RotorUtils.DEFAULT_VEHICLE_NAME, "1234"))

        assertEquals(VehicleConnectionState.READY_VEHICLE_CONNECTED, connector.currentConnectionState())
    }

    @Test
    fun bluetoothOnTooManyConnectedVehicles() {
        every { mockRotorBTAdapterWrapper.isBluetoothRadioAvailable() } returns true
        every { mockRotorBTAdapterWrapper.isBluetoothRadioOn() } returns true
        every { mockRotorBTAdapterWrapper.getBondedDeviceNamesAndAddress() } returns
                listOf(buildMockBTDevice(RotorUtils.DEFAULT_VEHICLE_NAME, "1234"),
                        buildMockBTDevice(RotorUtils.DEFAULT_VEHICLE_NAME+"1", "5678"))

        assertEquals(VehicleConnectionState.TOO_MANY_VEHICLES_CONNECTED, connector.currentConnectionState())
    }


    @Test
    fun beginBTDeviceDiscovery() {
        every { mockRotorBTAdapterWrapper.startDiscovery() } returns Unit
        verify (exactly = 0) { mockRotorBTAdapterWrapper.startDiscovery() }

        connector.startDiscovery()

        verify (exactly = 1) { mockRotorBTAdapterWrapper.startDiscovery() }
    }

    //----- HELPER METHODS BELOW HERE -----


    private fun buildMockBTDevice(name: String, address: String): GenericBTDevice {
        return GenericBTDevice(name, address)
    }




}