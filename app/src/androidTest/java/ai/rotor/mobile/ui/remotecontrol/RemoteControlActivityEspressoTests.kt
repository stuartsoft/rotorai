package ai.rotor.mobile.ui.remotecontrol

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import ai.rotor.mobile.MainApplicationDaggerMockRule

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteControlActivityEspressoTests {

    @JvmField @Rule val mockitoRule = MainApplicationDaggerMockRule()

    @JvmField @Rule val activityRule = ActivityTestRule<RemoteControlActivity>(RemoteControlActivity::class.java, false, false)

    @Test
    fun testLaunchActivity() {
        activityRule.launchActivity(null)
    }
}
