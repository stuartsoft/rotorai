package com.stuartsoft.rotorai.data

import dagger.Module
import dagger.Provides

@Module
class TestDataModule(val deviceList: MutableList<GenericBTDevice>) {
    @Provides
    fun giveMeADeviceList(): MutableList<GenericBTDevice> = deviceList
}