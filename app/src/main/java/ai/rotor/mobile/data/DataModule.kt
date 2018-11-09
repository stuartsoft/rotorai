package ai.rotor.mobile.data

import ai.rotor.commonstuff.GenericBTDevice
import android.app.Application
import android.bluetooth.BluetoothAdapter
import com.squareup.moshi.Moshi
import ai.rotor.mobile.app.Settings
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton

interface OkHttpSecurityModifier {
    fun apply(builder: OkHttpClient.Builder)
}

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideCache(app: Application): Cache {
        val cacheDir = File(app.cacheDir, "http")
        return Cache(cacheDir, DISK_CACHE_SIZE.toLong())
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(cache: Cache, securityModifier: OkHttpSecurityModifier): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.cache(cache)
        securityModifier.apply(builder)
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideBaseUrl(settings: Settings): String {
        return settings.baseUrl
    }

    @Singleton
    @Provides
    fun provideConverter(): Converter.Factory {
        val moshi = Moshi.Builder().build()
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
            client: OkHttpClient,
            baseUrl: String,
            converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Singleton
    @Provides
    fun provideBTAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Singleton
    @Provides
    fun provideRotorBTAdapterWrapper(): RotorBTAdapterWrapper = RotorBTAdapterWrapper(provideBTAdapter())

    @Singleton
    @Provides
    fun provideBTVehicleConnector(rotorBTAdapterWrapper: RotorBTAdapterWrapper) : BTVehicleConnector {
        return BTVehicleConnector(rotorBTAdapterWrapper)
    }

    @Provides
    fun giveMeAListOfBTDevices() : MutableList<GenericBTDevice> {
        return mutableListOf()
    }

    companion object {
        private const val DISK_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
    }
}
