package org.chornobyl.hamdash.ui.digital

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
fun DigitalScreen(viewModel: DigitalViewModel) {
    val state by viewModel.uiState.collectAsState()

    when (val s = state) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(s.message)
        is UiState.Empty -> EmptyState("No digital mode reference data available.")
        is UiState.Success -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    "Facts are taken from each mode's official specification or documentation, " +
                        "linked as the source on each entry.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(s.data, key = { it.id }) { mode ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(mode.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(mode.description, style = MaterialTheme.typography.bodyMedium)
                        Text("How it works: ${mode.howItWorks}", style = MaterialTheme.typography.bodyMedium)
                        Text("Use cases: ${mode.useCases}", style = MaterialTheme.typography.bodyMedium)
                        Text("Equipment: ${mode.equipmentNeeded}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Source: ${mode.docUrl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        }
    }
}
