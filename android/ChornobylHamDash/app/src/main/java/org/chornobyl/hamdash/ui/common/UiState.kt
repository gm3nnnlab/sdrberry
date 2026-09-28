package org.chornobyl.hamdash.ui.common

/** Generic Loading/Error/Empty/Success state used by every data-driven screen. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
}

fun <T : Collection<*>> T.toUiState(): UiState<T> =
    if (isEmpty()) UiState.Empty else UiState.Success(this)
