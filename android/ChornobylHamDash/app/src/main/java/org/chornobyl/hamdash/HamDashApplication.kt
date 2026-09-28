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
        // Seed local storage from the bundled data, then hand over to the auto-sync
        // schedule. A failed refresh leaves the seeded/local data in place.
        applicationScope.launch {
            container.radioDataRepository.ensureSeeded()
            container.syncScheduler.run()
        }
    }
}
