package org.chornobyl.hamdash.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.settings.AppSettings
import org.chornobyl.hamdash.settings.AppTheme
import org.chornobyl.hamdash.settings.DistanceUnit

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    val settings: StateFlow<AppSettings> = container.settingsManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setShowLocalTime(v: Boolean) = viewModelScope.launch { container.settingsManager.setShowLocalTime(v) }
    fun setShowUtcTime(v: Boolean) = viewModelScope.launch { container.settingsManager.setShowUtcTime(v) }
    fun setDistanceUnit(v: DistanceUnit) = viewModelScope.launch { container.settingsManager.setDistanceUnit(v) }
    fun setShowGpsOnDashboard(v: Boolean) = viewModelScope.launch { container.settingsManager.setShowGpsOnDashboard(v) }
    fun setTheme(v: AppTheme) = viewModelScope.launch { container.settingsManager.setTheme(v) }
    fun setAutoSync(v: Boolean) = viewModelScope.launch { container.settingsManager.setAutoSyncEnabled(v) }
    fun setSyncInterval(v: Int) = viewModelScope.launch { container.settingsManager.setSyncIntervalMinutes(v) }
    fun setUseMobileData(v: Boolean) = viewModelScope.launch { container.settingsManager.setUseMobileData(v) }

    fun clearCache() = viewModelScope.launch {
        container.settingsManager.clearAll()
    }
}
