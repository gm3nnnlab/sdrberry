package org.chornobyl.hamdash.data

import android.content.Context
import kotlinx.serialization.json.Json
import org.chornobyl.hamdash.data.model.BandDto
import org.chornobyl.hamdash.data.model.DigitalModeDto
import org.chornobyl.hamdash.data.model.RepeaterDto
import org.chornobyl.hamdash.data.model.TalkgroupDto

/**
 * Reads the bundled JSON in `assets/` — the offline seed data used the very first time
 * the app runs (before any successful network sync) and whenever the network is
 * unavailable for a fresh install. `repeaters.json` is a dated HearHam snapshot produced
 * by [HearhamRepeaterMapper]; the other files are illustrative mock data.
 */
class AssetDataSource(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun readAsset(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }

    fun loadRepeaters(): List<RepeaterDto> =
        json.decodeFromString(readAsset("repeaters.json"))

    fun loadBands(): List<BandDto> =
        json.decodeFromString(readAsset("bands.json"))

    fun loadDigitalModes(): List<DigitalModeDto> =
        json.decodeFromString(readAsset("digital_modes.json"))

    fun loadTalkgroups(): List<TalkgroupDto> =
        json.decodeFromString(readAsset("talkgroups.json"))
}
