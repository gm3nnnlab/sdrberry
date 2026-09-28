package org.chornobyl.hamdash.data.model

import kotlinx.serialization.Serializable

/**
 * One entry of https://hearham.com/api/repeaters/v1. `frequency` is the repeater's
 * output in Hz; `offset` (Hz) takes it to the repeater's input. `encode` holds the
 * CTCSS tone to send for analog modes, or "CCn" for DMR.
 */
@Serializable
data class HearhamRepeaterDto(
    val id: Long,
    val callsign: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val city: String? = null,
    val mode: String? = null,
    val encode: String? = null,
    val frequency: Long? = null,
    val offset: Long? = null,
    val operational: Int? = null,
)
