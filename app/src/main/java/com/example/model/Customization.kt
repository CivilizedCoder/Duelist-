package com.example.model

import androidx.compose.ui.graphics.Color

data class SteelFinish(
    val id: String,
    val name: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val sheenColorHex: Long,
    val description: String
) {
    fun getPrimaryColor(): Color = Color(primaryColorHex)
    fun getAccentColor(): Color = Color(accentColorHex)
    fun getSheenColor(): Color = Color(sheenColorHex)

    companion object {
        val POLISHED_STEEL = SteelFinish(
            id = "polished_steel",
            name = "Polished Steel",
            primaryColorHex = 0xFFD0D4DC,
            accentColorHex = 0xFF8A90A0,
            sheenColorHex = 0xFFFFFFFF,
            description = "Classic mirror-polished knightly steel."
        )

        val DAMASCUS = SteelFinish(
            id = "damascus",
            name = "Damascus Steel",
            primaryColorHex = 0xFF4A505C,
            accentColorHex = 0xFF8892A4,
            sheenColorHex = 0xFFC0C8D8,
            description = "Intricate folded steel waves crafted by master smiths."
        )

        val GILDED_GOLD = SteelFinish(
            id = "gilded_gold",
            name = "Royal Gilded Gold",
            primaryColorHex = 0xFFE5B83B,
            accentColorHex = 0xFF8C6811,
            sheenColorHex = 0xFFFFF1A8,
            description = "Pure gold filigree engraving fit for emperors."
        )

        val OBSIDIAN_BLACK = SteelFinish(
            id = "obsidian",
            name = "Obsidian Black",
            primaryColorHex = 0xFF1C1D22,
            accentColorHex = 0xFF3D404A,
            sheenColorHex = 0xFF707688,
            description = "Dark forged steel that absorbs tournament torches."
        )

        val CRIMSON_STEEL = SteelFinish(
            id = "crimson",
            name = "Crimson Bloodsteel",
            primaryColorHex = 0xFF8E1B1B,
            accentColorHex = 0xFF4A0A0A,
            sheenColorHex = 0xFFE53935,
            description = "Tempered in dragon flame with deep ruby sheen."
        )

        val ALL_FINISHES = listOf(
            POLISHED_STEEL,
            DAMASCUS,
            GILDED_GOLD,
            OBSIDIAN_BLACK,
            CRIMSON_STEEL
        )
    }
}
