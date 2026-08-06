package com.example.model

enum class FighterStance {
    IDLE,
    WINDUP,       // Preparing attack
    STRIKING,     // Active weapon swing frame
    BLOCKING,     // In guard position
    PARRYING,     // Timed parry action executed
    RECOVERY,     // Post-swing recovery
    STUNNED,      // Stunned after getting parried!
    HIT_REACTION, // Staggered from taking damage
    DEFEATED      // Health depleted
}

data class FighterState(
    val id: String,
    val name: String,
    val currentHp: Float = 100f,
    val maxHp: Float = 100f,
    val currentStamina: Float = 100f,
    val maxStamina: Float = 100f,
    val guardDirection: CombatDirection = CombatDirection.TOP,
    val stance: FighterStance = FighterStance.IDLE,
    val weapon: Weapon = Weapon.LONGSWORD,
    val armor: Armor = Armor.CHAINMAIL,
    val finish: SteelFinish = SteelFinish.POLISHED_STEEL,
    val wins: Int = 0,
    val parriesExecuted: Int = 0
) {
    val isAlive: Boolean get() = currentHp > 0f
    val hpPercentage: Float get() = (currentHp / maxHp).coerceIn(0f, 1f)
    val staminaPercentage: Float get() = (currentStamina / maxStamina).coerceIn(0f, 1f)

    fun createInitialState(): FighterState {
        val totalHp = 100f + armor.hpBonus
        return copy(
            currentHp = totalHp,
            maxHp = totalHp,
            currentStamina = 100f,
            maxStamina = 100f,
            stance = FighterStance.IDLE,
            guardDirection = CombatDirection.TOP
        )
    }
}
