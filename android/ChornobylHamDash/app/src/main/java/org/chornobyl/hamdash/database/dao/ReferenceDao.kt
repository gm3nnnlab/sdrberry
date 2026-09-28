package org.chornobyl.hamdash.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.SyncMetadataEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity

@Dao
interface BandDao {
    @Query("SELECT * FROM bands ORDER BY freqRangeLow ASC")
    fun observeAll(): Flow<List<BandEntity>>

    @Query("SELECT COUNT(*) FROM bands")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bands: List<BandEntity>)

    @Query("DELETE FROM bands")
    suspend fun clear()
}

@Dao
interface DigitalModeDao {
    @Query("SELECT * FROM digital_modes ORDER BY name ASC")
    fun observeAll(): Flow<List<DigitalModeEntity>>

    @Query("SELECT COUNT(*) FROM digital_modes")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modes: List<DigitalModeEntity>)

    @Query("DELETE FROM digital_modes")
    suspend fun clear()
}

@Dao
interface TalkgroupDao {
    @Query("SELECT * FROM talkgroups ORDER BY tgId ASC")
    fun observeAll(): Flow<List<TalkgroupEntity>>

    @Query(
        "SELECT * FROM talkgroups WHERE name LIKE '%' || :query || '%' " +
            "OR description LIKE '%' || :query || '%' OR CAST(tgId AS TEXT) LIKE '%' || :query || '%' " +
            "ORDER BY tgId ASC"
    )
    fun search(query: String): Flow<List<TalkgroupEntity>>

    @Query("SELECT COUNT(*) FROM talkgroups")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(talkgroups: List<TalkgroupEntity>)

    @Query("DELETE FROM talkgroups")
    suspend fun clear()
}

@Dao
interface PropagationDao {
    @Query("SELECT * FROM propagation WHERE id = 1")
    fun observe(): Flow<PropagationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(propagation: PropagationEntity)

    @Query("DELETE FROM propagation")
    suspend fun clear()
}

@Dao
interface SyncMetadataDao {
    @Query("SELECT * FROM sync_metadata WHERE key = :key")
    fun observe(key: String): Flow<SyncMetadataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SyncMetadataEntity)

    @Query("DELETE FROM sync_metadata")
    suspend fun clear()
}
