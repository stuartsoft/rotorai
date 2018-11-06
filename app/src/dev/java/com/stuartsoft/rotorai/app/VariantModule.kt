package ai.rotor.mobile.app

import ai.rotor.mobile.data.OkHttpSecurityModifier
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class VariantModule {

    @Provides
    fun provideOkHttpClientTrustAllBinding(settings: Settings): OkHttpSecurityModifier {
        return object: OkHttpSecurityModifier {
            override fun apply(builder: OkHttpClient.Builder) {
                if (settings.trustAllSSL) {
                    SSLDevelopmentHelper.applyTrustAllSettings(builder)
                }
            }
        }
    }
}
