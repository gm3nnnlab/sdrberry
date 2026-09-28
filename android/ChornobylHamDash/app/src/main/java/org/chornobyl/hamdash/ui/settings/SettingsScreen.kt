package org.chornobyl.hamdash.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.settings.AppTheme
import org.chornobyl.hamdash.settings.DistanceUnit

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("SETTINGS", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        SettingRow("Show local time") {
            Switch(checked = settings.showLocalTime, onCheckedChange = viewModel::setShowLocalTime)
        }
        SettingRow("Show UTC time") {
            Switch(checked = settings.showUtcTime, onCheckedChange = viewModel::setShowUtcTime)
        }
        SettingRow("Show GPS on dashboard") {
            Switch(checked = settings.showGpsOnDashboard, onCheckedChange = viewModel::setShowGpsOnDashboard)
        }
        HorizontalDivider()

        Text("Distance units", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DistanceUnit.entries.forEach { unit ->
                Button(onClick = { viewModel.setDistanceUnit(unit) }) {
                    Text(if (settings.distanceUnit == unit) "[${unit.name}]" else unit.name)
                }
            }
        }
        HorizontalDivider()

        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppTheme.entries.forEach { theme ->
                Button(onClick = { viewModel.setTheme(theme) }) {
                    Text(if (settings.theme == theme) "[${theme.name}]" else theme.name)
                }
            }
        }
        HorizontalDivider()

        SettingRow("Auto-sync") {
            Switch(checked = settings.autoSyncEnabled, onCheckedChange = viewModel::setAutoSync)
        }
        Text("Sync interval: ${settings.syncIntervalMinutes} min", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = settings.syncIntervalMinutes.toFloat(),
            onValueChange = { viewModel.setSyncInterval(it.toInt()) },
            valueRange = 15f..240f,
            steps = 14,
        )
        SettingRow("Use mobile data for sync") {
            Switch(checked = settings.useMobileData, onCheckedChange = viewModel::setUseMobileData)
        }
        HorizontalDivider()

        Button(onClick = viewModel::clearCache, modifier = Modifier.fillMaxWidth()) {
            Text("Clear cache")
        }
    }
}

@Composable
private fun SettingRow(label: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        control()
    }
}
