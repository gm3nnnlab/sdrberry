package org.chornobyl.hamdash.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.chornobyl.hamdash.data.repository.RadioDataRepository
import org.chornobyl.hamdash.network.ConnectivityObserver
import org.chornobyl.hamdash.settings.SettingsManager

/**
 * Periodic remote refresh driven by the Settings screen's auto-sync, interval and
 * mobile-data options. Runs for as long as the app process is alive; there is no
 * background job, so nothing syncs while the app is not running.
 */
class SyncScheduler(
    private val repository: RadioDataRepository,
    private val settingsManager: SettingsManager,
    private val connectivity: ConnectivityObserver,
) {
    private data class Policy(val enabled: Boolean, val intervalMillis: Long, val useMobileData: Boolean)

    private var lastAttemptMillis = 0L

    suspend fun run() {
        settingsManager.settingsFlow
            .map {
                Policy(
                    enabled = it.autoSyncEnabled,
                    intervalMillis = it.syncIntervalMinutes.coerceAtLeast(MIN_INTERVAL_MINUTES) * 60_000L,
                    useMobileData = it.useMobileData,
                )
            }
            .distinctUntilChanged()
            .collectLatest { policy ->
                if (!policy.enabled) return@collectLatest
                while (true) {
                    // Waiting from the last attempt (not from the settings change) stops a
                    // slider drag from firing one request per intermediate value.
                    val wait = lastAttemptMillis + policy.intervalMillis - System.currentTimeMillis()
                    if (wait > 0) delay(wait)
                    lastAttemptMillis = System.currentTimeMillis()
                    if (connectivity.isSyncAllowed(policy.useMobileData)) repository.refresh()
                }
            }
    }

    private companion object {
        const val MIN_INTERVAL_MINUTES = 15
    }
}
