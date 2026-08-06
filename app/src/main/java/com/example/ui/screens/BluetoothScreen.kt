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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.bluetooth.BluetoothConnectionState
import com.example.model.MatchType
import com.example.ui.GameViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelLight
import com.example.ui.theme.SteelMedium

@Composable
fun BluetoothScreen(
    viewModel: GameViewModel,
    onStartBluetoothMatch: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val manager = viewModel.bluetoothManager
    val connState by manager.connectionState.collectAsState()
    val devices by manager.discoveredDevices.collectAsState()

    LaunchedEffect(Unit) {
        manager.refreshPairedDevices()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
            .testTag("bluetooth_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackToMenu, modifier = Modifier.testTag("bt_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BLUETOOTH DUEL ROOM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connection Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SteelDark)
                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bluetooth,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "STATUS", fontSize = 11.sp, color = SteelLight, fontWeight = FontWeight.Bold)
                        Text(
                            text = connState.name.replace("_", " "),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { manager.startHosting() },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("bt_host_button")
                ) {
                    Text("HOST DUEL", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { manager.refreshPairedDevices() },
                    colors = ButtonDefaults.buttonColors(containerColor = SteelMedium),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("bt_refresh_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SCAN DEVICES", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("PAIRED KNIGHT DEVICES", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(devices) { dev ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SteelMedium),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                manager.connectToDevice(dev.address)
                            }
                            .testTag("bt_device_item")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(dev.name, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(dev.address, fontSize = 11.sp, color = SteelLight)
                            }
                            Text("CONNECT", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulated P2P Mode Button (Guarantees playability even without physical 2nd phone)
            Button(
                onClick = {
                    manager.startSimulatedPeer()
                    viewModel.startDuel(MatchType.BLUETOOTH_P2P)
                    onStartBluetoothMatch()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("bt_simulated_peer_button")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ENTER BLUETOOTH DUEL MATCH", fontWeight = FontWeight.Black)
            }
        }
    }
}
