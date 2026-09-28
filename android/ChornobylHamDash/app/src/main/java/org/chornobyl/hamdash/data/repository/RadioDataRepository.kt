package org.chornobyl.hamdash.data.repository

import kotlinx.coroutines.flow.Flow
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity

/**
 * Single entry point the UI/ViewModels use for all amateur-radio reference data.
 * Not hard-coded to one API: it tries [org.chornobyl.hamdash.data.RemoteDataSource]
 * first, validates and persists a successful response to Room, and otherwise falls
 * back to whatever is already in [org.chornobyl.hamdash.data.LocalDataSource] (seeded
 * from bundled assets on first run). The last successful sync time is exposed to the UI.
 */
interface RadioDataRepository {
    fun observeRepeaters(): Flow<List<RepeaterEntity>>
    fun observeBands(): Flow<List<BandEntity>>
    fun observeDigitalModes(): Flow<List<DigitalModeEntity>>
    fun observeTalkgroups(): Flow<List<TalkgroupEntity>>
    fun searchTalkgroups(query: String): Flow<List<TalkgroupEntity>>
    fun observePropagation(): Flow<PropagationEntity?>
    fun observeLastSync(): Flow<Long?>

    suspend fun ensureSeeded()
    suspend fun refresh(): Boolean
    suspend fun toggleFavorite(repeaterId: String)
}
