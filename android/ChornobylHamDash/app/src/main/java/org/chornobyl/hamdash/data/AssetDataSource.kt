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
 * unavailable for a fresh install. `repeaters.json` and `talkgroups.json` are dated
 * snapshots made by [HearhamRepeaterMapper] and [BrandmeisterTalkgroupMapper]; `bands.json`
 * and `digital_modes.json` are taken from the IARU band plans and each mode's official
 * documentation.
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
