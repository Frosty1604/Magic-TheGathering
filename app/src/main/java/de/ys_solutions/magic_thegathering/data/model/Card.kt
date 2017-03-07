package de.ys_solutions.magic_thegathering.data.model

/**
 * Created by Yannik on 06.03.2017 - 22:24.
 */

class Card {
    val id: String? = null
    val layout: String? = null
    val name: String? = null
    val names: Array<String>? = null
    val manaCost: String? = null
    val cmc: Double = 0.toDouble()
    val colors: Array<String>? = null
    val colorIdentity: Array<String>? = null
    val type: String? = null
    val supertypes: Array<String>? = null
    val types: Array<String>? = null
    val subtypes: Array<String>? = null
    val rarity: String? = null
    val text: String? = null
    val originalText: String? = null
    val flavor: String? = null
    val artist: String? = null
    val number: String? = null
    val power: String? = null
    val toughness: String? = null
    val loyalty: Int = 0
    val multiverseid = -1
    val variations: IntArray? = null
    val imageName: String? = null
    val watermark: String? = null
    val border: String? = null
    val timeshifted: Boolean = false
    val hand: Int = 0
    val life: Int = 0
    val reserved: Boolean = false
    val releaseDate: String? = null
    val starter: Boolean = false
    val set: String? = null
    val setName: String? = null
    val printings: Array<String>? = null
    val imageUrl: String? = null
}
