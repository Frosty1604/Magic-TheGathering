package de.ys_solutions.magic_thegathering.data.source;


import javax.inject.Singleton;

import dagger.Component;
import de.ys_solutions.magic_thegathering.data.module.AppModule;
import de.ys_solutions.magic_thegathering.data.module.NetModule;

@Singleton
@Component(modules = {CardsRepositoryModule.class, NetModule.class, AppModule.class})
public interface CardsRepositoryComponent {

    CardsRepository getCardsRepository();
}
