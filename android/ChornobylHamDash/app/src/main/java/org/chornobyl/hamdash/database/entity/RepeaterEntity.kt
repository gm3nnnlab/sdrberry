package org.chornobyl.hamdash.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single publicly documented amateur repeater. Every field mirrors what the data
 * source actually published — never populate a field with a guessed/invented value;
 * leave it null and let the UI omit it instead.
 */
@Entity(tableName = "repeaters")
data class RepeaterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val callsign: String,
    val mode: String, // RepeaterMode.name
    val rxFrequencyMhz: Double,
    val txFrequencyMhz: Double,
    val offsetMhz: Double?,
    val toneCtcss: String?,
    val digitalMode: String?,
    val colorCode: Int?,
    val timeslot: Int?,
    val talkgroup: String?,
    val latitude: Double,
    val longitude: Double,
    val elevationM: Int?,
    val source: String,
    val lastUpdated: String,
    val isFavorite: Boolean = false,
)
