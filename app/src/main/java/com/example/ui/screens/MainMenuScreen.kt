package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiDifficulty
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BronzeAccent
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelLight
import com.example.ui.theme.SteelMedium

@Composable
fun MainMenuScreen(
    selectedDifficulty: AiDifficulty,
    customGameSpeed: Float,
    customCameraShake: Float,
    onSelectDifficulty: (AiDifficulty) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onChangeShake: (Float) -> Unit,
    onStartSolo: (AiDifficulty) -> Unit,
    onStartLocal2P: () -> Unit,
    onStartBluetooth: () -> Unit,
    onOpenArmory: () -> Unit,
    onOpenStats: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        SteelDark,
                        Color(0xFF1D1414),
                        BackgroundDark
                    )
                )
            )
            .padding(20.dp)
            .testTag("main_menu_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Emblem Banner Box
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SteelMedium)
                    .border(2.dp, GoldPrimary, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Iron & Steel Logo",
                    tint = GoldPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "IRON & STEEL",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = GoldPrimary,
                letterSpacing = 2.sp
            )

            Text(
                text = "DIRECTIONAL COMBAT DUEL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldLight.copy(alpha = 0.8f),
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Move joystick to matching 120° zone to block or parry enemy swings!",
                fontSize = 12.sp,
                color = SteelLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Difficulty & Gameplay Settings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SteelDark)
                    .border(1.dp, BronzeAccent, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DIFFICULTY & GAMEPLAY SETTINGS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${(customGameSpeed * 100).toInt()}% SPEED | ${(customCameraShake * 100).toInt()}% SHAKE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // AI Difficulty Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AiDifficulty.values().forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CrimsonRed else SteelMedium)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.5.dp,
                                        color = if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectDifficulty(diff) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = diff.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else SteelLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedDifficulty.description,
                        fontSize = 11.sp,
                        color = GoldLight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Speed Multiplier Selector
                    Text(
                        text = "COMBAT SPEED MULTIPLIER:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SteelLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.75f, 1.0f, 1.25f, 1.50f).forEach { speed ->
                            val isSelected = kotlin.math.abs(customGameSpeed - speed) < 0.05f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) GoldPrimary else SteelMedium)
                                    .clickable { onChangeSpeed(speed) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${speed}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Camera Shake Intensity Selector
                    Text(
                        text = "CAMERA SHAKE INTENSITY:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SteelLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            0.0f to "OFF",
                            0.5f to "50%",
                            1.0f to "100%",
                            1.5f to "150%",
                            2.0f to "200%"
                        ).forEach { (shakeVal, label) ->
                            val isSelected = kotlin.math.abs(customCameraShake - shakeVal) < 0.05f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) GoldPrimary else SteelMedium)
                                    .clickable { onChangeShake(shakeVal) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mode Selection Buttons
            MenuButton(
                text = "SOLO CAMPAIGN (${selectedDifficulty.name})",
                icon = Icons.Default.SportsKabaddi,
                testTag = "solo_ai_button",
                onClick = { onStartSolo(selectedDifficulty) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuButton(
                text = "LOCAL 2-PLAYER DUEL",
                icon = Icons.Default.Groups,
                testTag = "local_2p_button",
                onClick = onStartLocal2P
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuButton(
                text = "BLUETOOTH P2P DUEL",
                icon = Icons.Default.Bluetooth,
                testTag = "bluetooth_p2p_button",
                onClick = onStartBluetooth
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onOpenArmory,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("armory_button")
                ) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ARMORY", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = onOpenStats,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("stats_button")
                ) {
                    Icon(imageVector = Icons.Default.QueryStats, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "RECORDS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = CrimsonRed,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
        }
    }
}
