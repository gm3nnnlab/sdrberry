package org.chornobyl.hamdash.data.model

import kotlinx.serialization.Serializable

/**
 * The app's source-independent data shapes, also used for the bundled JSON assets.
 * Each remote source is converted into these by its own mapper.
 */
@Serializable
data class RepeaterDto(
    val id: String,
    val name: String,
    val callsign: String,
    val mode: String,
    val rxFrequencyMhz: Double,
    val txFrequencyMhz: Double,
    val offsetMhz: Double? = null,
    val toneCtcss: String? = null,
    val digitalMode: String? = null,
    val colorCode: Int? = null,
    val timeslot: Int? = null,
    val talkgroup: String? = null,
    val latitude: Double,
    val longitude: Double,
    val elevationM: Int? = null,
    val source: String,
    val lastUpdated: String,
)

@Serializable
data class BandDto(
    val id: String,
    val name: String,
    val freqRangeLow: Double,
    val freqRangeHigh: Double,
    val typicalModes: String,
    val purpose: String,
    val blurb: String,
)

@Serializable
data class DigitalModeDto(
    val id: String,
    val name: String,
    val description: String,
    val howItWorks: String,
    val useCases: String,
    val equipmentNeeded: String,
    val docUrl: String,
)

@Serializable
data class TalkgroupDto(
    val id: String,
    val tgId: Int,
    val name: String,
    val description: String,
    val source: String,
)

@Serializable
data class PropagationDto(
    val id: Int = 1,
    val solarFluxIndex: Int? = null,
    val kIndex: Int? = null,
    val aIndex: Int? = null,
    val solarFlares: String? = null,
    val geomagneticState: String? = null,
    val source: String? = null,
    val lastUpdated: String? = null,
)
