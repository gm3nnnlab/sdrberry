package org.chornobyl.hamdash.ui.repeaters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.domain.RepeaterMode
import org.chornobyl.hamdash.ui.common.EmptyState
import org.chornobyl.hamdash.ui.common.LoadingState
import org.chornobyl.hamdash.ui.common.UiState

@Composable
fun RepeatersScreen(
    viewModel: RepeatersViewModel,
    onOpenDetail: (String) -> Unit,
) {
    val selectedMode by viewModel.selectedMode.collectAsState()
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = selectedMode == null,
                    onClick = { viewModel.selectMode(null) },
                    label = { Text("All") },
                )
            }
            items(RepeaterMode.entries) { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { viewModel.selectMode(mode) },
                    label = { Text(mode.label) },
                )
            }
        }

        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> org.chornobyl.hamdash.ui.common.ErrorState(s.message)
            is UiState.Empty -> EmptyState("No repeaters match this filter.")
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(s.data, key = { it.id }) { repeater ->
                        RepeaterRow(
                            repeater = repeater,
                            onClick = { onOpenDetail(repeater.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(repeater.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RepeaterRow(
    repeater: RepeaterEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .then(Modifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clickable(onClick = onClick),
            ) {
                Text(repeater.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${repeater.callsign} · ${RepeaterMode.fromRaw(repeater.mode).label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "RX ${repeater.rxFrequencyMhz} MHz / TX ${repeater.txFrequencyMhz} MHz",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (repeater.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Toggle favorite",
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}
