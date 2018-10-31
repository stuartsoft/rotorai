package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.VehicleConnectionState
import com.stuartsoft.rotorai.data.VehicleConnectionState.*
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
        //TODO FILL THIS IN NOW!
    }

    @Test
    fun needsBluetoothLinkShouldShow() {
        //TODO rebuild this test using the multistate screen
    }

    @Test
    fun `BT switches from off to on`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        verify (exactly = 0) { viewModel.notifyChange() }

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 1) { viewModel.notifyChange() }
    }

    @Test
    fun `Start discovering devices when bt switches from off to on`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        verify (exactly = 0) { mockBTVehicleConnector.startDiscovery() }

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }

        val intentB = Intent()
        intentB.putExtra(EXTRA_PREVIOUS_STATE, STATE_ON)
        intentB.putExtra(EXTRA_STATE, STATE_OFF)
        viewModel.onReceiveBroadcast(intentB)

        verify (exactly = 1) { mockBTVehicleConnector.startDiscovery() }
    }

    @Test
    fun `New device is discovered`() {
        every { mockBTVehicleConnector.inspectNewDevice(any()) } returns Unit
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = WelcomeViewModel(app, mockBTVehicleConnector)

        val intentA = Intent()
        val mockdevice : BluetoothDevice = spyk()
        intentA.putExtra(EXTRA_DEVICE, mockdevice )
        viewModel.onReceiveBroadcast(intentA)

        verify(exactly = 1) { mockBTVehicleConnector.inspectNewDevice(ofType(BluetoothDevice::class)) }
    }

    @Test
    fun `Random broadcast intents shouldnt do anything`() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        val viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))

        val intentA = Intent()
        intentA.putExtra("asdf", STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 0) { viewModel.notifyChange() }
        verify(exactly = 0) { mockBTVehicleConnector.inspectNewDevice(ofType(BluetoothDevice::class)) }
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
        every { mockBTVehicleConnector.currentConnectionState() } returns connectionState
        val spyViewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        every { spyViewModel.isLocationPermissionEnabled() } returns locationIsEnabled
        return spyViewModel
    }

}
