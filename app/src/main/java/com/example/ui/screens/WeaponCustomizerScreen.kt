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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Armor
import com.example.model.SteelFinish
import com.example.model.Weapon
import com.example.ui.GameViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelLight
import com.example.ui.theme.SteelMedium

@Composable
fun WeaponCustomizerScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit
) {
    val activeWeapon by viewModel.selectedWeapon.collectAsState()
    val activeArmor by viewModel.selectedArmor.collectAsState()
    val activeFinish by viewModel.selectedFinish.collectAsState()

    var loadoutName by remember { mutableStateOf("My Knight Build") }
    var showSavedMessage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
            .testTag("weapon_customizer_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToMenu, modifier = Modifier.testTag("armory_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("WEAPON ARMORY & LOADOUTS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Weapon Selector
            Text("SELECT WEAPON", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            Weapon.ALL_WEAPONS.forEach { weapon ->
                val selected = weapon.id == activeWeapon.id
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) SteelMedium else SteelDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) GoldPrimary else SteelLight.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.selectedWeapon.value = weapon }
                        .testTag("weapon_item_${weapon.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(weapon.name, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${weapon.baseDamage} DMG", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                        Text(weapon.description, fontSize = 12.sp, color = SteelLight)

                        Spacer(modifier = Modifier.height(6.dp))

                        // Parry Window Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Parry Window: ${weapon.parryWindowMs}ms", fontSize = 11.sp, color = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Stamina Cost: ${weapon.staminaCost}", fontSize = 11.sp, color = SteelLight)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Armor Selector
            Text("SELECT ARMOR", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            Armor.ALL_ARMORS.forEach { armor ->
                val selected = armor.id == activeArmor.id
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (selected) SteelMedium else SteelDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) GoldPrimary else SteelLight.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.selectedArmor.value = armor }
                        .testTag("armor_item_${armor.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(armor.name, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("+${armor.hpBonus} HP", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                        Text(armor.description, fontSize = 12.sp, color = SteelLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Steel Finish Selector
            Text("STEEL FINISH & FINISHING", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SteelFinish.ALL_FINISHES.forEach { finish ->
                    val selected = finish.id == activeFinish.id
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(finish.getPrimaryColor())
                            .border(
                                width = if (selected) 3.dp else 1.dp,
                                color = if (selected) GoldPrimary else SteelLight,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectedFinish.value = finish }
                            .testTag("finish_item_${finish.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = finish.getAccentColor())
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Save Loadout Preset to Room DB
            Button(
                onClick = {
                    viewModel.saveCustomLoadout(loadoutName)
                    showSavedMessage = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_loadout_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVE LOADOUT PRESET TO ROOM DB", fontWeight = FontWeight.Bold)
            }

            if (showSavedMessage) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Preset saved successfully!", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
