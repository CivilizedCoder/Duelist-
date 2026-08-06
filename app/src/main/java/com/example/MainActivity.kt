package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.model.AiDifficulty
import com.example.model.MatchType
import com.example.ui.GameViewModel
import com.example.ui.screens.BluetoothScreen
import com.example.ui.screens.CombatScreen
import com.example.ui.screens.LocalMultiplayerScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.WeaponCustomizerScreen
import com.example.ui.theme.IronAndSteelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IronAndSteelTheme {
                MainAppNavHost()
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()
    val duelState by viewModel.duelState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main_menu",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main_menu") {
                val difficulty by viewModel.selectedDifficulty.collectAsState()
                val speed by viewModel.customGameSpeed.collectAsState()
                val shake by viewModel.customCameraShake.collectAsState()

                MainMenuScreen(
                    selectedDifficulty = difficulty,
                    customGameSpeed = speed,
                    customCameraShake = shake,
                    onSelectDifficulty = { viewModel.setDifficultyPreset(it) },
                    onChangeSpeed = { viewModel.updateGameSpeed(it) },
                    onChangeShake = { viewModel.updateCameraShake(it) },
                    onStartSolo = { selectedDiff ->
                        viewModel.startDuel(
                            matchType = MatchType.SOLO_AI,
                            difficulty = selectedDiff,
                            speedMultiplier = speed,
                            shakeMultiplier = shake
                        )
                        navController.navigate("combat")
                    },
                    onStartLocal2P = {
                        viewModel.startDuel(MatchType.LOCAL_2P)
                        navController.navigate("local_2p")
                    },
                    onStartBluetooth = {
                        navController.navigate("bluetooth")
                    },
                    onOpenArmory = {
                        navController.navigate("armory")
                    },
                    onOpenStats = {
                        navController.navigate("stats")
                    }
                )
            }

            composable("combat") {
                CombatScreen(
                    viewModel = viewModel,
                    duelState = duelState,
                    onBackToMenu = {
                        navController.popBackStack("main_menu", false)
                    }
                )
            }

            composable("local_2p") {
                LocalMultiplayerScreen(
                    viewModel = viewModel,
                    duelState = duelState,
                    onBackToMenu = {
                        navController.popBackStack("main_menu", false)
                    }
                )
            }

            composable("bluetooth") {
                BluetoothScreen(
                    viewModel = viewModel,
                    onStartBluetoothMatch = {
                        navController.navigate("combat")
                    },
                    onBackToMenu = {
                        navController.popBackStack("main_menu", false)
                    }
                )
            }

            composable("armory") {
                WeaponCustomizerScreen(
                    viewModel = viewModel,
                    onBackToMenu = {
                        navController.popBackStack()
                    }
                )
            }

            composable("stats") {
                StatsScreen(
                    viewModel = viewModel,
                    onBackToMenu = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
