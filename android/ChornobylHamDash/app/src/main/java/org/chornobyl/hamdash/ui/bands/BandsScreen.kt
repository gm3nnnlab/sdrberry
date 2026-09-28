package org.chornobyl.hamdash.ui.bands

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.ui.common.EmptyState
import org.chornobyl.hamdash.ui.common.ErrorState
import org.chornobyl.hamdash.ui.common.LoadingState
import org.chornobyl.hamdash.ui.common.UiState

@Composable
fun BandsScreen(viewModel: BandsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "General reference only. Always check your own country's amateur-radio " +
                "frequency allocation table before assuming any frequency is licensed for your use.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message)
            is UiState.Empty -> EmptyState("No band reference data available.")
            is UiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(s.data, key = { it.id }) { band ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(band.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${band.freqRangeLow} – ${band.freqRangeHigh} MHz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text("Modes: ${band.typicalModes}", style = MaterialTheme.typography.bodyMedium)
                            Text("Purpose: ${band.purpose}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                band.blurb,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
