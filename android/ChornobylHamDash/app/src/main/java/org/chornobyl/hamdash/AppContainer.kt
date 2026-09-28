package org.chornobyl.hamdash

import android.content.Context
import org.chornobyl.hamdash.data.AssetDataSource
import org.chornobyl.hamdash.data.LocalDataSource
import org.chornobyl.hamdash.data.RemoteDataSource
import org.chornobyl.hamdash.data.repository.RadioDataRepository
import org.chornobyl.hamdash.data.repository.RadioDataRepositoryImpl
import org.chornobyl.hamdash.database.HamDashDatabase
import org.chornobyl.hamdash.location.LocationManagerWrapper
import org.chornobyl.hamdash.network.ConnectivityObserver
import org.chornobyl.hamdash.network.NetworkModule
import org.chornobyl.hamdash.settings.SettingsManager

/**
 * Minimal hand-rolled dependency container (no DI framework needed for this app's
 * size). Built once in [HamDashApplication] and handed down to ViewModels.
 */
class AppContainer(context: Context) {
    private val database = HamDashDatabase.getInstance(context)
    private val localDataSource = LocalDataSource(database)
    private val remoteDataSource = RemoteDataSource(NetworkModule.radioApiService)
    private val assetDataSource = AssetDataSource(context)

    val radioDataRepository: RadioDataRepository =
        RadioDataRepositoryImpl(localDataSource, remoteDataSource, assetDataSource)

    val settingsManager = SettingsManager(context)
    val locationManagerWrapper = LocationManagerWrapper(context)
    val connectivityObserver = ConnectivityObserver(context)
}
