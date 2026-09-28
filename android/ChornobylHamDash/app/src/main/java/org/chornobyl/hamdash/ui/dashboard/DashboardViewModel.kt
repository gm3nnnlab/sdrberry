package org.chornobyl.hamdash.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.domain.RepeaterMode
import org.chornobyl.hamdash.domain.SunCalculator
import org.chornobyl.hamdash.domain.SunTimes
import org.chornobyl.hamdash.settings.AppSettings
import java.time.LocalDate
import java.time.ZoneId

data class DashboardUiState(
    val repeaterCountsByMode: Map<RepeaterMode, Int> = emptyMap(),
    val dmrSummary: RepeaterEntity? = null,
    val propagation: PropagationEntity? = null,
    val isOnline: Boolean = false,
    val lastSyncEpochMillis: Long? = null,
    val sunTimes: SunTimes? = null,
    val hasLocation: Boolean = false,
)

class DashboardViewModel(private val container: AppContainer) : ViewModel() {

    val nowTicker: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    val uiState: StateFlow<DashboardUiState> = combine(
        container.radioDataRepository.observeRepeaters(),
        container.radioDataRepository.observePropagation(),
        container.connectivityObserver.observe(),
        container.radioDataRepository.observeLastSync(),
    ) { repeaters, propagation, online, lastSync ->
        val counts = RepeaterMode.entries.associateWith { mode -> repeaters.count { it.mode == mode.name } }
        val dmrSummary = repeaters.firstOrNull { it.mode == RepeaterMode.DMR.name && it.colorCode != null }
        DashboardUiState(
            repeaterCountsByMode = counts,
            dmrSummary = dmrSummary,
            propagation = propagation,
            isOnline = online,
            lastSyncEpochMillis = lastSync,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    /** Null until DataStore has loaded, so the screen never acts on defaults the user has changed. */
    val settings: StateFlow<AppSettings?> = container.settingsManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _sunTimes = MutableStateFlow<SunTimes?>(null)
    val sunTimes: StateFlow<SunTimes?> = _sunTimes

    private val _hasLocation = MutableStateFlow(false)
    val hasLocation: StateFlow<Boolean> = _hasLocation

    fun loadSunTimes() {
        viewModelScope.launch {
            val location = container.locationManagerWrapper.getCurrentLocation()
            if (location != null) {
                _hasLocation.value = true
                _sunTimes.value = SunCalculator.calculate(
                    LocalDate.now(ZoneId.systemDefault()),
                    location.latitude,
                    location.longitude,
                )
            } else {
                _hasLocation.value = false
                _sunTimes.value = null
            }
        }
    }
}
