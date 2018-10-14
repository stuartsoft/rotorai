package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.content.Context
import android.content.Intent
import com.stuartsoft.rotorai.BR
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
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
    val mockBTAdapter = mockk<BluetoothAdapter>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        app = RuntimeEnvironment.application
        viewModel = WelcomeViewModel(app, mockBTAdapter)
    }

    @Test
    fun setupViewModel() {
        //ARRANGE
        every { mockBTAdapter.isEnabled } returns true
        Assert.assertNull(viewModel.bluetoothRadioIsOn)

        //ACT
        viewModel.setupViewModel()

        //ASSERT
        Assert.assertTrue(viewModel.bluetoothRadioIsOn!!)
    }

    @Test
    fun needsBluetoothLinkShouldShow() {
        viewModel.bluetoothRadioIsOn = false
        Assert.assertTrue(viewModel.isNeedsBluetoothRadio())

        viewModel.bluetoothRadioIsOn = true
        Assert.assertFalse(viewModel.isNeedsBluetoothRadio())
    }

    @Test
    fun broadcastFilterUpdatesViewModel() {
        viewModel = spyk(WelcomeViewModel(app, mockBTAdapter))
        viewModel.bluetoothRadioIsOn = false

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        Assert.assertTrue(viewModel.bluetoothRadioIsOn!!)
        verify (exactly = 1) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }


        val intentB = Intent()
        intentB.putExtra(EXTRA_PREVIOUS_STATE, STATE_ON)
        intentB.putExtra(EXTRA_STATE, STATE_OFF)
        viewModel.onReceiveBroadcast(intentB)

        Assert.assertFalse(viewModel.bluetoothRadioIsOn!!)
        verify (exactly = 2) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }

    }

}
