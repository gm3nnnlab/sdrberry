package org.chornobyl.hamdash.ui.propagation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.ui.common.DashCard

@Composable
fun PropagationScreen(viewModel: PropagationViewModel) {
    val propagation by viewModel.propagation.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "PROPAGATION",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        propagation?.source?.let {
            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        DashCard(title = "SOLAR FLUX INDEX") {
            Text(propagation?.solarFluxIndex?.toString() ?: "NO DATA", style = MaterialTheme.typography.displayLarge)
        }
        DashCard(title = "K-INDEX") {
            Text(propagation?.kIndex?.toString() ?: "NO DATA", style = MaterialTheme.typography.headlineMedium)
        }
        DashCard(title = "A-INDEX") {
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
