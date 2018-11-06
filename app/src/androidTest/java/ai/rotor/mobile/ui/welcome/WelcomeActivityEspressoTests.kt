package ai.rotor.mobile.ui.welcome

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import ai.rotor.mobile.MainApplicationDaggerMockRule

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeActivityEspressoTests {

    @JvmField @Rule val mockitoRule = MainApplicationDaggerMockRule()

    @JvmField @Rule val activityRule = ActivityTestRule<WelcomeActivity>(WelcomeActivity::class.java, false, false)

    @Test
    fun testLaunchActivity() {
        activityRule.launchActivity(null)
    }
}
