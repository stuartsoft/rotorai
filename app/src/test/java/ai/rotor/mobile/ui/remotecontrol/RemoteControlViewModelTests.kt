package ai.rotor.mobile.ui.remotecontrol

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RemoteControlViewModelTests {

    private lateinit var viewModel: RemoteControlViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        val app = RuntimeEnvironment.application
        viewModel = RemoteControlViewModel(app)
    }

    @Test
    fun testTrueIsTrue() {
        // This is lame...replace with something useful!
        Assert.assertTrue(true)
    }
}
