package com.example.model

enum class MatchType {
    SOLO_AI,
    LOCAL_2P,
    BLUETOOTH_P2P
}

enum class AiDifficulty(
    val label: String,
    val speedFactor: Float,
    val parryChance: Float,
    val defaultShakeMultiplier: Float,
    val description: String
) {
    SQUIRE("Squire (Novice)", 0.75f, 0.15f, 0.5f, "Slow 0.75x speed & subtle impact shake."),
    KNIGHT("Knight (Veteran)", 1.0f, 0.40f, 1.0f, "Standard 1.0x combat speed & balanced shake."),
    CHAMPION("Champion (Master)", 1.25f, 0.70f, 1.5f, "Swift 1.25x swings & heavy impact shake."),
    BERSERKER("Berserker (Hardcore)", 1.50f, 0.90f, 2.0f, "Hyper 1.50x speed & violent camera shake!")
}

enum class RoundState {
    COUNTDOWN,
    IN_COMBAT,
    ROUND_OVER,
    MATCH_OVER
}

data class FeedbackEvent(
    val type: EventType,
    val text: String,
    val direction: CombatDirection,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class EventType {
        PARRY,
        BLOCK,
        HIT,
        GUARD_BREAK,
        STUN,
        COUNTER_STRIKE
    }
}

data class DuelState(
    val matchType: MatchType = MatchType.SOLO_AI,
    val aiDifficulty: AiDifficulty = AiDifficulty.KNIGHT,
    val gameSpeedMultiplier: Float = 1.0f,
    val cameraShakeMultiplier: Float = 1.0f,
    val round: Int = 1,
    val roundState: RoundState = RoundState.COUNTDOWN,
    val countdownSeconds: Int = 3,
    val player1: FighterState = FighterState("p1", "Sir Player", weapon = Weapon.LONGSWORD),
    val player2: FighterState = FighterState("p2", "Sir Roland", weapon = Weapon.BATTLEAXE),
    val p1Wins: Int = 0,
    val p2Wins: Int = 0,
    val activeAttackerId: String? = null,
    val attackDirection: CombatDirection? = null,
    val attackProgress: Float = 0f, // 0.0f (windup start) to 1.0f (impact)
    val parryWindowActive: Boolean = false,
    val lastFeedbackEvent: FeedbackEvent? = null,
    val cameraShake: Float = 0f,
    val executionCamActive: Boolean = false,
    val winnerName: String? = null
)
