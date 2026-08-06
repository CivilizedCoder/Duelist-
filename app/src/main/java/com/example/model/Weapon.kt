package com.example.model

data class Weapon(
    val id: String,
    val name: String,
    val category: String,
    val baseDamage: Int,
    val speedMs: Long,
    val parryWindowMs: Long,
    val staminaCost: Int,
    val shieldBreakerMultiplier: Float = 1.0f,
    val blockMitigation: Float = 0.85f,
    val description: String
) {
    companion object {
        val LONGSWORD = Weapon(
            id = "longsword",
            name = "Knight's Longsword",
            category = "Versatile Blade",
            baseDamage = 32,
            speedMs = 750L,
            parryWindowMs = 230L,
            staminaCost = 20,
            shieldBreakerMultiplier = 1.0f,
            blockMitigation = 0.80f,
            description = "Balanced range, damage, and speed. A staple weapon of medieval chivalry."
        )

        val BATTLEAXE = Weapon(
            id = "battleaxe",
            name = "Executioner's Battleaxe",
            category = "Heavy Cleaver",
            baseDamage = 48,
            speedMs = 950L,
            parryWindowMs = 170L,
            staminaCost = 35,
            shieldBreakerMultiplier = 1.8f,
            blockMitigation = 0.70f,
            description = "Devastating heavy swings that crush enemy stamina on block."
        )

        val RAPIER = Weapon(
            id = "rapier",
            name = "Duelist's Rapier",
            category = "Fencing Needle",
            baseDamage = 24,
            speedMs = 550L,
            parryWindowMs = 290L,
            staminaCost = 12,
            shieldBreakerMultiplier = 0.7f,
            blockMitigation = 0.65f,
            description = "Ultra-fast stabs with a generous parry timing window for counter-strikes."
        )

        val WARHAMMER = Weapon(
            id = "warhammer",
            name = "Crusader's Warhammer",
            category = "Blunt Armor Piercer",
            baseDamage = 42,
            speedMs = 880L,
            parryWindowMs = 190L,
            staminaCost = 30,
            shieldBreakerMultiplier = 1.5f,
            blockMitigation = 0.50f, // Bypasses block protection
            description = "Heavy blunt strike that ignores half of enemy block damage mitigation."
        )

        val BROADSWORD_SHIELD = Weapon(
            id = "broadsword_shield",
            name = "Heater Shield & Broadsword",
            category = "Bastion Defense",
            baseDamage = 28,
            speedMs = 800L,
            parryWindowMs = 250L,
            staminaCost = 15,
            shieldBreakerMultiplier = 0.9f,
            blockMitigation = 0.95f, // Exceptional block mitigation
            description = "Provides near impenetrable blocking at the cost of modest weapon damage."
        )

        val ALL_WEAPONS = listOf(
            LONGSWORD,
            BATTLEAXE,
            RAPIER,
            WARHAMMER,
            BROADSWORD_SHIELD
        )
    }
}
