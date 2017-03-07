package de.ys_solutions.magic_thegathering.data.component

import dagger.Component
import de.ys_solutions.magic_thegathering.MagicActivity
import de.ys_solutions.magic_thegathering.data.module.AppModule
import de.ys_solutions.magic_thegathering.data.module.NetModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        NetModule::class
))
interface NetComponent {

    fun inject(magicActivity: MagicActivity)
}