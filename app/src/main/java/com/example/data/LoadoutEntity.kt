package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loadouts")
data class LoadoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val loadoutName: String,
    val weaponId: String,
    val armorId: String,
    val finishId: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "duel_stats")
data class DuelStatsEntity(
    @PrimaryKey val id: Int = 1,
    val wins: Int = 0,
    val losses: Int = 0,
    val totalParries: Int = 0,
    val totalBlocks: Int = 0,
    val executions: Int = 0
)
