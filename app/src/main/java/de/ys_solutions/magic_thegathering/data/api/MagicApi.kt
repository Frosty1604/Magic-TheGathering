package de.ys_solutions.magic_thegathering.data.api

import de.ys_solutions.magic_thegathering.data.model.Card
import de.ys_solutions.magic_thegathering.data.model.CardType
import de.ys_solutions.magic_thegathering.data.model.Cards
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface MagicApi {
    @Headers("Cache-Control: no-cache")
    @GET("cards")
    fun getAllCards(): Call<Cards>

    @GET("cards/{id}")
    fun getCard(@Path("id") multiverseId: String): Call<Card>

    @GET("types")
    fun getAllCardTypes(): Call<List<CardType>>

}
