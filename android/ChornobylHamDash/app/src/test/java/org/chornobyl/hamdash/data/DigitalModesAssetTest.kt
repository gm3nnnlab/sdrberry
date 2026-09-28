package org.chornobyl.hamdash.data

import kotlinx.serialization.json.Json
import org.chornobyl.hamdash.data.model.DigitalModeDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Each bundled digital mode must link to the body that defines it (ETSI for DMR, JARL
 * for D-STAR, Yaesu for System Fusion, the APRS specification, WSJT-X, JS8Call) rather
 * than to a reseller or third-party page, since its facts are taken from those documents.
 */
class DigitalModesAssetTest {
    private val modes: List<DigitalModeDto> =
        Json.decodeFromString(File("src/main/assets/digital_modes.json").readText())

    private val officialDocs = linkedMapOf(
        "dmr" to "https://www.etsi.org/",
        "dstar" to "https://www.jarl.com/d-star/",
        "c4fm" to "https://www.yaesu.com/",
        "aprs" to "https://github.com/wb2osz/aprsspec",
        "ft8" to "https://wsjt.sourceforge.io/",
        "js8call" to "https://js8call.com/",
    )

    @Test
    fun `lists the documented modes in order`() {
        assertEquals(officialDocs.keys.toList(), modes.map { it.id })
    }

    @Test
    fun `every mode links to its official specification or documentation`() {
        modes.forEach { mode ->
            val expected = officialDocs.getValue(mode.id)
            assertTrue("${mode.id} links to ${mode.docUrl}", mode.docUrl.startsWith(expected))
        }
    }

    @Test
    fun `contains no mock placeholder text`() {
        modes.forEach { mode ->
            val text = listOf(mode.name, mode.description, mode.howItWorks, mode.useCases, mode.equipmentNeeded)
                .joinToString(" ")
            assertFalse("${mode.id} mentions mock data", text.contains("mock", ignoreCase = true))
        }
    }
}
