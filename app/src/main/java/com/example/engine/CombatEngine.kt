package com.example.engine

import com.example.model.AiDifficulty
import com.example.model.CombatDirection
import com.example.model.DuelState
import com.example.model.FeedbackEvent
import com.example.model.FighterStance
import com.example.model.FighterState
import com.example.model.MatchType
import com.example.model.RoundState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class CombatEngine {

    private val _duelState = MutableStateFlow(DuelState())
    val duelState: StateFlow<DuelState> = _duelState.asStateFlow()

    private var lastTickTime: Long = System.currentTimeMillis()
    private var aiDecisionTimer: Long = 0L

    fun startNewMatch(
        matchType: MatchType,
        difficulty: AiDifficulty = AiDifficulty.KNIGHT,
        gameSpeedMultiplier: Float = difficulty.speedFactor,
        cameraShakeMultiplier: Float = difficulty.defaultShakeMultiplier,
        player1Fighter: FighterState,
        player2Fighter: FighterState
    ) {
        _duelState.value = DuelState(
            matchType = matchType,
            aiDifficulty = difficulty,
            gameSpeedMultiplier = gameSpeedMultiplier,
            cameraShakeMultiplier = cameraShakeMultiplier,
            round = 1,
            roundState = RoundState.COUNTDOWN,
            countdownSeconds = 3,
            player1 = player1Fighter.createInitialState(),
            player2 = player2Fighter.createInitialState(),
            p1Wins = 0,
            p2Wins = 0,
            activeAttackerId = null,
            attackDirection = null,
            attackProgress = 0f,
            parryWindowActive = false,
            lastFeedbackEvent = null,
            cameraShake = 0f,
            executionCamActive = false,
            winnerName = null
        )
        lastTickTime = System.currentTimeMillis()
    }

    /**
     * Dynamically update speed or camera shake multipliers.
     */
    fun updateGameplaySettings(speedMultiplier: Float, shakeMultiplier: Float) {
        val current = _duelState.value
        _duelState.value = current.copy(
            gameSpeedMultiplier = speedMultiplier,
            cameraShakeMultiplier = shakeMultiplier
        )
    }

    /**
     * Ticks the physics and combat state (called on frame loop).
     */
    fun tick(deltaMs: Long) {
        val current = _duelState.value

        // Handle Camera Shake Decay
        var shake = current.cameraShake
        if (shake > 0f) {
            shake = (shake - (deltaMs / 200f)).coerceAtLeast(0f)
        }

        // Handle Countdown Phase
        if (current.roundState == RoundState.COUNTDOWN) {
            val nextCountdown = current.countdownSeconds
            _duelState.value = current.copy(cameraShake = shake)
            return
        }

        if (current.roundState != RoundState.IN_COMBAT) {
            _duelState.value = current.copy(cameraShake = shake)
            return
        }

        var p1 = current.player1
        var p2 = current.player2

        // 1. Regenerate Stamina for both fighters (scaled by game speed)
        val p1Regen = 18f * p1.armor.staminaRegenRate * (deltaMs / 1000f) * current.gameSpeedMultiplier
        val p2Regen = 18f * p2.armor.staminaRegenRate * (deltaMs / 1000f) * current.gameSpeedMultiplier

        if (p1.stance != FighterStance.WINDUP && p1.stance != FighterStance.STRIKING) {
            p1 = p1.copy(currentStamina = (p1.currentStamina + p1Regen).coerceAtMost(p1.maxStamina))
        }
        if (p2.stance != FighterStance.WINDUP && p2.stance != FighterStance.STRIKING) {
            p2 = p2.copy(currentStamina = (p2.currentStamina + p2Regen).coerceAtMost(p2.maxStamina))
        }

        // Recover Stunned / Hit Reaction stances
        if (p1.stance == FighterStance.STUNNED || p1.stance == FighterStance.HIT_REACTION) {
            // Automatic recovery after 1.2s
        }

        // 2. Process Ongoing Attack Progress (Speed scaled by Difficulty & Game Speed Multiplier)
        var attackProgress = current.attackProgress
        var activeAttacker = current.activeAttackerId
        var attackDir = current.attackDirection
        var parryActive = current.parryWindowActive
        var feedback = current.lastFeedbackEvent
        var executionCam = current.executionCamActive

        if (activeAttacker != null && attackDir != null) {
            val attacker = if (activeAttacker == p1.id) p1 else p2
            val defender = if (activeAttacker == p1.id) p2 else p1

            val effectiveSpeedFactor = (current.aiDifficulty.speedFactor * current.gameSpeedMultiplier).coerceAtLeast(0.2f)
            val totalWindupMs = (attacker.weapon.speedMs / effectiveSpeedFactor).toLong()
            val progressDelta = deltaMs.toFloat() / totalWindupMs.toFloat()
            attackProgress += progressDelta

            // Activate Parry Timing Window (e.g., progress between 0.68 and 0.98)
            parryActive = attackProgress in 0.68f..0.98f

            // Check Impact Moment
            if (attackProgress >= 1.0f) {
                // IMPACT COLLISION RESOLUTION!
                val resolved = resolveImpact(
                    attacker = attacker,
                    defender = defender,
                    attackDirection = attackDir,
                    shakeMultiplier = current.cameraShakeMultiplier
                )

                if (activeAttacker == p1.id) {
                    p1 = resolved.attacker
                    p2 = resolved.defender
                } else {
                    p2 = resolved.attacker
                    p1 = resolved.defender
                }

                feedback = resolved.feedback
                shake = resolved.shakeMagnitude

                if (!p1.isAlive || !p2.isAlive) {
                    executionCam = true
                }

                // Reset Attack State
                activeAttacker = null
                attackDir = null
                attackProgress = 0f
                parryActive = false
            }
        }

        // 3. AI Behavior Trigger (for Solo AI Mode - scaled by speed factor)
        if (current.matchType == MatchType.SOLO_AI && p2.isAlive && p1.isAlive) {
            aiDecisionTimer += deltaMs
            val targetAiTimerThreshold = (750L / current.gameSpeedMultiplier).toLong().coerceAtLeast(200L)
            if (aiDecisionTimer >= targetAiTimerThreshold) {
                aiDecisionTimer = 0L
                processAiTurn(p1, p2, activeAttacker, attackDir, current.aiDifficulty) { newP2, newAttacker, newDir ->
                    p2 = newP2
                    if (newAttacker != null) {
                        activeAttacker = newAttacker
                        attackDir = newDir
                        attackProgress = 0f
                    }
                }
            }
        }

        // 4. Check Match Over Conditions
        var roundState = current.roundState
        var winner = current.winnerName
        var p1Wins = current.p1Wins
        var p2Wins = current.p2Wins

        if (!p1.isAlive || !p2.isAlive) {
            roundState = RoundState.ROUND_OVER
            if (!p1.isAlive && p2.isAlive) {
                winner = p2.name
                p2Wins++
            } else if (!p2.isAlive && p1.isAlive) {
                winner = p1.name
                p1Wins++
            } else {
                winner = "Draw"
            }
        }

        _duelState.value = current.copy(
            player1 = p1,
            player2 = p2,
            activeAttackerId = activeAttacker,
            attackDirection = attackDir,
            attackProgress = attackProgress,
            parryWindowActive = parryActive,
            lastFeedbackEvent = feedback,
            cameraShake = shake,
            executionCamActive = executionCam,
            roundState = roundState,
            winnerName = winner,
            p1Wins = p1Wins,
            p2Wins = p2Wins
        )
    }

    /**
     * Updates player joystick guard direction in real time.
     */
    fun setPlayerGuard(playerId: String, direction: CombatDirection) {
        val current = _duelState.value
        val p1 = if (playerId == "p1") current.player1.copy(guardDirection = direction) else current.player1
        val p2 = if (playerId == "p2") current.player2.copy(guardDirection = direction) else current.player2
        _duelState.value = current.copy(player1 = p1, player2 = p2)
    }

    /**
     * Player attempts an attack from current guard direction.
     */
    fun playerInitiateAttack(playerId: String): Boolean {
        val current = _duelState.value
        if (current.roundState != RoundState.IN_COMBAT || current.activeAttackerId != null) return false

        val attacker = if (playerId == "p1") current.player1 else current.player2
        if (attacker.stance == FighterStance.STUNNED || attacker.currentStamina < attacker.weapon.staminaCost) return false

        val attackDirection = attacker.guardDirection
        val updatedAttacker = attacker.copy(
            currentStamina = attacker.currentStamina - attacker.weapon.staminaCost,
            stance = FighterStance.WINDUP
        )

        _duelState.value = current.copy(
            player1 = if (playerId == "p1") updatedAttacker else current.player1,
            player2 = if (playerId == "p2") updatedAttacker else current.player2,
            activeAttackerId = playerId,
            attackDirection = attackDirection,
            attackProgress = 0f
        )
        return true
    }

    /**
     * Player presses Parry/Action button while under attack.
     */
    fun playerAttemptParry(playerId: String) {
        val current = _duelState.value
        val activeAttacker = current.activeAttackerId ?: return
        if (activeAttacker == playerId) return // Attacker can't parry their own attack

        val defender = if (playerId == "p1") current.player1 else current.player2
        val attacker = if (activeAttacker == "p1") current.player1 else current.player2
        val attackDir = current.attackDirection ?: return

        // Must match 120-degree direction AND be within parry window progress (0.68..0.98)
        if (defender.guardDirection == attackDir && current.parryWindowActive) {
            // SUCCESSFUL PERFECT PARRY!
            val newDefender = defender.copy(
                stance = FighterStance.PARRYING,
                currentStamina = (defender.currentStamina + 25f).coerceAtMost(defender.maxStamina),
                parriesExecuted = defender.parriesExecuted + 1
            )
            val newAttacker = attacker.copy(
                stance = FighterStance.STUNNED // Opponent Stunned!
            )

            val feedback = FeedbackEvent(
                type = FeedbackEvent.EventType.PARRY,
                text = "PERFECT PARRY!",
                direction = attackDir
            )

            _duelState.value = current.copy(
                player1 = if (playerId == "p1") newDefender else newAttacker,
                player2 = if (playerId == "p2") newDefender else newAttacker,
                activeAttackerId = null,
                attackDirection = null,
                attackProgress = 0f,
                parryWindowActive = false,
                lastFeedbackEvent = feedback,
                cameraShake = 15f * current.cameraShakeMultiplier
            )
        }
    }

    private data class ImpactResult(
        val attacker: FighterState,
        val defender: FighterState,
        val feedback: FeedbackEvent,
        val shakeMagnitude: Float
    )

    private fun resolveImpact(
        attacker: FighterState,
        defender: FighterState,
        attackDirection: CombatDirection,
        shakeMultiplier: Float
    ): ImpactResult {
        val isDirectionMatched = defender.guardDirection == attackDirection

        return if (isDirectionMatched) {
            // BLOCK EXECUTED!
            val baseDmg = attacker.weapon.baseDamage.toFloat()
            val mitigation = defender.weapon.blockMitigation
            val actualDmg = baseDmg * (1f - mitigation)

            val staminaPenalty = 25f * attacker.weapon.shieldBreakerMultiplier
            val newDefenderStamina = defender.currentStamina - staminaPenalty

            val (finalHp, finalStamina, stance, feedbackText, shake) = if (newDefenderStamina <= 0f) {
                // GUARD BROKEN!
                val bonusDmg = baseDmg * 0.7f
                Tuple5(
                    (defender.currentHp - bonusDmg).coerceAtLeast(0f),
                    0f,
                    FighterStance.STUNNED,
                    "GUARD BROKEN!",
                    12f
                )
            } else {
                Tuple5(
                    (defender.currentHp - actualDmg).coerceAtLeast(0f),
                    newDefenderStamina,
                    FighterStance.BLOCKING,
                    "BLOCKED!",
                    6f
                )
            }

            val updatedDefender = defender.copy(
                currentHp = finalHp,
                currentStamina = finalStamina,
                stance = if (finalHp <= 0f) FighterStance.DEFEATED else stance
            )
            val updatedAttacker = attacker.copy(
                stance = FighterStance.RECOVERY
            )

            ImpactResult(
                attacker = updatedAttacker,
                defender = updatedDefender,
                feedback = FeedbackEvent(
                    type = if (newDefenderStamina <= 0f) FeedbackEvent.EventType.GUARD_BREAK else FeedbackEvent.EventType.BLOCK,
                    text = feedbackText,
                    direction = attackDirection
                ),
                shakeMagnitude = shake * shakeMultiplier
            )
        } else {
            // UNGUARDED HIT!
            val fullDmg = attacker.weapon.baseDamage.toFloat()
            val newHp = (defender.currentHp - fullDmg).coerceAtLeast(0f)

            val updatedDefender = defender.copy(
                currentHp = newHp,
                stance = if (newHp <= 0f) FighterStance.DEFEATED else FighterStance.HIT_REACTION
            )
            val updatedAttacker = attacker.copy(
                stance = FighterStance.RECOVERY
            )

            ImpactResult(
                attacker = updatedAttacker,
                defender = updatedDefender,
                feedback = FeedbackEvent(
                    type = FeedbackEvent.EventType.HIT,
                    text = "CRITICAL HIT! -${fullDmg.toInt()}",
                    direction = attackDirection
                ),
                shakeMagnitude = 18f * shakeMultiplier
            )
        }
    }

    private fun processAiTurn(
        p1: FighterState,
        p2: FighterState,
        activeAttackerId: String?,
        currentAttackDir: CombatDirection?,
        difficulty: AiDifficulty,
        onAiUpdated: (FighterState, String?, CombatDirection?) -> Unit
    ) {
        if (activeAttackerId == p1.id && currentAttackDir != null) {
            // AI is defending against P1 attack
            if (Random.nextFloat() < difficulty.parryChance) {
                // AI shifts guard to match player attack direction
                val updatedP2 = p2.copy(guardDirection = currentAttackDir)
                onAiUpdated(updatedP2, activeAttackerId, currentAttackDir)
            }
        } else if (activeAttackerId == null && p2.currentStamina >= p2.weapon.staminaCost) {
            // AI initiates attack
            val randomDir = CombatDirection.random()
            val updatedP2 = p2.copy(
                guardDirection = randomDir,
                currentStamina = p2.currentStamina - p2.weapon.staminaCost,
                stance = FighterStance.WINDUP
            )
            onAiUpdated(updatedP2, "p2", randomDir)
        }
    }

    fun decrementCountdown() {
        val current = _duelState.value
        if (current.roundState == RoundState.COUNTDOWN) {
            if (current.countdownSeconds > 1) {
                _duelState.value = current.copy(countdownSeconds = current.countdownSeconds - 1)
            } else {
                _duelState.value = current.copy(
                    roundState = RoundState.IN_COMBAT,
                    countdownSeconds = 0
                )
            }
        }
    }

    private data class Tuple5<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )
}
