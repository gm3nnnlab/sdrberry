package org.chornobyl.hamdash.ui.bands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.ui.common.UiState

class BandsViewModel(container: AppContainer) : ViewModel() {
    val uiState: StateFlow<UiState<List<BandEntity>>> = container.radioDataRepository
        .observeBands()
        .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
}
