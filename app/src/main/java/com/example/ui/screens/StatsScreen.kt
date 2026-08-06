package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GameViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelLight
import com.example.ui.theme.SteelMedium

@Composable
fun StatsScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val loadouts by viewModel.loadouts.collectAsState()

    val totalMatches = (stats?.wins ?: 0) + (stats?.losses ?: 0)
    val winRate = if (totalMatches > 0) ((stats?.wins ?: 0).toFloat() / totalMatches.toFloat() * 100f).toInt() else 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
            .testTag("stats_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToMenu, modifier = Modifier.testTag("stats_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("COMBAT RECORDS & STATS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stat Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBox(
                    label = "VICTORIES",
                    value = "${stats?.wins ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatBox(
                    label = "DEFEATS",
                    value = "${stats?.losses ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatBox(
                    label = "WIN RATE",
                    value = "$winRate%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBox(
                    label = "PARRIES",
                    value = "${stats?.totalParries ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatBox(
                    label = "BLOCKS",
                    value = "${stats?.totalBlocks ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatBox(
                    label = "EXECUTIONS",
                    value = "${stats?.executions ?: 0}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("SAVED KNIGHT LOADOUTS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(loadouts) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SteelMedium),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("saved_loadout_item")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.loadoutName, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Weapon: ${item.weaponId.uppercase()} | Armor: ${item.armorId.uppercase()}", fontSize = 11.sp, color = SteelLight)
                            }

                            IconButton(onClick = { viewModel.deleteLoadout(item.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SteelDark)
            .border(1.dp, SteelMedium, RoundedCornerShape(10.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = SteelLight, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
        }
    }
}
