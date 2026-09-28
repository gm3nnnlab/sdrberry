package org.chornobyl.hamdash.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** General amateur-band reference row. Informational only — see UI disclaimer. */
@Entity(tableName = "bands")
data class BandEntity(
    @PrimaryKey val id: String,
    val name: String,
    val freqRangeLow: Double,
    val freqRangeHigh: Double,
    val typicalModes: String,
    val purpose: String,
    val blurb: String,
)
