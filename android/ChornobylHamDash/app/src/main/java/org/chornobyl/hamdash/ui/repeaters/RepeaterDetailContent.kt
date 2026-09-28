package org.chornobyl.hamdash.ui.repeaters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.domain.RepeaterMode

/**
 * All fields present on a repeater. Anything the data source did not publish (null)
 * is simply omitted, never guessed at.
 */
@Composable
fun RepeaterDetailContent(repeater: RepeaterEntity, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(repeater.name, style = MaterialTheme.typography.titleLarge)
        Text(
            "Callsign: ${repeater.callsign}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        DetailRow("Mode", RepeaterMode.fromRaw(repeater.mode).label)
        DetailRow("RX Frequency", "${repeater.rxFrequencyMhz} MHz")
        DetailRow("TX Frequency", "${repeater.txFrequencyMhz} MHz")
        repeater.offsetMhz?.let { DetailRow("Offset", "$it MHz") }
        repeater.toneCtcss?.let { DetailRow("Tone / CTCSS", it) }
        repeater.digitalMode?.let { DetailRow("Digital Mode", it) }
        repeater.colorCode?.let { DetailRow("DMR Color Code", it.toString()) }
        repeater.timeslot?.let { DetailRow("DMR Timeslot", "TS $it") }
        repeater.talkgroup?.let { DetailRow("Talkgroup", it) }
        DetailRow("Coordinates", "${repeater.latitude}, ${repeater.longitude}")
        repeater.elevationM?.let { DetailRow("Elevation", "$it m") }
        DetailRow("Source", repeater.source)
        DetailRow("Last Updated", repeater.lastUpdated)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
