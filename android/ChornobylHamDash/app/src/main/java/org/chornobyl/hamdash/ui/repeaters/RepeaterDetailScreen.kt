package org.chornobyl.hamdash.ui.repeaters

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import org.chornobyl.hamdash.ui.common.EmptyState

@Composable
fun RepeaterDetailScreen(viewModel: RepeatersViewModel, repeaterId: String) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val repeater = (state as? org.chornobyl.hamdash.ui.common.UiState.Success)
        ?.data
        ?.firstOrNull { it.id == repeaterId }

    if (repeater == null) {
        EmptyState("Repeater not found.")
    } else {
        RepeaterDetailContent(
            repeater = repeater,
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        )
    }
}
