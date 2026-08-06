package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM loadouts ORDER BY isDefault DESC, id ASC")
    fun getAllLoadouts(): Flow<List<LoadoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoadout(loadout: LoadoutEntity)

    @Query("DELETE FROM loadouts WHERE id = :id")
    suspend fun deleteLoadoutById(id: Int)

    @Query("SELECT * FROM duel_stats WHERE id = 1")
    fun getStats(): Flow<DuelStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: DuelStatsEntity)
}
