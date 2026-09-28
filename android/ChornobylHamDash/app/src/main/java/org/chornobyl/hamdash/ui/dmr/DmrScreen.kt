package org.chornobyl.hamdash.ui.dmr

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.ui.common.EmptyState
import org.chornobyl.hamdash.ui.common.LoadingState
import org.chornobyl.hamdash.ui.common.UiState

@Composable
fun DmrScreen(viewModel: DmrViewModel) {
    val repeaters by viewModel.dmrRepeaters.collectAsState()
    val query by viewModel.query.collectAsState()
    val talkgroups by viewModel.talkgroups.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("DMR", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }

        if (repeaters.isEmpty()) {
            item { EmptyState("No DMR repeaters in the current dataset.") }
        } else {
            items(repeaters, key = { it.id }) { repeater ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(repeater.name, style = MaterialTheme.typography.titleMedium)
                        Text("Frequency: ${repeater.rxFrequencyMhz} MHz", style = MaterialTheme.typography.bodyMedium)
                        Text("Mode: DMR", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Color Code: ${repeater.colorCode ?: "NO DATA"}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "Slot: ${repeater.timeslot?.let { "TS $it" } ?: "NO DATA"}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "Talkgroup: ${repeater.talkgroup ?: "NO DATA"}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        item {
            Text(
                "TALKGROUPS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search talkgroups") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        when (val s = talkgroups) {
            is UiState.Loading -> item { LoadingState() }
            is UiState.Error -> item { org.chornobyl.hamdash.ui.common.ErrorState(s.message) }
            is UiState.Empty -> item { EmptyState("No talkgroups match your search.") }
            is UiState.Success -> {
                items(s.data, key = { it.id }) { tg ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${tg.tgId} — ${tg.name}", style = MaterialTheme.typography.titleMedium)
                            Text(tg.description, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Source: ${tg.source}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
