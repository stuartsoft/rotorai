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
    }

    @Test
    fun needsBluetoothLinkShouldShow() {
        viewModel.setupViewModel()

        val randomBool = ((0..1).shuffled().first() == 1)
        every { mockBTAdapter.isEnabled } returns randomBool

        Assert.assertEquals(!randomBool, viewModel.isNeedsBluetoothRadio())
    }

    @Test
    fun broadcastFilterUpdatesViewModel() {
        viewModel.setupViewModel()

        viewModel = spyk(WelcomeViewModel(app, mockBTAdapter))

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 1) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }

        val intentB = Intent()
        intentB.putExtra(EXTRA_PREVIOUS_STATE, STATE_ON)
        intentB.putExtra(EXTRA_STATE, STATE_OFF)
        viewModel.onReceiveBroadcast(intentB)

        verify (exactly = 2) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }
    }

}
