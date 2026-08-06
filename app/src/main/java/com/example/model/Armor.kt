package com.example.model

data class Armor(
    val id: String,
    val name: String,
    val hpBonus: Int,
    val staminaRegenRate: Float, // Multiplier (e.g. 1.0f = baseline)
    val parrySpeedBonusMs: Long, // Subtracted from windup delay
    val description: String
) {
    companion object {
        val FULL_PLATE = Armor(
            id = "plate",
            name = "Gilded Full Plate",
            hpBonus = 40,
            staminaRegenRate = 0.85f,
            parrySpeedBonusMs = 0L,
            description = "Maximum structural protection and health at the expense of slower stamina recovery."
        )

        val CHAINMAIL = Armor(
            id = "chainmail",
            name = "Reinforced Chainmail",
            hpBonus = 20,
            staminaRegenRate = 1.0f,
            parrySpeedBonusMs = 30L,
            description = "Flexible steel rings providing ideal balance between defense and mobility."
        )

        val DUELIST_LEATHER = Armor(
            id = "leather",
            name = "Duelist's Hardened Leather",
            hpBonus = 0,
            staminaRegenRate = 1.25f,
            parrySpeedBonusMs = 60L,
            description = "Lightweight gear allowing rapid stamina recovery and lightning parry reactions."
        )

        val ALL_ARMORS = listOf(FULL_PLATE, CHAINMAIL, DUELIST_LEATHER)
    }
}
