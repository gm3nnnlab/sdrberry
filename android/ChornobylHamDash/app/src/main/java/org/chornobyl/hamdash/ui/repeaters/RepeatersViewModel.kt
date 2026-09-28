package org.chornobyl.hamdash.ui.repeaters

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
import org.chornobyl.hamdash.ui.common.UiState

class RepeatersViewModel(private val container: AppContainer) : ViewModel() {

    private val _selectedMode = MutableStateFlow<RepeaterMode?>(null)
    val selectedMode: StateFlow<RepeaterMode?> = _selectedMode

    val uiState: StateFlow<UiState<List<RepeaterEntity>>> = combine(
        container.radioDataRepository.observeRepeaters(),
        _selectedMode,
    ) { repeaters, mode ->
        val filtered = if (mode == null) repeaters else repeaters.filter { it.mode == mode.name }
        if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered.sortedBy { it.name })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun selectMode(mode: RepeaterMode?) {
        _selectedMode.value = mode
    }

    fun toggleFavorite(repeaterId: String) {
        viewModelScope.launch { container.radioDataRepository.toggleFavorite(repeaterId) }
    }
}
