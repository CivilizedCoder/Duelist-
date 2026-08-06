package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DuelState
import com.example.model.FeedbackEvent
import com.example.model.RoundState
import com.example.ui.GameViewModel
import com.example.ui.components.DirectionalJoystick
import com.example.ui.components.HealthStaminaBar
import com.example.ui.components.VisualArenaCanvas
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BronzeAccent
import com.example.ui.theme.CrimsonBright
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HealthRed
import com.example.ui.theme.ParrySparkGold
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelMedium

@Composable
fun CombatScreen(
    viewModel: GameViewModel,
    duelState: DuelState,
    onBackToMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("combat_screen")
    ) {
        // 1. Center Visual Arena Canvas
        VisualArenaCanvas(
            modifier = Modifier.fillMaxSize(),
            duelState = duelState
        )

        // 2. Top Header HUD (Opponent Status & Player Status)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToMenu,
                    modifier = Modifier.testTag("back_to_menu_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Exit Duel", tint = GoldPrimary)
                }

                Text(
                    text = "ROUND ${duelState.round}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )

                Text(
                    text = "${duelState.p1Wins} - ${duelState.p2Wins}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Active Gameplay Settings Badge
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SteelDark.copy(alpha = 0.85f))
                        .border(0.5.dp, BronzeAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${duelState.aiDifficulty.name} | ${(duelState.gameSpeedMultiplier * 100).toInt()}% SPEED | ${(duelState.cameraShakeMultiplier * 100).toInt()}% SHAKE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }
            }

            // Opponent Top Status Bar
            HealthStaminaBar(
                fighter = duelState.player2,
                isOpponent = true
            )

            // Player 1 Top Status Bar
            HealthStaminaBar(
                fighter = duelState.player1,
                isOpponent = false
            )
        }

        // 3. Countdown Overlay
        if (duelState.roundState == RoundState.COUNTDOWN) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PREPARE YOUR GUARD!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${duelState.countdownSeconds}",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        // 4. Center Feedback Event Banner ("PERFECT PARRY!", "BLOCKED!", "GUARD BROKEN!")
        val feedback = duelState.lastFeedbackEvent
        AnimatedVisibility(
            visible = feedback != null && System.currentTimeMillis() - feedback.timestamp < 1200L,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (feedback != null) {
                val bannerColor = when (feedback.type) {
                    FeedbackEvent.EventType.PARRY -> ParrySparkGold
                    FeedbackEvent.EventType.BLOCK -> GoldPrimary
                    FeedbackEvent.EventType.HIT -> CrimsonBright
                    FeedbackEvent.EventType.GUARD_BREAK -> HealthRed
                    else -> GoldPrimary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SteelDark.copy(alpha = 0.90f))
                        .border(2.dp, bannerColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .testTag("feedback_banner")
                ) {
                    Text(
                        text = feedback.text,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = bannerColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 5. Bottom Touch Control Panel (Directional Joystick + Action Buttons)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left: 120-Degree Directional Joystick
                DirectionalJoystick(
                    size = 150.dp,
                    activeDirection = duelState.player1.guardDirection,
                    onJoystickMoved = { x, y ->
                        viewModel.onJoystickMoved("p1", x, y)
                    },
                    onJoystickReleased = {}
                )

                // Right: Dual Action Buttons (Attack & Parry)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Timed Parry Button
                    Button(
                        onClick = { viewModel.onParryButtonPressed("p1") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (duelState.parryWindowActive) ParrySparkGold else SteelMedium,
                            contentColor = if (duelState.parryWindowActive) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(width = 130.dp, height = 54.dp)
                            .testTag("parry_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "PARRY", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Attack / Strike Button
                    Button(
                        onClick = { viewModel.onAttackButtonPressed("p1") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(width = 130.dp, height = 58.dp)
                            .testTag("attack_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "STRIKE", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // 6. Round Over / Match Victory Overlay
        if (duelState.roundState == RoundState.ROUND_OVER) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.80f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "VICTORY DECLARED!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${duelState.winnerName} Wins!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.startDuel(duelState.matchType, duelState.aiDifficulty)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                            .testTag("rematch_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "REMATCH DUEL", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onBackToMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = SteelMedium),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                            .testTag("return_main_menu_button")
                    ) {
                        Text(text = "MAIN MENU", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
