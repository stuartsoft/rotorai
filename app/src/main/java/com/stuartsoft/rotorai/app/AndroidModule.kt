package com.stuartsoft.rotorai.app

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidModule(private val application: MainApplication) {
    @Singleton
    @Provides
    fun provideContext(): Context  = application

    @Singleton
    @Provides
    fun provideApplication(): Application = application

    @Singleton
    @Provides
    fun provideBTAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
}
