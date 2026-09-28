package org.chornobyl.hamdash.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Singleton row (id is always 1) holding the latest known HF propagation snapshot.
 * Any field the data source did not report stays null; the UI must render that as
 * "NO DATA" rather than fabricating a number.
 */
@Entity(tableName = "propagation")
data class PropagationEntity(
    @PrimaryKey val id: Int = 1,
    val solarFluxIndex: Int?,
    val kIndex: Int?,
    val aIndex: Int?,
    val solarFlares: String?,
    val geomagneticState: String?,
    val source: String?,
    val lastUpdated: String?,
)
