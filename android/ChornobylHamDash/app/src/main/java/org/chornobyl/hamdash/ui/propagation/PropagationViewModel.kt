package org.chornobyl.hamdash.ui.propagation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.database.entity.PropagationEntity

class PropagationViewModel(container: AppContainer) : ViewModel() {
    val propagation: StateFlow<PropagationEntity?> = container.radioDataRepository
        .observePropagation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
