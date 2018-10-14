package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
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
        viewModel.setupViewModel()

    }

    @Test
    fun setupViewModel() {
    }

    @Test
    fun needsBluetoothLinkShouldShow() {
        val randomBool = ((0..1).shuffled().first() == 1)
        every { mockBTAdapter.isEnabled } returns randomBool

        Assert.assertEquals(!randomBool, viewModel.isNeedsBluetoothRadio())
    }

    @Test
    fun broadcastFilterUpdatesViewModel() {
        viewModel = spyk(WelcomeViewModel(app, mockBTAdapter))
        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }

        val intentA = Intent()
        intentA.putExtra(EXTRA_PREVIOUS_STATE, STATE_OFF)
        intentA.putExtra(EXTRA_STATE, STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 1) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }
    }

    @Test
    fun randomReceiveBroadcastDoesntTriggerBT() {
        viewModel = spyk(WelcomeViewModel(app, mockBTAdapter))
        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }

        val intentA = Intent()
        intentA.putExtra("asdf", STATE_ON)
        viewModel.onReceiveBroadcast(intentA)

        verify (exactly = 0) { viewModel.notifyPropertyChanged(BR.needsBluetoothRadio) }
    }

    @Test
    fun onClickNeedsBTInitiatesSingleEvent(){

        //gotta figure out how to write this test
        //kinda don't know how to test live data really

    }



}
