package org.chornobyl.hamdash.data

import kotlinx.serialization.json.Json
import org.chornobyl.hamdash.data.model.BandDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

/**
 * Checks the bundled band list against the IARU Region 1 band plans (HF effective
 * 16 Oct 2020, VHF and UHF effective Dec 2020). Ukraine is in Region 1; Region 2 (US)
 * edges such as 144–148 MHz would lead users to transmit outside their allocation.
 */
class BandsAssetTest {
    private val bands: List<BandDto> =
        Json.decodeFromString(File("src/main/assets/bands.json").readText())

    private val region1EdgesMhz = linkedMapOf(
        "160m" to (1.81 to 2.0),
        "80m" to (3.5 to 3.8),
        "40m" to (7.0 to 7.2),
        "30m" to (10.1 to 10.15),
        "20m" to (14.0 to 14.35),
        "17m" to (18.068 to 18.168),
        "15m" to (21.0 to 21.45),
        "12m" to (24.89 to 24.99),
        "10m" to (28.0 to 29.7),
        "6m" to (50.0 to 54.0),
        "2m" to (144.0 to 146.0),
        "70cm" to (430.0 to 440.0),
    )

    @Test
    fun `lists the twelve bands in order`() {
        assertEquals(region1EdgesMhz.keys.toList(), bands.map { it.id })
    }

    @Test
    fun `band edges match the IARU Region 1 band plans`() {
        bands.forEach { band ->
            val (low, high) = region1EdgesMhz.getValue(band.id)
            assertEquals("${band.id} lower edge", low, band.freqRangeLow, 1e-9)
            assertEquals("${band.id} upper edge", high, band.freqRangeHigh, 1e-9)
        }
    }

    @Test
    fun `contains no mock placeholder text`() {
        bands.forEach { band ->
            val text = listOf(band.name, band.typicalModes, band.purpose, band.blurb).joinToString(" ")
            assertFalse("${band.id} mentions mock data", text.contains("mock", ignoreCase = true))
        }
    }
}
