package de.ys_solutions.magic_thegathering.data.source

import de.ys_solutions.magic_thegathering.data.model.Card
import de.ys_solutions.magic_thegathering.data.model.CardType

/**
 * Created by Yannik on 06.03.2017 - 22:31.
 */

internal interface CardsDataSource {

    fun loadAllCards(queryParams: Map<String, String>, callback: LoadAllCardsCallback)

    fun loadCard(multiverseId: String, callback: LoadCardCallback)

    fun loadAllTypes(callback: LoadAllTypesCallback)

    interface LoadAllCardsCallback {

        fun onCardsLoaded(cards: List<Card>)

        fun onDataNotAvailable()
    }

    interface LoadCardCallback {

        fun onCardLoaded(card: Card)

        fun onDataNotAvailable()
    }

    interface LoadAllTypesCallback {

        fun onTypesLoaded(cardTypes: List<CardType>)

        fun onDataNotAvailable()
    }
}
