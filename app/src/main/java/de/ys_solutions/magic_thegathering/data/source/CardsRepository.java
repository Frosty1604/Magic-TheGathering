package de.ys_solutions.magic_thegathering.data.source;

import de.ys_solutions.magic_thegathering.data.api.MagicApi;
import de.ys_solutions.magic_thegathering.data.model.Card;
import de.ys_solutions.magic_thegathering.data.model.Cards;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yannik on 06.03.2017 - 22:30.
 */

public class CardsRepository implements CardsDataSource {

  private final MagicApi magicApi;

  @Inject
  public CardsRepository(MagicApi magicApi) {
    this.magicApi = magicApi;
  }

  @Override
  public void loadAllCards(@Nonnull final LoadAllCardsCallback callback) {
    Call<Cards> call = magicApi.getAllCards();

    call.enqueue(new Callback<Cards>() {
      @Override
      public void onResponse(Call<Cards> call, Response<Cards> response) {
        if (response.isSuccessful()) {
          callback.onCardsLoaded(response.body().getName());
        } else {
          callback.onDataNotAvailable();
        }
      }

      @Override
      public void onFailure(Call<Cards> call, Throwable t) {
        callback.onDataNotAvailable();
      }
    });
  }

  @Override
  public void loadCard(@Nonnull String multiverseId, @Nonnull final LoadCardCallback callback) {
    Call<Card> call = magicApi.getCard(multiverseId);

    call.enqueue(new Callback<Card>() {
      @Override
      public void onResponse(Call<Card> call, Response<Card> response) {
        if (response.isSuccessful()) {
          callback.onCardLoaded(response.body());
        } else {
          callback.onDataNotAvailable();
        }
      }

      @Override
      public void onFailure(Call<Card> call, Throwable t) {
        callback.onDataNotAvailable();
      }
    });
  }

  @Override
  public void loadAllTypes(@Nonnull LoadAllTypesCallback callback) {

  }
}
