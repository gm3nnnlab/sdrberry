package org.chornobyl.hamdash.ui.digital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.ui.common.UiState

class DigitalViewModel(container: AppContainer) : ViewModel() {
    val uiState: StateFlow<UiState<List<DigitalModeEntity>>> = container.radioDataRepository
        .observeDigitalModes()
        .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
}
