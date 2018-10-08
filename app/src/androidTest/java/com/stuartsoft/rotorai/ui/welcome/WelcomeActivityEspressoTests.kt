package com.stuartsoft.rotorai.ui.welcome

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.stuartsoft.rotorai.MainApplicationDaggerMockRule

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
