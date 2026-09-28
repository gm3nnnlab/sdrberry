package org.chornobyl.hamdash.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.chornobyl.hamdash.data.AssetDataSource
import org.chornobyl.hamdash.data.LocalDataSource
import org.chornobyl.hamdash.data.RemoteDataSource
import org.chornobyl.hamdash.data.model.toEntity
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.SyncKeys
import org.chornobyl.hamdash.database.entity.TalkgroupEntity

class RadioDataRepositoryImpl(
    private val local: LocalDataSource,
    private val remote: RemoteDataSource,
    private val assets: AssetDataSource,
) : RadioDataRepository {

    override fun observeRepeaters(): Flow<List<RepeaterEntity>> = local.observeRepeaters()
    override fun observeBands(): Flow<List<BandEntity>> = local.observeBands()
    override fun observeDigitalModes(): Flow<List<DigitalModeEntity>> = local.observeDigitalModes()
    override fun observeTalkgroups(): Flow<List<TalkgroupEntity>> = local.observeTalkgroups()
    override fun searchTalkgroups(query: String): Flow<List<TalkgroupEntity>> =
        if (query.isBlank()) local.observeTalkgroups() else local.searchTalkgroups(query)
    override fun observePropagation(): Flow<PropagationEntity?> = local.observePropagation()

    override fun observeLastSync(): Flow<Long?> =
        combine(
            local.observeSync(SyncKeys.REPEATERS),
            local.observeSync(SyncKeys.BANDS),
            local.observeSync(SyncKeys.DIGITAL_MODES),
            local.observeSync(SyncKeys.TALKGROUPS),
            local.observeSync(SyncKeys.PROPAGATION),
        ) { rows -> rows.mapNotNull { it?.lastSyncEpochMillis }.maxOrNull() }

    /** First-run bootstrap: if Room is empty, seed it from the bundled mock JSON. */
    override suspend fun ensureSeeded() {
        if (local.repeaterCount() == 0) {
            local.replaceRepeaters(assets.loadRepeaters().map { it.toEntity() })
        }
        if (local.bandCount() == 0) {
            local.replaceBands(assets.loadBands().map { it.toEntity() })
        }
        if (local.digitalModeCount() == 0) {
            local.replaceDigitalModes(assets.loadDigitalModes().map { it.toEntity() })
        }
        if (local.talkgroupCount() == 0) {
            local.replaceTalkgroups(assets.loadTalkgroups().map { it.toEntity() })
        }
    }

    /**
     * Try the remote source first; on any success, validate (non-empty) and persist.
     * On failure or an empty/invalid payload, leave the existing local data untouched
     * and report that this refresh did not update anything.
     */
    override suspend fun refresh(): Boolean {
        var anySucceeded = false
        val now = System.currentTimeMillis()

        remote.fetchRepeaters().onSuccess { dtos ->
            if (dtos.isNotEmpty()) {
                val favorites = dtos.associate { it.id to (local.getRepeater(it.id)?.isFavorite ?: false) }
                local.replaceRepeaters(dtos.map { it.toEntity(favorites[it.id] ?: false) })
                local.markSynced(SyncKeys.REPEATERS, now)
                anySucceeded = true
            }
        }
        remote.fetchBands().onSuccess { dtos ->
            if (dtos.isNotEmpty()) {
                local.replaceBands(dtos.map { it.toEntity() })
                local.markSynced(SyncKeys.BANDS, now)
                anySucceeded = true
            }
        }
        remote.fetchDigitalModes().onSuccess { dtos ->
            if (dtos.isNotEmpty()) {
                local.replaceDigitalModes(dtos.map { it.toEntity() })
                local.markSynced(SyncKeys.DIGITAL_MODES, now)
                anySucceeded = true
            }
        }
        remote.fetchTalkgroups().onSuccess { dtos ->
            if (dtos.isNotEmpty()) {
                local.replaceTalkgroups(dtos.map { it.toEntity() })
                local.markSynced(SyncKeys.TALKGROUPS, now)
                anySucceeded = true
            }
        }
        remote.fetchPropagation().onSuccess { dto ->
            local.replacePropagation(dto.toEntity())
            local.markSynced(SyncKeys.PROPAGATION, now)
            anySucceeded = true
        }

        return anySucceeded
    }

    override suspend fun toggleFavorite(repeaterId: String) {
        val repeater = local.getRepeater(repeaterId) ?: return
        local.setFavorite(repeater, !repeater.isFavorite)
    }

    override suspend fun clearCache() {
        val favoriteIds = local.favoriteRepeaterIds()
        local.clearAll()
        ensureSeeded()
        if (favoriteIds.isNotEmpty()) local.markFavorites(favoriteIds)
    }
}
