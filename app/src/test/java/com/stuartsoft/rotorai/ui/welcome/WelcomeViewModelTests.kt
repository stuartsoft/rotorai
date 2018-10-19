package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.content.Intent
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.RotorUtils
import com.stuartsoft.rotorai.data.VehicleConnectionState
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import org.junit.Assert
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
        viewModel = WelcomeViewModel(app, mockBTVehicleConnector)
        viewModel.setupViewModel()

    }

    @Test
    fun setupViewModel() {
    }

    @Test
    fun WelcomeVMStepUnavailable() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.UNAVAILABLE
        assertEquals(WelcomeViewModel.WelcomeScreenStep.SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
    }

    @Test
    fun WelcomeVMStepOffline() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.OFFLINE
        assertEquals(WelcomeViewModel.WelcomeScreenStep.SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
    }

    @Test
    fun WelcomeVMStepNotConnected() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.VEHICLE_NOT_CONNECTED
        assertEquals(WelcomeViewModel.WelcomeScreenStep.SELECT_VEHICLE, viewModel.getWelcomeScreenStep())
    }

    @Test
    fun WelcomeVMStepConnected() {
        every { mockBTVehicleConnector.currentConnectionState() } returns VehicleConnectionState.READY_VEHICLE_CONNECTED
        assertEquals(WelcomeViewModel.WelcomeScreenStep.CONNECTED, viewModel.getWelcomeScreenStep())
    }

    @Test
    fun needsBluetoothLinkShouldShow() {
        //TODO rebuild this test using the multistate screen
    }

    @Test
    fun broadcastFilterUpdatesViewModel() {
        viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 1) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
    }

    @Test
    fun randomReceiveBroadcastDoesntTriggerBT() {
        viewModel = spyk(WelcomeViewModel(app, mockBTVehicleConnector))
        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }

        val intentA = Intent()
        intentA.putExtra("asdf", STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.welcomeScreenStep) }
    }


    @Ignore
    @Test
    fun onClickNeedsBTInitiatesSingleEvent(){
        //TODO basically, testing that clicking the blue link causes a change on the live data
        //TODO I don't have the patience to test this right now
    }

    //----- HELPERS BELOW THIS LINE -----

}
