package de.ys_solutions.magic_thegathering.data.module

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import de.ys_solutions.magic_thegathering.data.api.MagicApi
import de.ys_solutions.magic_thegathering.util.StethoInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetModule(internal val baseUrl: String) {

    @Provides
    @Singleton
    internal fun providesCache(context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    internal fun providesMoshi(): Moshi {
        return Moshi.Builder()
                .build()
    }

    @Provides
    @Singleton
    internal fun providesOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    @Provides
    @Singleton
    internal fun providesRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    internal fun providesMagicApi(retrofit: Retrofit): MagicApi {
        return retrofit.create(MagicApi::class.java)
    }
}
