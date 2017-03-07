package de.ys_solutions.magic_thegathering.data.source;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.ys_solutions.magic_thegathering.data.api.MagicApi;

/**
 * Created by Yannik on 06.03.2017 - 22:38.
 */

@Module
public class CardsRepositoryModule {

    @Singleton
    @Provides
    CardsDataSource provideCardsDataSource(MagicApi magicApi) {
        return new CardsRepository(magicApi);
    }
}
