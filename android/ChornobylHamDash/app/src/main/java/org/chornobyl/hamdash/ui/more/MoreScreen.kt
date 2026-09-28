package org.chornobyl.hamdash.ui.more

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class MoreEntry(val label: String, val route: String)

@Composable
fun MoreScreen(onNavigate: (String) -> Unit) {
    val entries = listOf(
        MoreEntry("Bands", "bands"),
        MoreEntry("Digital Modes", "digital"),
        MoreEntry("Propagation", "propagation"),
        MoreEntry("Settings", "settings"),
        MoreEntry("About / Data Sources", "about"),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("MORE", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
        items(entries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigate(entry.route) },
            ) {
                Text(
                    entry.label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}
