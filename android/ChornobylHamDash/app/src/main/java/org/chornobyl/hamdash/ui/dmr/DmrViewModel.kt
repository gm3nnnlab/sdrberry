package org.chornobyl.hamdash.ui.dmr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity
import org.chornobyl.hamdash.domain.RepeaterMode
import org.chornobyl.hamdash.ui.common.UiState

class DmrViewModel(private val container: AppContainer) : ViewModel() {

    val dmrRepeaters: StateFlow<List<RepeaterEntity>> = container.radioDataRepository
        .observeRepeaters()
        .map { repeaters -> repeaters.filter { it.mode == RepeaterMode.DMR.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val talkgroups: StateFlow<UiState<List<TalkgroupEntity>>> = _query
        .flatMapLatest { q -> container.radioDataRepository.searchTalkgroups(q) }
        .map { list -> if (list.isEmpty()) UiState.Empty else UiState.Success(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun onQueryChange(value: String) {
        _query.value = value
    }
}
