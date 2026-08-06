package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DuelState
import com.example.model.MatchType
import com.example.ui.GameViewModel
import com.example.ui.components.DirectionalJoystick
import com.example.ui.components.HealthStaminaBar
import com.example.ui.components.VisualArenaCanvas
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ParrySparkGold
import com.example.ui.theme.SteelMedium

@Composable
fun LocalMultiplayerScreen(
    viewModel: GameViewModel,
    duelState: DuelState,
    onBackToMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("local_multiplayer_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Player 2 Controls (Top Half - Rotated 180° for Face-to-Face Same-Device Duel!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .rotate(180f)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HealthStaminaBar(fighter = duelState.player2, isOpponent = true)

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        DirectionalJoystick(
                            size = 120.dp,
                            activeDirection = duelState.player2.guardDirection,
                            onJoystickMoved = { x, y -> viewModel.onJoystickMoved("p2", x, y) },
                            onJoystickReleased = {}
                        )

                        Row {
                            Button(
                                onClick = { viewModel.onParryButtonPressed("p2") },
                                colors = ButtonDefaults.buttonColors(containerColor = ParrySparkGold, contentColor = Color.Black),
                                modifier = Modifier
                                    .size(width = 80.dp, height = 48.dp)
                                    .testTag("p2_parry_button")
                            ) {
                                Text(text = "PARRY", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.onAttackButtonPressed("p2") },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                modifier = Modifier
                                    .size(width = 80.dp, height = 48.dp)
                                    .testTag("p2_attack_button")
                            ) {
                                Text(text = "STRIKE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Center Visual Battlefield
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                VisualArenaCanvas(
                    modifier = Modifier.fillMaxSize(),
                    duelState = duelState
                )

                IconButton(
                    onClick = onBackToMenu,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .testTag("local_2p_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Exit", tint = GoldPrimary)
                }
            }

            // Player 1 Controls (Bottom Half)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HealthStaminaBar(fighter = duelState.player1, isOpponent = false)

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        DirectionalJoystick(
                            size = 120.dp,
                            activeDirection = duelState.player1.guardDirection,
                            onJoystickMoved = { x, y -> viewModel.onJoystickMoved("p1", x, y) },
                            onJoystickReleased = {}
                        )

                        Row {
                            Button(
                                onClick = { viewModel.onParryButtonPressed("p1") },
                                colors = ButtonDefaults.buttonColors(containerColor = ParrySparkGold, contentColor = Color.Black),
                                modifier = Modifier
                                    .size(width = 80.dp, height = 48.dp)
                                    .testTag("p1_parry_button")
                            ) {
                                Text(text = "PARRY", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.onAttackButtonPressed("p1") },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                modifier = Modifier
                                    .size(width = 80.dp, height = 48.dp)
                                    .testTag("p1_attack_button")
                            ) {
                                Text(text = "STRIKE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
