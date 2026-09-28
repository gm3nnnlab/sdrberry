package org.chornobyl.hamdash.ui.propagation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.data.repository.RadioDataRepository
import org.chornobyl.hamdash.database.entity.PropagationEntity

data class RefreshState(val inProgress: Boolean = false, val message: String? = null)

class PropagationViewModel(
    private val repository: RadioDataRepository,
    private val isOnline: () -> Boolean,
) : ViewModel() {

    constructor(container: AppContainer) : this(
        container.radioDataRepository,
        { container.connectivityObserver.isSyncAllowed(allowMobileData = true) },
    )

    val propagation: StateFlow<PropagationEntity?> = repository
        .observePropagation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _refreshState = MutableStateFlow(RefreshState())
    val refreshState: StateFlow<RefreshState> = _refreshState.asStateFlow()

    /** Fetches from NOAA SWPC now, regardless of the auto-sync settings. */
    fun refresh() {
        if (_refreshState.value.inProgress) return
        if (!isOnline()) {
            _refreshState.value = RefreshState(message = MESSAGE_OFFLINE)
            return
        }
        _refreshState.value = RefreshState(inProgress = true)
        viewModelScope.launch {
            val updated = repository.refreshPropagation()
            _refreshState.value = RefreshState(message = if (updated) null else MESSAGE_FAILED)
        }
    }

    companion object {
        const val MESSAGE_OFFLINE = "Offline — showing the last stored reading."
        const val MESSAGE_FAILED = "Couldn't get data from NOAA SWPC — showing the last stored reading."
    }
}
