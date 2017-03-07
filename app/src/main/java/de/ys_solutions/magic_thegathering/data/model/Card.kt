package de.ys_solutions.magic_thegathering.data.model

/**
 * Created by Yannik on 06.03.2017 - 22:24.
 */

class Card(val id: String,
           val layout: String,
           val name: String,
           val names: Array<String>,
           val manaCost: String,
           val cmc: Double = 0.toDouble(),
           val colors: Array<String>,
           val colorIdentity: Array<String>,
           val type: String,
           val supertypes: Array<String>,
           val types: Array<String>,
           val subtypes: Array<String>,
           val rarity: String,
           val text: String,
           val originalText: String,
           val flavor: String,
           val artist: String,
           val number: String,
           val power: String,
           val toughness: String,
           val loyalty: Int = 0,
           val multiverseid: Int = -1,
           val valiations: IntArray,
           val imageName: String,
           val watermark: String,
           val border: String,
           val timeshifted: Boolean = false,
           val hand: Int = 0,
           val life: Int = 0,
           val reserved: Boolean = false,
           val releaseDate: String,
           val starter: Boolean = false,
           val set: String,
           val setName: String,
           val printings: Array<String>,
           val imageUrl: String) {

}
