package org.chornobyl.hamdash.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hamdash_settings")

enum class DistanceUnit { KM, MI }
enum class AppTheme { DARK, LIGHT }

data class AppSettings(
    val showLocalTime: Boolean = true,
    val showUtcTime: Boolean = true,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val showGpsOnDashboard: Boolean = true,
    val theme: AppTheme = AppTheme.DARK,
    val autoSyncEnabled: Boolean = true,
    val syncIntervalMinutes: Int = 60,
    val useMobileData: Boolean = true,
)

/** Wraps the Preferences DataStore that backs the Settings screen. */
class SettingsManager(private val context: Context) {
    private object Keys {
        val SHOW_LOCAL = booleanPreferencesKey("show_local_time")
        val SHOW_UTC = booleanPreferencesKey("show_utc_time")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
        val SHOW_GPS = booleanPreferencesKey("show_gps_on_dashboard")
        val THEME = stringPreferencesKey("theme")
        val AUTO_SYNC = booleanPreferencesKey("auto_sync_enabled")
        val SYNC_INTERVAL = intPreferencesKey("sync_interval_minutes")
        val USE_MOBILE_DATA = booleanPreferencesKey("use_mobile_data")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            showLocalTime = prefs[Keys.SHOW_LOCAL] ?: true,
            showUtcTime = prefs[Keys.SHOW_UTC] ?: true,
            distanceUnit = prefs[Keys.DISTANCE_UNIT]?.let { runCatching { DistanceUnit.valueOf(it) }.getOrNull() }
                ?: DistanceUnit.KM,
            showGpsOnDashboard = prefs[Keys.SHOW_GPS] ?: true,
            theme = prefs[Keys.THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.DARK,
            autoSyncEnabled = prefs[Keys.AUTO_SYNC] ?: true,
            syncIntervalMinutes = prefs[Keys.SYNC_INTERVAL] ?: 60,
            useMobileData = prefs[Keys.USE_MOBILE_DATA] ?: true,
        )
    }

    suspend fun setShowLocalTime(value: Boolean) = context.dataStore.edit { it[Keys.SHOW_LOCAL] = value }
    suspend fun setShowUtcTime(value: Boolean) = context.dataStore.edit { it[Keys.SHOW_UTC] = value }
    suspend fun setDistanceUnit(value: DistanceUnit) = context.dataStore.edit { it[Keys.DISTANCE_UNIT] = value.name }
    suspend fun setShowGpsOnDashboard(value: Boolean) = context.dataStore.edit { it[Keys.SHOW_GPS] = value }
    suspend fun setTheme(value: AppTheme) = context.dataStore.edit { it[Keys.THEME] = value.name }
    suspend fun setAutoSyncEnabled(value: Boolean) = context.dataStore.edit { it[Keys.AUTO_SYNC] = value }
    suspend fun setSyncIntervalMinutes(value: Int) = context.dataStore.edit { it[Keys.SYNC_INTERVAL] = value }
    suspend fun setUseMobileData(value: Boolean) = context.dataStore.edit { it[Keys.USE_MOBILE_DATA] = value }

    suspend fun clearAll() = context.dataStore.edit { it.clear() }
}
