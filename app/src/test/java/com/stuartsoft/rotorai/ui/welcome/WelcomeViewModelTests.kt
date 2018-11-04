package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.*
import com.stuartsoft.rotorai.data.VehicleConnectionState.*
import com.stuartsoft.rotorai.ui.welcome.WelcomeViewModel.Companion.REQUEST_ENABLE_LOCATION_PERMISSION
import com.stuartsoft.rotorai.ui.welcome.WelcomeViewModel.WelcomeScreenStep.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

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
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, false)

        viewModel.onRequestPermissionResult(
                REQUEST_ENABLE_LOCATION_PERMISSION,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                intArrayOf(0))

        verify { mockBTVehicleConnector.startDiscovery() }
        verify { viewModel.notifyChange() }
        verify { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }

    }

    @Test
    fun `Location Permission Denied`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = buildSpyViewModel(VEHICLE_NOT_CONNECTED, false)

        viewModel.onRequestPermissionResult(
                REQUEST_ENABLE_LOCATION_PERMISSION,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                intArrayOf(-1))

        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
        verify (exactly = 0) { viewModel.notifyChange() }
        verify { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
    }

    @Test
    fun `Current connection state must be VEHICLE_NOT_CONNECTED to start discovery mode`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        every { mockBTVehicleConnector.currentConnectionState() } returns OFFLINE
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        val someRandomOldDevicePreviouslyDiscovered = GenericBTDevice("asdf", "0000")
        injectTestModule(viewModel, mutableListOf(someRandomOldDevicePreviouslyDiscovered))

        //ACT
        viewModel.beginSearchingForDevices()//derp, we should look for devices!

        //ASSERT
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() } // NO, don't look for devices. There's no BT, ya nerd
        assertEquals(1, viewModel.getDiscoveredDevices().count())

        //ARRANGE
        every { mockBTVehicleConnector.currentConnectionState() } returns VEHICLE_NOT_CONNECTED

        //ACT
        viewModel.beginSearchingForDevices()//try again

        //ASSERT
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() } // NO, don't look for devices. There's no BT, ya nerd
        assertEquals(0, viewModel.getDiscoveredDevices().count())
    }

    @Test
    fun `Start discovering devices when bt switches from off to on`() {
        //THIS TEST IS A MESS. I really should be mocking the btclasses directly. Shame on me

        //ARRANGE
        val viewModel = buildSpyViewModel(OFFLINE, true)
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
        verify (exactly = 0) { viewModel.notifyChange() }
        val offToOn = Intent()
        offToOn.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        offToOn.putExtra(EXTRA_STATE, STATE_ON)

        //ACT
        every { mockBTVehicleConnector.currentConnectionState() } returns VEHICLE_NOT_CONNECTED
        viewModel.onReceiveBroadcast(offToOn)

        //ASSERT
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }
        verify (exactly = 1) { viewModel.notifyChange() }

        //ARRANGE
        val onToOff = Intent()
        onToOff.putExtra(EXTRA_PREVIOUS_STATE, STATE_ON)
        onToOff.putExtra(EXTRA_STATE, STATE_OFF)

        //ACT
        every { mockBTVehicleConnector.currentConnectionState() } returns OFFLINE
        viewModel.onReceiveBroadcast(onToOff)

        //ASSERT
        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }
    }

    @Test
    fun `New device is discovered`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        injectTestModule(viewModel)
        assertEquals(0, viewModel.getDiscoveredDevices().count())

        val intentA = Intent()
        val mockdevice : BluetoothDevice = spyk()
        every { mockdevice.name } returns "lol"
        intentA.putExtra(EXTRA_DEVICE, mockdevice )
        viewModel.onReceiveBroadcast(intentA)

        assertEquals(1, viewModel.getDiscoveredDevices().count())
    }

    @Test
    fun `Dont show devices with no name`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        injectTestModule(viewModel)
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
    fun `Clear devices when starting discovery`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        every { mockBTVehicleConnector.currentConnectionState() } returns VEHICLE_NOT_CONNECTED
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        injectTestModule(viewModel, mutableListOf(GenericBTDevice("asdf", "0000")))

        assertEquals(1, viewModel.getDiscoveredDevices().count())

        viewModel.beginSearchingForDevices()

        assertEquals(0, viewModel.getDiscoveredDevices().count())
    }

    @Test
    fun `Random broadcast intents shouldnt do anything`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))

        val intentA = Intent()
        intentA.putExtra("asdf", STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 0) { viewModel.notifyChange() }
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }
    }

    @Ignore
    @Test
    fun onClickNeedsBTInitiatesSingleEvent() {
        //TODO basically, testing that clicking the blue link causes a change on the live data
        //TODO I don't have the patience to test this right now
    }

    //----- HELPERS BELOW THIS LINE -----


    private fun buildSpyViewModel(connectionState: VehicleConnectionState, locationIsEnabled: Boolean): WelcomeViewModel{
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        every { mockBTVehicleConnector.currentConnectionState() } returns connectionState
        val spyViewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        every { spyViewModel.isLocationPermissionEnabled() } returns locationIsEnabled
        injectTestModule(spyViewModel)
        return spyViewModel
    }

    private fun injectTestModule(welcomeViewModel: WelcomeViewModel, listOfDevices: MutableList<GenericBTDevice> = mutableListOf()){
        DaggerTestComponent.builder()
                .testDataModule(TestDataModule(listOfDevices))
                .build().inject(welcomeViewModel)
    }

}
