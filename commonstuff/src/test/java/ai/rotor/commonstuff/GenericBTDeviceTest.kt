package ai.rotor.commonstuff

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class GenericBTDeviceTest {

    @MockK
    val bluetoothDevice = mockk<BluetoothDevice>()

    @Test
    fun testSetup(){
        every { bluetoothDevice.name } returns "LMAO"
        every { bluetoothDevice.address } returns "ADDRESS"
        every { bluetoothDevice.uuids } returns arrayOf(ParcelUuid(UUID.fromString("4a14c657-e073-4432-a633-487233362fb2")))
        val genericBTDevice = GenericBTDevice(bluetoothDevice)

        assertEquals("LMAO", genericBTDevice.name)
        assertEquals("ADDRESS", genericBTDevice.address)
        assertEquals("4a14c657-e073-4432-a633-487233362fb2", genericBTDevice.uuid.toString())
    }

    @Test
    fun testNulls(){
        every { bluetoothDevice.name } returns null
        every { bluetoothDevice.address } returns null
        every { bluetoothDevice.uuids } returns null
        val genericBTDevice = GenericBTDevice(bluetoothDevice)

        assertEquals("", genericBTDevice.name)
        assertEquals("", genericBTDevice.address)
        assertNull(genericBTDevice.uuid)
    }


    @Test
    fun testEmpty(){
        every { bluetoothDevice.name } returns ""
        every { bluetoothDevice.address } returns ""
        every { bluetoothDevice.uuids } returns arrayOf()
        val genericBTDevice = GenericBTDevice(bluetoothDevice)

        assertEquals("", genericBTDevice.name)
        assertEquals("", genericBTDevice.address)
        assertNull(genericBTDevice.uuid)
    }
}