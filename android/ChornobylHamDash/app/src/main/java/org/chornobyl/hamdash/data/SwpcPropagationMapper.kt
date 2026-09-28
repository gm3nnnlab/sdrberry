package org.chornobyl.hamdash.data

import org.chornobyl.hamdash.data.model.PropagationDto
import org.chornobyl.hamdash.data.model.SwpcFlareDto
import org.chornobyl.hamdash.data.model.SwpcFluxDto
import org.chornobyl.hamdash.data.model.SwpcKpDto
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Turns the raw NOAA SWPC feeds into one propagation snapshot. A null feed means that
 * request failed; its fields stay null so the UI shows NO DATA instead of a guess.
 */
object SwpcPropagationMapper {
    const val SOURCE = "NOAA Space Weather Prediction Center (services.swpc.noaa.gov)"

    private val FLARE_WINDOW: Duration = Duration.ofHours(24)
    private val TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'").withZone(ZoneOffset.UTC)
    private val CLOCK = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC)

    fun map(
        flux: List<SwpcFluxDto>?,
        kp: List<SwpcKpDto>?,
        flares: List<SwpcFlareDto>?,
        now: Instant,
    ): PropagationDto? {
        if (flux == null && kp == null && flares == null) return null

        val latestFlux = flux.orEmpty()
            .mapNotNull { dto -> dto.flux?.takeIf { it > 0 }?.let { value -> parseUtc(dto.timeTag)?.let { it to value } } }
            .maxByOrNull { it.first }
        val latestKp = kp.orEmpty()
            .mapNotNull { dto -> dto.kp?.takeIf { it >= 0 }?.let { value -> parseUtc(dto.timeTag)?.let { it to value } } }
            .maxByOrNull { it.first }
        val kIndex = latestKp?.second?.roundToInt()

        return PropagationDto(
            solarFluxIndex = latestFlux?.second?.roundToInt(),
            kIndex = kIndex,
            aIndex = kp?.let { dailyAp(it, now) },
            solarFlares = flares?.let { describeFlares(it, now) },
            geomagneticState = kIndex?.let(::geomagneticState),
            source = SOURCE,
            lastUpdated = listOfNotNull(latestFlux?.first, latestKp?.first).maxOrNull()?.let(TIMESTAMP::format),
        )
    }

    /** Planetary Ap: the mean of the eight 3-hourly ap values of the last complete UTC day. */
    private fun dailyAp(rows: List<SwpcKpDto>, now: Instant): Int? {
        val today = now.atOffset(ZoneOffset.UTC).toLocalDate()
        return rows
            .mapNotNull { dto -> dto.aRunning?.takeIf { it >= 0 }?.let { a -> parseUtc(dto.timeTag)?.let { it to a } } }
            .distinctBy { it.first }
            .groupBy { it.first.atOffset(ZoneOffset.UTC).toLocalDate() }
            .filter { (day, values) -> day < today && values.size >= 8 }
            .maxByOrNull { it.key }
            ?.value
            ?.map { it.second }
            ?.average()
            ?.roundToInt()
    }

    private fun describeFlares(events: List<SwpcFlareDto>, now: Instant): String {
        val since = now - FLARE_WINDOW
        val largest = events
            .filter { it.maxClass != null && it.maxXrLong != null }
            .mapNotNull { event -> parseUtc(event.maxTime)?.takeIf { it > since && it <= now }?.let { event to it } }
            .maxByOrNull { it.first.maxXrLong!! }
            ?: return "None in last 24 h"
        return "${largest.first.maxClass} at ${CLOCK.format(largest.second)} UTC (largest, last 24 h)"
    }

    /** NOAA geomagnetic storm scale: Kp 5 is G1 through Kp 9 as G5. */
    private fun geomagneticState(k: Int): String = when {
        k <= 2 -> "Quiet"
        k == 3 -> "Unsettled"
        k == 4 -> "Active"
        k == 5 -> "G1 minor storm"
        k == 6 -> "G2 moderate storm"
        k == 7 -> "G3 strong storm"
        k == 8 -> "G4 severe storm"
        else -> "G5 extreme storm"
    }

    /** SWPC mixes "2026-09-28T12:00:00" and "2026-09-28T12:00:00Z"; both are UTC. */
    private fun parseUtc(tag: String?): Instant? = tag?.let {
        runCatching { LocalDateTime.parse(it.removeSuffix("Z")).toInstant(ZoneOffset.UTC) }.getOrNull()
    }
}
