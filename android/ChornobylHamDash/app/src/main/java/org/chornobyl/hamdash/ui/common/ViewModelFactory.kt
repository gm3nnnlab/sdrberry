package org.chornobyl.hamdash.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import org.chornobyl.hamdash.AppContainer

/** Simple factory that hands every ViewModel the same [AppContainer]. */
class HamDashViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        create(modelClass) as T

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AppContainer::class.java).newInstance(container) as T
    }
}
