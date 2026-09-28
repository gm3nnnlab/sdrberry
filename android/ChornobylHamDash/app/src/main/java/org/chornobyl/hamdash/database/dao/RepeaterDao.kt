package org.chornobyl.hamdash.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.chornobyl.hamdash.database.entity.RepeaterEntity

@Dao
interface RepeaterDao {
    @Query("SELECT * FROM repeaters ORDER BY name ASC")
    fun observeAll(): Flow<List<RepeaterEntity>>

    @Query("SELECT * FROM repeaters WHERE id = :id")
    suspend fun getById(id: String): RepeaterEntity?

    @Query("SELECT COUNT(*) FROM repeaters")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repeaters: List<RepeaterEntity>)

    @Update
    suspend fun update(repeater: RepeaterEntity)

    @Query("DELETE FROM repeaters")
    suspend fun clear()

    @Query("SELECT id FROM repeaters WHERE isFavorite = 1")
    suspend fun favoriteIds(): List<String>

    @Query("UPDATE repeaters SET isFavorite = 1 WHERE id IN (:ids)")
    suspend fun markFavorites(ids: List<String>)
}
