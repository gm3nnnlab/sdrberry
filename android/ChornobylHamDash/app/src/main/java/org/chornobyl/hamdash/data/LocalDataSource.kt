package org.chornobyl.hamdash.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import org.chornobyl.hamdash.database.HamDashDatabase
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.SyncMetadataEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity

/** Room-backed local data source. The single source of truth the UI reads from. */
class LocalDataSource(private val db: HamDashDatabase) {
    fun observeRepeaters(): Flow<List<RepeaterEntity>> = db.repeaterDao().observeAll()
    fun observeBands(): Flow<List<BandEntity>> = db.bandDao().observeAll()
    fun observeDigitalModes(): Flow<List<DigitalModeEntity>> = db.digitalModeDao().observeAll()
    fun observeTalkgroups(): Flow<List<TalkgroupEntity>> = db.talkgroupDao().observeAll()
    fun searchTalkgroups(query: String): Flow<List<TalkgroupEntity>> = db.talkgroupDao().search(query)
    fun observePropagation(): Flow<PropagationEntity?> = db.propagationDao().observe()
    fun observeSync(key: String): Flow<SyncMetadataEntity?> = db.syncMetadataDao().observe(key)

    suspend fun repeaterCount(): Int = db.repeaterDao().count()
    suspend fun bandCount(): Int = db.bandDao().count()
    suspend fun digitalModeCount(): Int = db.digitalModeDao().count()
    suspend fun talkgroupCount(): Int = db.talkgroupDao().count()

    suspend fun replaceRepeaters(repeaters: List<RepeaterEntity>) = db.withTransaction {
        db.repeaterDao().clear()
        db.repeaterDao().insertAll(repeaters)
    }

    suspend fun replaceBands(bands: List<BandEntity>) = db.withTransaction {
        db.bandDao().clear()
        db.bandDao().insertAll(bands)
    }

    suspend fun replaceDigitalModes(modes: List<DigitalModeEntity>) = db.withTransaction {
        db.digitalModeDao().clear()
        db.digitalModeDao().insertAll(modes)
    }

    suspend fun replaceTalkgroups(talkgroups: List<TalkgroupEntity>) = db.withTransaction {
        db.talkgroupDao().clear()
        db.talkgroupDao().insertAll(talkgroups)
    }

    suspend fun replacePropagation(propagation: PropagationEntity) = db.propagationDao().upsert(propagation)

    suspend fun favoriteRepeaterIds(): List<String> = db.repeaterDao().favoriteIds()
    suspend fun markFavorites(ids: List<String>) = db.repeaterDao().markFavorites(ids)

    suspend fun getRepeater(id: String): RepeaterEntity? = db.repeaterDao().getById(id)
    suspend fun setFavorite(repeater: RepeaterEntity, favorite: Boolean) =
        db.repeaterDao().update(repeater.copy(isFavorite = favorite))

    suspend fun lastSynced(key: String): Long? = db.syncMetadataDao().get(key)?.lastSyncEpochMillis

    suspend fun markSynced(key: String, epochMillis: Long) =
        db.syncMetadataDao().upsert(SyncMetadataEntity(key, epochMillis))

    suspend fun clearAll() = db.withTransaction {
        db.repeaterDao().clear()
        db.bandDao().clear()
        db.digitalModeDao().clear()
        db.talkgroupDao().clear()
        db.propagationDao().clear()
        db.syncMetadataDao().clear()
    }
}
