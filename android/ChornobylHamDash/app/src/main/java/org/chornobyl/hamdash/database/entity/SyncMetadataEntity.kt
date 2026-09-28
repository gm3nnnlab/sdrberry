package org.chornobyl.hamdash.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val key: String,
    val lastSyncEpochMillis: Long,
)

object SyncKeys {
    const val REPEATERS = "repeaters"
    const val BANDS = "bands"
    const val DIGITAL_MODES = "digital_modes"
    const val TALKGROUPS = "talkgroups"
    const val PROPAGATION = "propagation"
}
