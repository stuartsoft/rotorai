package com.stuartsoft.rotorai.data

import dagger.Module
import dagger.Provides

@Module
class TestDataModule() {
    @Provides
    fun giveMeADeviceList(): MutableList<GenericBTDevice> = mutableListOf()
}