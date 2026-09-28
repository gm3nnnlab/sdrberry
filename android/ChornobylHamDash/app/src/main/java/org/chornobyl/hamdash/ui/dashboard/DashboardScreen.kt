package org.chornobyl.hamdash.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.domain.RepeaterMode
import org.chornobyl.hamdash.ui.common.ConnectivityBadge
import org.chornobyl.hamdash.ui.common.DashCard
import org.chornobyl.hamdash.ui.common.formatDate
import org.chornobyl.hamdash.ui.common.formatLastSync
import org.chornobyl.hamdash.ui.common.formatLocalClock
import org.chornobyl.hamdash.ui.common.formatUtcClock
import org.chornobyl.hamdash.ui.theme.HamAmber
import org.chornobyl.hamdash.ui.theme.HamCyan
import org.chornobyl.hamdash.ui.theme.HamGreen

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenMap: () -> Unit,
    onOpenRepeaters: () -> Unit,
    onOpenDmr: () -> Unit,
    onOpenPropagation: () -> Unit,
) {
    val now by viewModel.nowTicker.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val sunTimes by viewModel.sunTimes.collectAsState()
    val hasLocation by viewModel.hasLocation.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val showUtc = settings?.showUtcTime ?: true
    val showLocal = settings?.showLocalTime ?: true
    val showGps = settings?.showGpsOnDashboard

    LaunchedEffect(showGps) { if (showGps == true) viewModel.loadSunTimes() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "CHORNOBYL HAMDASH",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HamGreen,
                    )
                    ConnectivityBadge(isOnline = state.isOnline)
                }
                Text(
                    text = formatDate(now),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatLastSync(state.lastSyncEpochMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            DashCard(title = "CLOCK") {
                if (showUtc) {
                    Text(
                        text = "UTC  " + formatUtcClock(now),
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                        color = HamGreen,
                    )
                }
                if (showLocal) {
                    Text(
                        text = "LOCAL " + formatLocalClock(now),
                        style = MaterialTheme.typography.headlineMedium,
                        color = HamAmber,
                    )
                }
                if (!showUtc && !showLocal) {
                    Text(
                        text = "Clocks hidden in Settings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = if (showGps == false) {
                        "Sunrise/Sunset: GPS display is off in Settings"
                    } else if (hasLocation && sunTimes != null) {
                        val sr = sunTimes?.sunriseUtc?.let { formatUtcClock(it.toInstant().toEpochMilli()) } ?: "NO DATA"
                        val ss = sunTimes?.sunsetUtc?.let { formatUtcClock(it.toInstant().toEpochMilli()) } ?: "NO DATA"
                        val len = sunTimes?.dayLengthMinutes?.let { "${it / 60}h ${it % 60}m" } ?: "NO DATA"
                        "Sunrise $sr UTC  Sunset $ss UTC  Day length $len"
                    } else {
                        "Sunrise/Sunset: NO DATA (location unavailable)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            DashCard(title = "MAP", onClick = onOpenMap) {
                Text(
                    text = "Tap to open the full repeater map",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            DashCard(title = "REPEATERS", onClick = onOpenRepeaters) {
                RepeaterMode.entries.forEach { mode ->
                    val count = state.repeaterCountsByMode[mode] ?: 0
                    if (count > 0) {
                        Text(
                            text = "${mode.label} — $count",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                if (state.repeaterCountsByMode.values.all { it == 0 }) {
                    Text("NO DATA", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            DashCard(title = "DMR", onClick = onOpenDmr) {
                val dmr = state.dmrSummary
                if (dmr == null) {
                    Text("NO DATA", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Text("${dmr.rxFrequencyMhz} MHz — DMR", color = HamCyan)
                    Text("Color Code ${dmr.colorCode ?: "NO DATA"}  Slot ${dmr.timeslot ?: "NO DATA"}")
                    Text("TG: ${dmr.talkgroup ?: "NO DATA"}")
                }
            }
        }

        item {
            DashCard(title = "PROPAGATION", onClick = onOpenPropagation) {
                val prop = state.propagation
                Text("Solar Flux: ${prop?.solarFluxIndex?.toString() ?: "NO DATA"}")
                Text("K-index: ${prop?.kIndex?.toString() ?: "NO DATA"}")
                Text("A-index: ${prop?.aIndex?.toString() ?: "NO DATA"}")
                Text("Solar flares: ${prop?.solarFlares ?: "NO DATA"}")
                Text("Geomagnetic: ${prop?.geomagneticState ?: "NO DATA"}")
            }
        }
    }
}
