package com.stuartsoft.rotorai.data

import com.stuartsoft.rotorai.data.TestDataModule
import com.stuartsoft.rotorai.ui.welcome.WelcomeViewModel
import dagger.Component

@Component(modules = [TestDataModule::class])
interface TestComponent {
    fun inject(welcomeViewModel: WelcomeViewModel)
}