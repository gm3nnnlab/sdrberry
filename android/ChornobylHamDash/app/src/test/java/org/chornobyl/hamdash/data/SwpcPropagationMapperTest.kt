package org.chornobyl.hamdash.data

import org.chornobyl.hamdash.data.model.SwpcFlareDto
import org.chornobyl.hamdash.data.model.SwpcFluxDto
import org.chornobyl.hamdash.data.model.SwpcKpDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class SwpcPropagationMapperTest {
    private val now = Instant.parse("2026-09-28T16:00:00Z")

    // 2026-09-27 as published by SWPC: planetary Ap for that day is 9.
    private val fullPreviousDay = listOf(
        "00" to (4.00 to 27), "03" to (3.33 to 18), "06" to (2.00 to 7), "09" to (1.33 to 5),
        "12" to (1.33 to 5), "15" to (0.33 to 2), "18" to (0.33 to 2), "21" to (0.33 to 2),
    ).map { (hour, v) -> SwpcKpDto("2026-09-27T$hour:00:00", v.first, v.second) }

    private val partialToday = listOf(
        SwpcKpDto("2026-09-28T00:00:00", 0.67, 3),
        SwpcKpDto("2026-09-28T03:00:00", 1.67, 6),
    )

    private fun map(
        flux: List<SwpcFluxDto>? = null,
        kp: List<SwpcKpDto>? = null,
        flares: List<SwpcFlareDto>? = null,
    ) = SwpcPropagationMapper.map(flux, kp, flares, now)

    @Test
    fun `solar flux is the latest reading, rounded`() {
        val result = map(flux = listOf(SwpcFluxDto(97.4, "2026-09-27T20:00:00")))
        assertEquals(97, result?.solarFluxIndex)
    }

    @Test
    fun `K-index is the latest valid Kp, rounded`() {
        val result = map(kp = fullPreviousDay + partialToday)
        assertEquals(2, result?.kIndex)
    }

    @Test
    fun `missing Kp values reported as negative are skipped`() {
        val result = map(kp = partialToday + SwpcKpDto("2026-09-28T06:00:00", -1.0, -1))
        assertEquals(2, result?.kIndex)
    }

    @Test
    fun `A-index is the daily Ap of the last complete UTC day`() {
        val result = map(kp = fullPreviousDay + partialToday)
        assertEquals(9, result?.aIndex)
    }

    @Test
    fun `A-index is absent when no UTC day is complete`() {
        val result = map(kp = partialToday)
        assertNull(result?.aIndex)
    }

    @Test
    fun `geomagnetic state follows the NOAA G-scale`() {
        fun stateFor(kp: Double) = map(kp = listOf(SwpcKpDto("2026-09-28T12:00:00", kp, 0)))?.geomagneticState
        assertEquals("Quiet", stateFor(1.67))
        assertEquals("Unsettled", stateFor(3.0))
        assertEquals("Active", stateFor(4.0))
        assertEquals("G1 minor storm", stateFor(4.67))
        assertEquals("G3 strong storm", stateFor(7.0))
        assertEquals("G5 extreme storm", stateFor(9.0))
    }

    @Test
    fun `flares reports the largest event in the last 24 hours`() {
        val result = map(
            flares = listOf(
                SwpcFlareDto("2026-09-26T10:00:00Z", "M1.0", 1.0e-5),
                SwpcFlareDto("2026-09-28T09:00:00Z", "B6.5", 6.5e-7),
                SwpcFlareDto("2026-09-28T15:19:00Z", "C2.5", 2.5e-6),
            ),
        )
        assertEquals("C2.5 at 15:19 UTC (largest, last 24 h)", result?.solarFlares)
    }

    @Test
    fun `flares says none when the feed has no event in the last 24 hours`() {
        val result = map(flares = listOf(SwpcFlareDto("2026-09-26T10:00:00Z", "M1.0", 1.0e-5)))
        assertEquals("None in last 24 h", result?.solarFlares)
    }

    @Test
    fun `a failed feed leaves only its own fields empty`() {
        val result = map(flux = listOf(SwpcFluxDto(97.0, "2026-09-27T20:00:00")), kp = null, flares = null)
        assertNotNull(result)
        assertEquals(97, result?.solarFluxIndex)
        assertNull(result?.kIndex)
        assertNull(result?.aIndex)
        assertNull(result?.geomagneticState)
        assertNull(result?.solarFlares)
    }

    @Test
    fun `nothing is returned when every feed failed`() {
        assertNull(map())
    }

    @Test
    fun `empty feeds count as failed so the stored reading is kept`() {
        assertNull(map(flux = emptyList(), kp = emptyList(), flares = emptyList()))
    }

    @Test
    fun `feeds holding only missing values count as empty`() {
        assertNull(map(kp = listOf(SwpcKpDto("2026-09-28T12:00:00", -1.0, -1))))
    }

    @Test
    fun `an empty flare feed shows no data rather than claiming no flares`() {
        val result = map(flux = listOf(SwpcFluxDto(97.0, "2026-09-27T20:00:00")), flares = emptyList())
        assertEquals(97, result?.solarFluxIndex)
        assertNull(result?.solarFlares)
    }

    @Test
    fun `last updated is the newest observation time`() {
        val result = map(
            flux = listOf(SwpcFluxDto(97.0, "2026-09-27T20:00:00")),
            kp = fullPreviousDay + partialToday,
        )
        assertEquals("2026-09-28 03:00 UTC", result?.lastUpdated)
        assertEquals(SwpcPropagationMapper.SOURCE, result?.source)
    }
}
