package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val loadouts: Flow<List<LoadoutEntity>> = gameDao.getAllLoadouts()
    val stats: Flow<DuelStatsEntity?> = gameDao.getStats()

    suspend fun saveLoadout(loadout: LoadoutEntity) {
        gameDao.insertLoadout(loadout)
    }

    suspend fun deleteLoadout(id: Int) {
        gameDao.deleteLoadoutById(id)
    }

    suspend fun recordMatch(won: Boolean, parries: Int, blocks: Int, executed: Boolean, currentStats: DuelStatsEntity?) {
        val oldStats = currentStats ?: DuelStatsEntity()
        val updated = oldStats.copy(
            wins = if (won) oldStats.wins + 1 else oldStats.wins,
            losses = if (!won) oldStats.losses + 1 else oldStats.losses,
            totalParries = oldStats.totalParries + parries,
            totalBlocks = oldStats.totalBlocks + blocks,
            executions = if (executed) oldStats.executions + 1 else oldStats.executions
        )
        gameDao.upsertStats(updated)
    }
}
