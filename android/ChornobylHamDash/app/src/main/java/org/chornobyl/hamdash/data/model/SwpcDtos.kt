package org.chornobyl.hamdash.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** products/summary/10cm-flux.json — latest observed 10.7 cm (2800 MHz) solar flux, in SFU. */
@Serializable
data class SwpcFluxDto(
    val flux: Double? = null,
    @SerialName("time_tag") val timeTag: String? = null,
)

/** products/noaa-planetary-k-index.json — one row per 3-hour interval, UTC. */
@Serializable
data class SwpcKpDto(
    @SerialName("time_tag") val timeTag: String? = null,
    @SerialName("Kp") val kp: Double? = null,
    @SerialName("a_running") val aRunning: Int? = null,
)

/** json/goes/primary/xray-flares-7-day.json — GOES X-ray flare events. */
@Serializable
data class SwpcFlareDto(
    @SerialName("max_time") val maxTime: String? = null,
    @SerialName("max_class") val maxClass: String? = null,
    @SerialName("max_xrlong") val maxXrLong: Double? = null,
)
