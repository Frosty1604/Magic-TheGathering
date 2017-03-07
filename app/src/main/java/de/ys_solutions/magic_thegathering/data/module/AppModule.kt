package de.ys_solutions.magic_thegathering.data.module

import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class AppModule(internal val context: Context) {

    @Provides
    @Singleton
    internal fun providesContext(): Context {
        return this.context
    }
}
