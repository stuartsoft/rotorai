package com.stuartsoft.rotorai.ui.welcome

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

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        val app = RuntimeEnvironment.application
        viewModel = WelcomeViewModel(app)
    }

    @Test
    fun testTrueIsTrue() {
        // This is lame...replace with something useful!
        Assert.assertTrue(true)
    }
}
