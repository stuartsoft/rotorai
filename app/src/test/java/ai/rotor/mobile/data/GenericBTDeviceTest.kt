package ai.rotor.mobile.data

import android.bluetooth.BluetoothDevice
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GenericBTDeviceTest {

    @MockK
    val bluetoothDevice = mockk<BluetoothDevice>()

    @Test
    fun testSetup(){
        every { bluetoothDevice.name } returns "LMAO"
        every { bluetoothDevice.address } returns "ADDRESS"
        val genericBTDevice = GenericBTDevice(bluetoothDevice)

        assertEquals("LMAO", genericBTDevice.name)
        assertEquals("ADDRESS", genericBTDevice.address)
    }

    @Test
    fun testNulls(){
        every { bluetoothDevice.name } returns null
        every { bluetoothDevice.address } returns null
        val genericBTDevice = GenericBTDevice(bluetoothDevice)

        assertEquals("", genericBTDevice.name)
        assertEquals("", genericBTDevice.address)
    }
}