package org.chornobyl.hamdash.ui.propagation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.ui.common.DashCard

@Composable
fun PropagationScreen(viewModel: PropagationViewModel) {
    val propagation by viewModel.propagation.collectAsState()
    val refreshState by viewModel.refreshState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "PROPAGATION",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            OutlinedButton(onClick = viewModel::refresh, enabled = !refreshState.inProgress) {
                if (refreshState.inProgress) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("REFRESH")
            }
        }
        val source = propagation?.source
        Text(
            source ?: "Not synced yet. Data comes from NOAA SWPC — tap Refresh or turn on auto-sync.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        refreshState.message?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }

        DashCard(title = "SOLAR FLUX INDEX (10.7 cm)") {
            Text(propagation?.solarFluxIndex?.toString() ?: "NO DATA", style = MaterialTheme.typography.displayLarge)
        }
        DashCard(title = "K-INDEX (PLANETARY Kp)") {
            Text(propagation?.kIndex?.toString() ?: "NO DATA", style = MaterialTheme.typography.headlineMedium)
        }
        DashCard(title = "A-INDEX (Ap, LAST FULL UTC DAY)") {
            Text(propagation?.aIndex?.toString() ?: "NO DATA", style = MaterialTheme.typography.headlineMedium)
        }
        DashCard(title = "SOLAR FLARES") {
            Text(propagation?.solarFlares ?: "NO DATA", style = MaterialTheme.typography.bodyLarge)
        }
        DashCard(title = "GEOMAGNETIC STATE") {
            Text(propagation?.geomagneticState ?: "NO DATA", style = MaterialTheme.typography.bodyLarge)
        }
        propagation?.lastUpdated?.let {
            Text("Last updated: $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
