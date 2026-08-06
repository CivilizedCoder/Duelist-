package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import com.example.model.CombatDirection
import com.example.model.DuelState
import com.example.model.FeedbackEvent
import com.example.model.FighterStance
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BronzeAccent
import com.example.ui.theme.CrimsonBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HealthRed
import com.example.ui.theme.ParrySparkGold
import com.example.ui.theme.ShieldZoneLeftColor
import com.example.ui.theme.ShieldZoneRightColor
import com.example.ui.theme.ShieldZoneTopColor
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelLight
import com.example.ui.theme.SteelMedium
import kotlin.random.Random

@Composable
fun VisualArenaCanvas(
    modifier: Modifier = Modifier,
    duelState: DuelState
) {
    // Generate random spark particles
    val particles = remember {
        List(25) {
            Triple(
                Random.nextFloat(), // x ratio
                Random.nextFloat(), // y ratio
                Random.nextFloat() * 20f + 5f // size
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("visual_arena_canvas")
    ) {
        val width = size.width
        val height = size.height

        // Apply Camera Shake offset
        val shakeOffset = if (duelState.cameraShake > 0f) {
            Offset(
                x = (Random.nextFloat() - 0.5f) * duelState.cameraShake * 8f,
                y = (Random.nextFloat() - 0.5f) * duelState.cameraShake * 8f
            )
        } else Offset.Zero

        // 1. Arena Background (Stone Wall & Floor with Torchlit Gradient)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    BackgroundDark,
                    SteelDark,
                    Color(0xFF221A1A), // Torch glow background
                    SteelDark,
                    BackgroundDark
                ),
                startY = 0f,
                endY = height
            ),
            topLeft = shakeOffset,
            size = size
        )

        // Torches on Wall
        val torchPositions = listOf(Offset(width * 0.18f, height * 0.28f), Offset(width * 0.82f, height * 0.28f))
        for (torch in torchPositions) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0x66FF5722), Color.Transparent),
                    center = torch + shakeOffset,
                    radius = 90f
                ),
                radius = 90f,
                center = torch + shakeOffset
            )
        }

        // Stone Arena Floor Grid Lines (Perspective Depth)
        val horizonY = height * 0.48f
        for (i in 0..10) {
            val lineY = horizonY + (height * 0.52f) * (i / 10f) * (i / 10f)
            drawLine(
                color = SteelMedium.copy(alpha = 0.4f),
                start = Offset(0f, lineY) + shakeOffset,
                end = Offset(width, lineY) + shakeOffset,
                strokeWidth = 2f
            )
        }

        // 2. Render Player 1 (Foreground / Bottom Left) & Player 2 (Opponent / Upper Right)
        val p1Pos = Offset(width * 0.32f, height * 0.68f) + shakeOffset
        val p2Pos = Offset(width * 0.68f, height * 0.42f) + shakeOffset

        // Render Knight Silhouette - Player 1
        drawKnightFigure(
            center = p1Pos,
            scale = 1.0f,
            isPlayer = true,
            guardDir = duelState.player1.guardDirection,
            stance = duelState.player1.stance,
            finishColor = duelState.player1.finish.getPrimaryColor()
        )

        // Render Knight Silhouette - Player 2 / Opponent
        drawKnightFigure(
            center = p2Pos,
            scale = 0.78f, // Depth scaling for opponent
            isPlayer = false,
            guardDir = duelState.player2.guardDirection,
            stance = duelState.player2.stance,
            finishColor = duelState.player2.finish.getPrimaryColor()
        )

        // 3. Render Telegraphed Strike Arcs & Windup Trajectories
        if (duelState.activeAttackerId != null && duelState.attackDirection != null) {
            val isP1Attacking = duelState.activeAttackerId == "p1"
            val attackerPos = if (isP1Attacking) p1Pos else p2Pos
            val defenderPos = if (isP1Attacking) p2Pos else p1Pos
            val attackDir = duelState.attackDirection
            val progress = duelState.attackProgress

            val zoneColor = when (attackDir) {
                CombatDirection.TOP -> ShieldZoneTopColor
                CombatDirection.LEFT -> ShieldZoneLeftColor
                CombatDirection.RIGHT -> ShieldZoneRightColor
            }

            // Draw Windup Telegraph Arc
            val swingStart = attackerPos
            val swingEnd = Offset(
                x = attackerPos.x + (defenderPos.x - attackerPos.x) * progress,
                y = attackerPos.y + (defenderPos.y - attackerPos.y) * progress
            )

            // Weapon Motion Trail
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, zoneColor, Color.White),
                    start = swingStart,
                    end = swingEnd
                ),
                start = swingStart,
                end = swingEnd,
                strokeWidth = 14f * progress,
                cap = StrokeCap.Round
            )

            // Glowing Direction Indicator Arrow
            val arrowHead = swingEnd
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(zoneColor, zoneColor.copy(alpha = 0.3f), Color.Transparent),
                    center = arrowHead,
                    radius = 45f
                ),
                radius = 45f,
                center = arrowHead
            )
        }

        // 4. Render Parry / Block / Hit Collision Particle Effects
        val feedback = duelState.lastFeedbackEvent
        if (feedback != null && System.currentTimeMillis() - feedback.timestamp < 600L) {
            val clashCenter = Offset(width * 0.50f, height * 0.55f) + shakeOffset

            when (feedback.type) {
                FeedbackEvent.EventType.PARRY -> {
                    // Dramatic Golden Parry Spark Explosion
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, ParrySparkGold, Color.Transparent),
                            center = clashCenter,
                            radius = 120f
                        ),
                        radius = 120f,
                        center = clashCenter
                    )

                    // Spark Rays
                    for (p in particles) {
                        val rayEnd = Offset(
                            x = clashCenter.x + (p.first - 0.5f) * 220f,
                            y = clashCenter.y + (p.second - 0.5f) * 220f
                        )
                        drawLine(
                            color = ParrySparkGold,
                            start = clashCenter,
                            end = rayEnd,
                            strokeWidth = p.third / 3f
                        )
                    }
                }

                FeedbackEvent.EventType.BLOCK -> {
                    // Shield Block Shockwave Ring
                    drawCircle(
                        color = GoldPrimary,
                        radius = 80f,
                        center = clashCenter,
                        style = Stroke(width = 8f)
                    )
                }

                FeedbackEvent.EventType.HIT, FeedbackEvent.EventType.GUARD_BREAK -> {
                    // Blood Splatter & Red Impact Flash
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(CrimsonBright, HealthRed.copy(alpha = 0.5f), Color.Transparent),
                            center = clashCenter,
                            radius = 100f
                        ),
                        radius = 100f,
                        center = clashCenter
                    )
                }

                else -> {}
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKnightFigure(
    center: Offset,
    scale: Float,
    isPlayer: Boolean,
    guardDir: CombatDirection,
    stance: FighterStance,
    finishColor: Color
) {
    val bodyRadius = 32f * scale
    val headRadius = 18f * scale

    // Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.5f),
        radius = bodyRadius * 1.5f,
        center = Offset(center.x, center.y + 40f * scale)
    )

    // Steel Armor Torso
    drawCircle(
        color = finishColor,
        radius = bodyRadius,
        center = center
    )

    // Metallic Rim Highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = bodyRadius,
        center = center,
        style = Stroke(width = 3f * scale)
    )

    // Helmet
    val helmetCenter = Offset(center.x, center.y - 35f * scale)
    drawCircle(
        color = SteelDark,
        radius = headRadius,
        center = helmetCenter
    )

    // Visor Slot
    val visorY = helmetCenter.y
    drawLine(
        color = GoldPrimary,
        start = Offset(helmetCenter.x - headRadius * 0.6f, visorY),
        end = Offset(helmetCenter.x + headRadius * 0.6f, visorY),
        strokeWidth = 3f * scale
    )

    // Sword Stance Vector Position (Dynamic based on Guard Direction)
    val weaponLength = 70f * scale
    val weaponAngle = when (guardDir) {
        CombatDirection.TOP -> -90f // Pointing Straight Up
        CombatDirection.LEFT -> -150f // Pointing Upper Left
        CombatDirection.RIGHT -> -30f  // Pointing Upper Right
    }

    val rad = Math.toRadians(weaponAngle.toDouble())
    val swordTip = Offset(
        x = center.x + (weaponLength * kotlin.math.cos(rad)).toFloat(),
        y = center.y + (weaponLength * kotlin.math.sin(rad)).toFloat()
    )

    // Draw Blade
    drawLine(
        color = Color.White,
        start = center,
        end = swordTip,
        strokeWidth = 6f * scale,
        cap = StrokeCap.Round
    )

    // Draw Crossguard
    val guardAngle = weaponAngle + 90f
    val gRad = Math.toRadians(guardAngle.toDouble())
    val guardOffset = Offset(
        x = center.x + (20f * scale * kotlin.math.cos(rad)).toFloat(),
        y = center.y + (20f * scale * kotlin.math.sin(rad)).toFloat()
    )
    val g1 = Offset(
        x = guardOffset.x + (15f * scale * kotlin.math.cos(gRad)).toFloat(),
        y = guardOffset.y + (15f * scale * kotlin.math.sin(gRad)).toFloat()
    )
    val g2 = Offset(
        x = guardOffset.x - (15f * scale * kotlin.math.cos(gRad)).toFloat(),
        y = guardOffset.y - (15f * scale * kotlin.math.sin(gRad)).toFloat()
    )

    drawLine(
        color = GoldPrimary,
        start = g1,
        end = g2,
        strokeWidth = 4f * scale
    )
}
