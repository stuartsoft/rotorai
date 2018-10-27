package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.VehicleConnectionState
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

    private lateinit var viewModel: WelcomeViewModel
    private lateinit var app: Application

    @MockK
    val mockBTVehicleConnector = mockk<BTVehicleConnector>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        app = RuntimeEnvironment.application
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.UNAVAILABLE
    }

    @Test
    fun setupViewModel() {
    }

    @Test
    fun startupWelcomeVMStepUnavailable() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.UNAVAILABLE
        viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        viewModel.setupViewModel()

        assertEquals(WelcomeViewModel.WelcomeScreenStep.SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.ui_welcome_doesnt_have_bt), viewModel.getHeaderMsg())
        assertEquals(false, viewModel.isEnableBTLinkVisible())
    }

    @Test
    fun startupWelcomeVMStepOffline() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.OFFLINE
        viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        viewModel.setupViewModel()

        assertEquals(WelcomeViewModel.WelcomeScreenStep.SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.ui_welcome_header_enable_bt), viewModel.getHeaderMsg())
        assertEquals(true, viewModel.isEnableBTLinkVisible())
    }

    @Test
    fun startupWelcomeVMStepNotConnected() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.VEHICLE_NOT_CONNECTED
        viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        viewModel.setupViewModel()

        assertEquals(WelcomeViewModel.WelcomeScreenStep.SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
        assertEquals(app.getString(R.string.ui_welcome_header_select_vehicle), viewModel.getHeaderMsg())
        assertEquals(false, viewModel.isEnableBTLinkVisible())
    }

    @Test
    fun startupWelcomeVMStepConnected() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.READY_VEHICLE_CONNECTED
        viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        viewModel.setupViewModel()

        assertEquals(WelcomeViewModel.WelcomeScreenStep.CONNECTED, viewModel.getWelcomeScreenStep())
        assertEquals(false, viewModel.isEnableBTLinkVisible())
    }

    @Test
    fun needsBluetoothLinkShouldShow() {
        //TODO rebuild this test using the multistate screen
    }

    @Test
    fun onReceiveBroadcastBTStateChange() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        verify (exactly = 0) { viewModel.notifyChange() }

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 1) { viewModel.notifyChange() }
    }

    @Test
    fun onReceiveBroadcastStartDiscoveryOnlyWhenOffToOn() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
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
    fun onReceiveBroadcastNewDeviceDiscovered() {
        every { mockBTVehicleConnector.inspectNewDevice(any()) } returns Unit
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        viewModel = WelcomeViewModel(app, mockBTVehicleConnector)

        val intentA = Intent()
        val mockdevice : BluetoothDevice = spyk()
        intentA.putExtra(EXTRA_DEVICE, mockdevice )
        viewModel.onReceiveBroadcast(intentA)

        verify(exactly = 1) { mockBTVehicleConnector.inspectNewDevice(ofType(BluetoothDevice::class)) }
    }

    @Test
    fun randomReceiveBroadcastDoesntTriggerBT() {
        every { mockBTVehicleConnector.startDiscovery() } returns Unit
        viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))

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

}
