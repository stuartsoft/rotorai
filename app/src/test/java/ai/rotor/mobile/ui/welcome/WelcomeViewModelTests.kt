package ai.rotor.mobile.ui.welcome

import ai.rotor.commonstuff.GenericBTDevice
import ai.rotor.mobile.BR
import ai.rotor.mobile.R
import ai.rotor.mobile.data.BTVehicleConnector
import ai.rotor.mobile.data.VehicleConnectionState
import ai.rotor.mobile.data.VehicleConnectionState.*
import ai.rotor.mobile.ui.welcome.WelcomeViewModel.Companion.REQUEST_ENABLE_LOCATION_PERMISSION
import ai.rotor.mobile.ui.welcome.WelcomeViewModel.WelcomeScreenStep.*
import android.app.Application
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import android.os.ParcelUuid
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.*

@RunWith(RobolectricTestRunner::class)
class WelcomeViewModelTests {

    private lateinit var app: Application

    @MockK
    val mockBTVehicleConnector = mockk<BTVehicleConnector>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        app = RuntimeEnvironment.application
    }

    @Test
    fun setupViewModel() {
    }

    @Test
    fun `starts up without bt available`() {
        val viewModel = buildSpyViewModel(UNAVAILABLE, true)
        viewModel.setupViewModel()

        assertEquals(BT_UNAVAILABLE, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.UI_WELCOME_BT_UNAVAILABLE), viewModel.getHeaderMsg())
    }

    @Test
    fun `starts up without location permission`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, false)
        viewModel.setupViewModel()

        assertEquals(ENABLE_LOCATION, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.UI_WELCOME_ENABLE_LOCATION_PERMISSION), viewModel.getHeaderMsg())
    }

    @Test
    fun `starts up without location permission and with bt off, we should first ask for location permission`() {
        val viewModel = buildSpyViewModel(OFFLINE, false)
        viewModel.setupViewModel()

        assertEquals(ENABLE_LOCATION, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.UI_WELCOME_ENABLE_LOCATION_PERMISSION), viewModel.getHeaderMsg())
    }

    @Test
    fun `starts up with bt off`() {
        val viewModel = buildSpyViewModel(OFFLINE, true)
        viewModel.setupViewModel()

        assertEquals(ENABLE_BT_RADIO, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.UI_WELCOME_ENABLE_BT_RADIO), viewModel.getHeaderMsg())
    }

    @Test
    fun `starts up with location permission and bt but not connected`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, true)
        viewModel.setupViewModel()

        assertEquals(SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.UI_WELCOME_SELECT_VEHICLE), viewModel.getHeaderMsg())
    }

    @Test
    fun `starts up and already connected`() {
        val viewModel = buildSpyViewModel(READY_VEHICLE_CONNECTED, true)
        viewModel.setupViewModel()

        assertEquals(CONNECTED, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.UI_WELCOME_CONNECTED), viewModel.getHeaderMsg())
    }

    @Test
    fun `should immediately begin searching on startup if possible`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, true)
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
        viewModel.setupViewModel()
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }
    }

    @Test
    fun `Location Permission Granted`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, false)

        viewModel.onRequestPermissionResult(
                REQUEST_ENABLE_LOCATION_PERMISSION,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                intArrayOf(0))

        verify { mockBTVehicleConnector.startDiscovery() }
        verify { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }

    }

    @Test
    fun `Location Permission Denied`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, false)

        viewModel.onRequestPermissionResult(
                REQUEST_ENABLE_LOCATION_PERMISSION,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                intArrayOf(-1))

        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
        verify { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
    }

    @Test
    fun `Current connection state must be VEHICLE_NOT_CONNECTED to start discovery mode`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        every { mockBTVehicleConnector.stopDiscovery() } returns Unit
        every { mockBTVehicleConnector.currentConnectionState() } returns OFFLINE
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)

        //ACT
        viewModel.beginSearchingForDevices()//derp, we should look for devices!

        //ASSERT
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() } // NO, don't look for devices. There's no BT, ya nerd

        //ARRANGE
        every { mockBTVehicleConnector.currentConnectionState() } returns VEHICLE_NOT_CONNECTED

        //ACT
        viewModel.beginSearchingForDevices()//try again

        //ASSERT
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() } //OK Now we are actually ready to search
    }

    @Test
    fun `BT switches from off to on, then on to off`() {
        //THIS TEST IS A MESS. I really should be mocking the android bt classes directly. Shame on me

        //ARRANGE
        val viewModel = buildSpyViewModel(OFFLINE, true)
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
        val offToOn = Intent()
        offToOn.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        offToOn.putExtra(EXTRA_STATE, STATE_ON)

        //ACT
        every { mockBTVehicleConnector.currentConnectionState() } returns VEHICLE_NOT_CONNECTED
        viewModel.onReceiveBroadcast(offToOn)

        //ASSERT
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }
        verify (exactly = 1) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }

        //ARRANGE
        val onToOff = Intent()
        onToOff.putExtra(EXTRA_PREVIOUS_STATE, STATE_ON)
        onToOff.putExtra(EXTRA_STATE, STATE_OFF)

        //ACT
        every { mockBTVehicleConnector.currentConnectionState() } returns OFFLINE
        viewModel.onReceiveBroadcast(onToOff)

        //ASSERT
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }
        verify (exactly = 2) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
    }

    @Test
    fun `New device is discovered`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, true)
        assertEquals(0, viewModel.getDiscoveredDevices().count())

        val intentA = Intent()
        val mockdevice : BluetoothDevice = spyk()
        every { mockdevice.name } returns "lol"
        intentA.putExtra(EXTRA_DEVICE, mockdevice )
        viewModel.onReceiveBroadcast(intentA)

        assertEquals(1, viewModel.getDiscoveredDevices().count())
        verify { viewModel.notifyPropertyChanged(BR.discoveredDevices) }
    }

    @Test
    fun `Show Simulator in device list at the top`() {
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, true)
        assertEquals(0, viewModel.getDiscoveredDevices().count())

        val intentA = Intent()
        val mockdevice : BluetoothDevice = spyk()
        every { mockdevice.name } returns "lol"
        intentA.putExtra(EXTRA_DEVICE, mockdevice )
        viewModel.onReceiveBroadcast(intentA)

        //OK NOW TURN ON THE SIMULATOR TOGGLE
        viewModel.shouldShowSimulatorInList = true

        assertEquals(2, viewModel.getDiscoveredDevices().count())
        assertEquals("4x4 Simulator", viewModel.getDiscoveredDevices()[0].name)
        assertEquals("lol", viewModel.getDiscoveredDevices()[1].name)
    }

    @Test
    fun `Dont show devices with no name`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        assertEquals(0, viewModel.getDiscoveredDevices().count())

        val intentA = Intent()
        val mockDeviceA : BluetoothDevice = spyk()
        every { mockDeviceA.name } returns ""
        intentA.putExtra(EXTRA_DEVICE, mockDeviceA )
        viewModel.onReceiveBroadcast(intentA)

        val intentB = Intent()
        val mockDeviceB : BluetoothDevice = spyk()
        every { mockDeviceB.name } returns null
        intentB.putExtra(EXTRA_DEVICE, mockDeviceB)
        viewModel.onReceiveBroadcast(intentB)

        assertEquals(0, viewModel.getDiscoveredDevices().count())
    }

    @Test
    fun `Dont show duplicate devices`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        assertEquals(0, viewModel.getDiscoveredDevices().count())
        val dName = "DeviceA"
        val dAddress = "11:22:33:44:55:66"
        val dUUID = UUID.randomUUID()

        val intentA = Intent()
        val mockDeviceA : BluetoothDevice = spyk()
        every { mockDeviceA.name } returns dName
        every { mockDeviceA.address } returns dAddress
        every { mockDeviceA.uuids } returns arrayOf(ParcelUuid(dUUID))
        intentA.putExtra(EXTRA_DEVICE, mockDeviceA )
        viewModel.onReceiveBroadcast(intentA)

        val intentB = Intent()
        val mockDeviceB : BluetoothDevice = spyk()
        every { mockDeviceB.name } returns dName
        every { mockDeviceB.address } returns dAddress
        every { mockDeviceB.uuids } returns arrayOf(ParcelUuid(dUUID))
        intentB.putExtra(EXTRA_DEVICE, mockDeviceB )
        viewModel.onReceiveBroadcast(intentB)

        assertEquals(1, viewModel.getDiscoveredDevices().count())
    }

    @Test
    fun `Clear devices when starting discovery`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        every { mockBTVehicleConnector.stopDiscovery() } returns Unit
        every { mockBTVehicleConnector.currentConnectionState() } returns VEHICLE_NOT_CONNECTED
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector, mutableListOf(GenericBTDevice("asdf", "0000", UUID.randomUUID())))

        assertEquals(1, viewModel.getDiscoveredDevices().count())

        viewModel.beginSearchingForDevices()

        assertEquals(0, viewModel.getDiscoveredDevices().count())
    }

    @Test
    fun `beginSearchingForDevices`() {
        //ARRANGE
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, true)

        //ACT
        viewModel.beginSearchingForDevices()

        //ASSERT
        verify { mockBTVehicleConnector.stopDiscovery() }
        verify { mockBTVehicleConnector.startDiscovery() }
    }

    @Test
    fun `Random broadcast intents shouldnt do anything`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))

        val intentA = Intent()
        intentA.putExtra("asdf", STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
    }

    //----- HELPERS BELOW THIS LINE -----


    private fun buildSpyViewModel(connectionState: VehicleConnectionState, locationIsEnabled: Boolean): WelcomeViewModel{
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        every { mockBTVehicleConnector.stopDiscovery() } returns Unit
        every { mockBTVehicleConnector.currentConnectionState() } returns connectionState
        val spyViewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        every { spyViewModel.isLocationPermissionEnabled() } returns locationIsEnabled
        return spyViewModel
    }


}
