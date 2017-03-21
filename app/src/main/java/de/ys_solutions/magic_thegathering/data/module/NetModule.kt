package de.ys_solutions.magic_thegathering.data.module

import android.content.Context
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.moshi.Moshi
import com.squareup.picasso.Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import de.ys_solutions.magic_thegathering.BuildConfig
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
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.cache(cache)
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(StethoInterceptor())
        }
        builder.build()

        return builder.build()

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

    @Provides
    @Singleton
    internal fun providesDownloader(httpClient: OkHttpClient): Downloader {
        return OkHttp3Downloader(httpClient)
    }

    @Provides
    @Singleton
    internal fun providesPicasso(context: Context, downloader: Downloader): Picasso {
        return Picasso.Builder(context)
                .downloader(downloader)
                .build()
    }
}
