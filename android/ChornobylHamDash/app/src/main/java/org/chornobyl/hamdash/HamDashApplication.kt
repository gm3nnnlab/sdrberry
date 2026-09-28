package org.chornobyl.hamdash

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HamDashApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Seed local storage from the bundled mock dataset on first run, then attempt
        // one opportunistic remote refresh (which will fail gracefully against the
        // placeholder mock URL and simply leave the seeded/local data in place).
        applicationScope.launch {
            container.radioDataRepository.ensureSeeded()
            container.radioDataRepository.refresh()
        }
    }
}
