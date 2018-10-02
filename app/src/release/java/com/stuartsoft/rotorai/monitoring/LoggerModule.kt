package com.stuartsoft.rotorai.monitoring

import com.stuartsoft.rotorai.monitoring.model.NoOpTree
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module
class LoggerModule {
    @Singleton
    @Provides
    fun provideTimberTree() : Timber.Tree = NoOpTree()
}
