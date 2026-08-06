package com.example.ui

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BluetoothConnectionState
import com.example.bluetooth.BluetoothGameManager
import com.example.bluetooth.BluetoothPacket
import com.example.data.AppDatabase
import com.example.data.DuelStatsEntity
import com.example.data.GameRepository
import com.example.data.LoadoutEntity
import com.example.engine.CombatEngine
import com.example.model.AiDifficulty
import com.example.model.Armor
import com.example.model.CombatDirection
import com.example.model.DuelState
import com.example.model.FighterState
import com.example.model.MatchType
import com.example.model.RoundState
import com.example.model.SteelFinish
import com.example.model.Weapon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = GameRepository(db.gameDao())
    val bluetoothManager = BluetoothGameManager(application)
    val combatEngine = CombatEngine()

    val duelState: StateFlow<DuelState> = combatEngine.duelState
    val loadouts: StateFlow<List<LoadoutEntity>> = MutableStateFlow(emptyList())
    val stats: StateFlow<DuelStatsEntity?> = repository.stats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(VibratorManager::class.java)
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Vibrator::class.java)
    }

    private var gameLoopJob: Job? = null
    private var lastRecordedRound = -1

    // Current Active Fighter Settings
    val selectedWeapon = MutableStateFlow(Weapon.LONGSWORD)
    val selectedArmor = MutableStateFlow(Armor.CHAINMAIL)
    val selectedFinish = MutableStateFlow(SteelFinish.POLISHED_STEEL)

    // Difficulty & Gameplay Speed / Camera Shake Settings
    val selectedDifficulty = MutableStateFlow(AiDifficulty.KNIGHT)
    val customGameSpeed = MutableStateFlow(1.0f)
    val customCameraShake = MutableStateFlow(1.0f)

    init {
        // Observe Room DB Loadouts
        viewModelScope.launch {
            repository.loadouts.collectLatest { list ->
                (loadouts as MutableStateFlow).value = list
            }
        }

        // Listen for Bluetooth packets
        viewModelScope.launch {
            bluetoothManager.receivedPacket.collectLatest { packet ->
                if (packet != null) {
                    handleIncomingBluetoothPacket(packet)
                }
            }
        }
    }

    fun setDifficultyPreset(difficulty: AiDifficulty) {
        selectedDifficulty.value = difficulty
        customGameSpeed.value = difficulty.speedFactor
        customCameraShake.value = difficulty.defaultShakeMultiplier
    }

    fun updateGameSpeed(speed: Float) {
        customGameSpeed.value = speed
        combatEngine.updateGameplaySettings(speed, customCameraShake.value)
    }

    fun updateCameraShake(shake: Float) {
        customCameraShake.value = shake
        combatEngine.updateGameplaySettings(customGameSpeed.value, shake)
    }

    fun startDuel(
        matchType: MatchType,
        difficulty: AiDifficulty = selectedDifficulty.value,
        speedMultiplier: Float = customGameSpeed.value,
        shakeMultiplier: Float = customCameraShake.value
    ) {
        gameLoopJob?.cancel()

        val player1 = FighterState(
            id = "p1",
            name = "Player 1",
            weapon = selectedWeapon.value,
            armor = selectedArmor.value,
            finish = selectedFinish.value
        )

        val player2 = FighterState(
            id = "p2",
            name = if (matchType == MatchType.SOLO_AI) difficulty.label else "Player 2",
            weapon = if (matchType == MatchType.SOLO_AI) Weapon.ALL_WEAPONS.random() else Weapon.BATTLEAXE,
            armor = Armor.ALL_ARMORS.random(),
            finish = SteelFinish.ALL_FINISHES.random()
        )

        combatEngine.startNewMatch(
            matchType = matchType,
            difficulty = difficulty,
            gameSpeedMultiplier = speedMultiplier,
            cameraShakeMultiplier = shakeMultiplier,
            player1Fighter = player1,
            player2Fighter = player2
        )
        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoopJob = viewModelScope.launch(Dispatchers.Default) {
            var lastTime = System.currentTimeMillis()

            // Countdown timer
            for (i in 3 downTo 1) {
                delay(1000L)
                combatEngine.decrementCountdown()
            }

            // 60 FPS Game Loop
            while (true) {
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now

                combatEngine.tick(delta)

                // Trigger Haptic Vibration on Clash
                val feedback = combatEngine.duelState.value.lastFeedbackEvent
                if (feedback != null && feedback.timestamp > now - 50L) {
                    triggerHapticFeedback(feedback.type.name)
                }

                // Auto-record match stats when round ends
                val currentRound = combatEngine.duelState.value.round
                if (combatEngine.duelState.value.roundState == RoundState.ROUND_OVER && lastRecordedRound != currentRound) {
                    lastRecordedRound = currentRound
                    val state = combatEngine.duelState.value
                    val p1Won = state.player1.isAlive
                    repository.recordMatch(
                        won = p1Won,
                        parries = state.player1.parriesExecuted,
                        blocks = 1,
                        executed = state.executionCamActive,
                        currentStats = stats.value
                    )
                }

                delay(16L) // ~60 Hz tick
            }
        }
    }

    fun onJoystickMoved(playerId: String, x: Float, y: Float) {
        val direction = CombatDirection.fromJoystickOffset(x, y) ?: return
        combatEngine.setPlayerGuard(playerId, direction)

        if (bluetoothManager.connectionState.value == BluetoothConnectionState.CONNECTED) {
            bluetoothManager.sendPacket(BluetoothPacket.guardChange(direction, playerId))
        }
    }

    fun onAttackButtonPressed(playerId: String) {
        val success = combatEngine.playerInitiateAttack(playerId)
        if (success) {
            triggerShortVibration()
            if (bluetoothManager.connectionState.value == BluetoothConnectionState.CONNECTED) {
                val dir = combatEngine.duelState.value.player1.guardDirection
                bluetoothManager.sendPacket(BluetoothPacket.attackStart(dir, playerId))
            }
        }
    }

    fun onParryButtonPressed(playerId: String) {
        combatEngine.playerAttemptParry(playerId)
        val dir = combatEngine.duelState.value.player1.guardDirection
        if (bluetoothManager.connectionState.value == BluetoothConnectionState.CONNECTED) {
            bluetoothManager.sendPacket(BluetoothPacket.parryAction(dir, playerId))
        }
    }

    private fun handleIncomingBluetoothPacket(packet: BluetoothPacket) {
        when (packet.type) {
            "GUARD_CHANGE" -> {
                val dir = packet.direction?.let { CombatDirection.valueOf(it) } ?: return
                combatEngine.setPlayerGuard("p2", dir)
            }
            "ATTACK_START" -> {
                combatEngine.playerInitiateAttack("p2")
            }
            "PARRY_ACTION" -> {
                combatEngine.playerAttemptParry("p2")
            }
        }
    }

    fun saveCustomLoadout(name: String) {
        viewModelScope.launch {
            val entity = LoadoutEntity(
                loadoutName = name,
                weaponId = selectedWeapon.value.id,
                armorId = selectedArmor.value.id,
                finishId = selectedFinish.value.id
            )
            repository.saveLoadout(entity)
        }
    }

    fun deleteLoadout(id: Int) {
        viewModelScope.launch {
            repository.deleteLoadout(id)
        }
    }

    private fun triggerHapticFeedback(eventType: String) {
        try {
            if (vibrator?.hasVibrator() == true) {
                when (eventType) {
                    "PARRY" -> vibrator.vibrate(VibrationEffect.createOneShot(120L, VibrationEffect.DEFAULT_AMPLITUDE))
                    "BLOCK" -> vibrator.vibrate(VibrationEffect.createOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE))
                    "HIT", "GUARD_BREAK" -> vibrator.vibrate(VibrationEffect.createOneShot(200L, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        } catch (e: Exception) {
            // Ignored if vibration not permitted
        }
    }

    private fun triggerShortVibration() {
        try {
            if (vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createOneShot(25L, 80))
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        bluetoothManager.disconnect()
    }
}
