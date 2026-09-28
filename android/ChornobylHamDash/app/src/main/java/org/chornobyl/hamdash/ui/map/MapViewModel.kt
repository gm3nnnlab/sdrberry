package org.chornobyl.hamdash.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.domain.RepeaterMode
import org.chornobyl.hamdash.location.SimpleLocation

data class MapUiState(
    val repeaters: List<RepeaterEntity> = emptyList(),
    val selectedMode: RepeaterMode? = null,
    val selectedRepeaterId: String? = null,
    val myLocation: SimpleLocation? = null,
    val locationRequested: Boolean = false,
)

class MapViewModel(private val container: AppContainer) : ViewModel() {

    private val _selectedMode = MutableStateFlow<RepeaterMode?>(null)
    private val _selectedRepeaterId = MutableStateFlow<String?>(null)
    private val _myLocation = MutableStateFlow<SimpleLocation?>(null)

    val uiState: StateFlow<MapUiState> = combine(
        container.radioDataRepository.observeRepeaters(),
        _selectedMode,
        _selectedRepeaterId,
        _myLocation,
    ) { repeaters, mode, selectedId, location ->
        MapUiState(
            repeaters = if (mode == null) repeaters else repeaters.filter { it.mode == mode.name },
            selectedMode = mode,
            selectedRepeaterId = selectedId,
            myLocation = location,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapUiState())

    fun selectMode(mode: RepeaterMode?) {
        _selectedMode.value = mode
    }

    fun selectRepeater(id: String?) {
        _selectedRepeaterId.value = id
    }

    fun hasLocationPermission(): Boolean = container.locationManagerWrapper.hasLocationPermission()

    fun refreshMyLocation() {
        viewModelScope.launch {
            _myLocation.value = container.locationManagerWrapper.getCurrentLocation()
        }
    }
}
