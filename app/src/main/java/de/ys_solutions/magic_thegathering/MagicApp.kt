package de.ys_solutions.magic_thegathering

import android.app.Application
import com.facebook.stetho.Stetho
import de.ys_solutions.magic_thegathering.data.component.DaggerNetComponent
import de.ys_solutions.magic_thegathering.data.component.NetComponent
import de.ys_solutions.magic_thegathering.data.module.AppModule
import de.ys_solutions.magic_thegathering.data.module.NetModule
import timber.log.Timber
import timber.log.Timber.DebugTree



class MagicApp : Application() {

    val ENDPOINT : String = "https://api.magicthegathering.io/v1/"

    lateinit var netComponent : NetComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        netComponent = DaggerNetComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(ENDPOINT))
                .build()

    }
}
