package org.chornobyl.hamdash.data

import org.chornobyl.hamdash.data.model.HearhamRepeaterDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HearhamRepeaterMapperTest {
    private val retrieved = LocalDate.of(2026, 9, 28)

    private fun kyivDmr(
        id: Long = 1,
        mode: String = "DMR",
        encode: String = "CC1",
        frequency: Long = 438_800_000,
        offset: Long = -7_600_000,
        operational: Int = 1,
        lat: Double = 50.45,
        lon: Double = 30.524,
    ) = HearhamRepeaterDto(id, "UR0UUM", lat, lon, "Kyiv, Ukraine", mode, encode, frequency, offset, operational)

    private fun map(vararg entries: HearhamRepeaterDto) = HearhamRepeaterMapper.map(entries.toList(), retrieved)

    @Test
    fun `maps a DMR repeater with user receive and transmit frequencies`() {
        val r = map(kyivDmr(id = 42)).single()
        assertEquals("hearham-42", r.id)
        assertEquals("UR0UUM", r.callsign)
        assertEquals("Kyiv, Ukraine", r.name)
        assertEquals("DMR", r.mode)
        assertEquals(438.8, r.rxFrequencyMhz, 1e-9)
        assertEquals(431.2, r.txFrequencyMhz, 1e-9)
        assertEquals(-7.6, r.offsetMhz!!, 1e-9)
        assertEquals(1, r.colorCode)
        assertNull(r.toneCtcss)
        assertNull(r.timeslot)
        assertNull(r.talkgroup)
        assertEquals(HearhamRepeaterMapper.SOURCE, r.source)
        assertEquals("Retrieved 2026-09-28", r.lastUpdated)
    }

    @Test
    fun `reads the CTCSS tone for analog repeaters and ignores a zero tone`() {
        val fm = map(kyivDmr(id = 1, mode = "FM", encode = "88.5", frequency = 145_600_000, offset = -600_000)).single()
        assertEquals("FM", fm.mode)
        assertEquals("88.5", fm.toneCtcss)
        assertNull(fm.colorCode)

        val noTone = map(kyivDmr(id = 2, mode = "FM", encode = "0.00", frequency = 145_600_000, offset = -600_000)).single()
        assertNull(noTone.toneCtcss)
    }

    @Test
    fun `normalises the mode spellings used in the feed`() {
        fun modeOf(raw: String) = map(kyivDmr(mode = raw, encode = "")).single().mode
        assertEquals("DMR", modeOf("DMR    "))
        assertEquals("DSTAR", modeOf("D-star"))
        assertEquals("DSTAR", modeOf("D-STAR/FM "))
        assertEquals("C4FM", modeOf("YSF/FM "))
        assertEquals("FM", modeOf("NFM"))
        assertEquals("OTHER", modeOf("P25"))
    }

    @Test
    fun `skips repeaters marked not operational`() {
        assertTrue(map(kyivDmr(operational = 0)).isEmpty())
    }

    @Test
    fun `keeps repeaters within the radius around Chornobyl only`() {
        val lviv = kyivDmr(id = 2, lat = 49.838, lon = 24.009)
        assertEquals(listOf("hearham-1"), map(kyivDmr(id = 1), lviv).map { it.id })
    }

    @Test
    fun `skips entries whose transmit frequency would be outside the amateur bands`() {
        val inputListedAsOutput = kyivDmr(frequency = 431_075_000, offset = -7_600_000)
        val intoPmr446 = kyivDmr(frequency = 438_650_000, offset = 7_600_000)
        assertTrue(map(inputListedAsOutput, intoPmr446).isEmpty())
    }

    @Test
    fun `skips entries without a usable position or frequency`() {
        val noPosition = kyivDmr(lat = 0.0, lon = 0.0)
        val noFrequency = HearhamRepeaterDto(3, "UR0XXX", 50.45, 30.52, "Kyiv", "FM", null, null, null, 1)
        assertTrue(map(noPosition, noFrequency).isEmpty())
    }
}
