package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CombatDirection
import com.example.model.FighterState
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HealthRed
import com.example.ui.theme.ShieldZoneLeftColor
import com.example.ui.theme.ShieldZoneRightColor
import com.example.ui.theme.ShieldZoneTopColor
import com.example.ui.theme.StaminaGreen
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelMedium

@Composable
fun HealthStaminaBar(
    modifier: Modifier = Modifier,
    fighter: FighterState,
    isOpponent: Boolean = false
) {
    val animatedHp by animateFloatAsState(targetValue = fighter.hpPercentage, label = "hpAnim")
    val animatedStamina by animateFloatAsState(targetValue = fighter.staminaPercentage, label = "staminaAnim")

    val guardZoneColor = when (fighter.guardDirection) {
        CombatDirection.TOP -> ShieldZoneTopColor
        CombatDirection.LEFT -> ShieldZoneLeftColor
        CombatDirection.RIGHT -> ShieldZoneRightColor
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SteelDark.copy(alpha = 0.85f))
            .border(1.dp, SteelMedium, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .testTag(if (isOpponent) "opponent_status_bar" else "player_status_bar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Guard Stance Badge Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(guardZoneColor.copy(alpha = 0.25f))
                    .border(1.5.dp, guardZoneColor, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Guard Zone",
                    tint = guardZoneColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Bars Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fighter.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${fighter.currentHp.toInt()} / ${fighter.maxHp.toInt()} HP",
                        color = GoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Health Bar Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, SteelMedium, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedHp)
                            .background(HealthRed)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stamina Bar Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedStamina)
                            .background(StaminaGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Weapon Label
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = fighter.weapon.name,
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = fighter.guardDirection.label,
                    color = guardZoneColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
