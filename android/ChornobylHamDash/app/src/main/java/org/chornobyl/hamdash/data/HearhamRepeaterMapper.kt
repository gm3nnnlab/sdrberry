package org.chornobyl.hamdash.data

import org.chornobyl.hamdash.data.model.HearhamRepeaterDto
import org.chornobyl.hamdash.data.model.RepeaterDto
import org.chornobyl.hamdash.domain.RepeaterMode
import java.time.LocalDate
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Filters the worldwide HearHam list down to operational repeaters near Chornobyl and
 * converts them to the app's model. Frequencies are from the user's side: receive on the
 * repeater output, transmit on its input.
 */
object HearhamRepeaterMapper {
    const val SOURCE = "HearHam.live repeater list (hearham.com)"

    private const val CENTER_LAT = 51.2763
    private const val CENTER_LON = 30.2219
    private const val RADIUS_KM = 300.0
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * IARU Region 1 amateur bands where repeaters operate, in MHz. An entry whose receive
     * or transmit frequency falls outside them is a listing error, and following it would
     * mean transmitting outside the amateur service.
     */
    private val AMATEUR_BANDS_MHZ = listOf(
        28.0..29.7,
        50.0..54.0,
        144.0..146.0,
        430.0..440.0,
        1240.0..1300.0,
    )

    private val COLOR_CODE = Regex("""CC\s*(\d+)""", RegexOption.IGNORE_CASE)

    fun map(entries: List<HearhamRepeaterDto>, retrievedOn: LocalDate): List<RepeaterDto> =
        entries.mapNotNull { toRepeater(it, retrievedOn) }

    private fun toRepeater(entry: HearhamRepeaterDto, retrievedOn: LocalDate): RepeaterDto? {
        if (entry.operational != 1) return null
        val callsign = entry.callsign?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val lat = entry.latitude ?: return null
        val lon = entry.longitude ?: return null
        if (lat == 0.0 && lon == 0.0) return null
        if (distanceKm(lat, lon) > RADIUS_KM) return null

        val outputHz = entry.frequency?.takeIf { it > 0 } ?: return null
        val offsetHz = entry.offset ?: 0L
        val rxMhz = outputHz / 1_000_000.0
        val txMhz = (outputHz + offsetHz) / 1_000_000.0
        if (!inAmateurBand(rxMhz) || !inAmateurBand(txMhz)) return null

        val rawMode = entry.mode?.trim().orEmpty()
        val mode = normaliseMode(rawMode)
        val encode = entry.encode?.trim().orEmpty()

        return RepeaterDto(
            id = "hearham-${entry.id}",
            name = entry.city?.trim()?.takeIf { it.isNotEmpty() } ?: callsign,
            callsign = callsign,
            mode = mode.name,
            rxFrequencyMhz = rxMhz,
            txFrequencyMhz = txMhz,
            offsetMhz = offsetHz / 1_000_000.0,
            toneCtcss = encode.toDoubleOrNull()?.takeIf { it > 0 }?.let { String.format(Locale.US, "%.1f", it) },
            digitalMode = rawMode.takeIf { mode != RepeaterMode.FM && it.isNotEmpty() },
            colorCode = if (mode == RepeaterMode.DMR) COLOR_CODE.find(encode)?.groupValues?.get(1)?.toIntOrNull() else null,
            latitude = lat,
            longitude = lon,
            source = SOURCE,
            lastUpdated = "Retrieved $retrievedOn",
        )
    }

    private fun normaliseMode(raw: String): RepeaterMode {
        val primary = raw.uppercase(Locale.ROOT).substringBefore('/').replace("-", "").trim()
        return when {
            primary.startsWith("DMR") -> RepeaterMode.DMR
            primary == "DSTAR" -> RepeaterMode.DSTAR
            primary == "YSF" || primary == "C4FM" || primary == "FUSION" -> RepeaterMode.C4FM
            primary == "FM" || primary == "NFM" -> RepeaterMode.FM
            else -> RepeaterMode.OTHER
        }
    }

    private fun inAmateurBand(mhz: Double) = AMATEUR_BANDS_MHZ.any { mhz in it }

    private fun distanceKm(lat: Double, lon: Double): Double {
        val p1 = Math.toRadians(CENTER_LAT)
        val p2 = Math.toRadians(lat)
        val dp = p2 - p1
        val dl = Math.toRadians(lon - CENTER_LON)
        val h = sin(dp / 2).pow(2) + cos(p1) * cos(p2) * sin(dl / 2).pow(2)
        return 2 * EARTH_RADIUS_KM * asin(sqrt(h))
    }
}
