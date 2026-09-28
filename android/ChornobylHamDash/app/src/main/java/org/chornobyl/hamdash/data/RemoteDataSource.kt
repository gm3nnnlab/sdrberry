package org.chornobyl.hamdash.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.chornobyl.hamdash.data.model.BandDto
import org.chornobyl.hamdash.data.model.DigitalModeDto
import org.chornobyl.hamdash.data.model.PropagationDto
import org.chornobyl.hamdash.data.model.RepeaterDto
import org.chornobyl.hamdash.data.model.TalkgroupDto
import org.chornobyl.hamdash.network.RadioApiService
import org.chornobyl.hamdash.network.SwpcApiService
import java.time.Instant

/**
 * Thin, failure-tolerant wrapper around the network APIs. Every call returns a
 * [Result] so the repository can fall back to local data without special-casing
 * network exceptions itself. The MVP's mock base URL never resolves, so the
 * [RadioApiService] calls are expected to fail today — that failure path is
 * intentional and exercised. Propagation comes live from NOAA SWPC.
 */
class RemoteDataSource(
    private val api: RadioApiService,
    private val swpc: SwpcApiService,
) {
    suspend fun fetchRepeaters(): Result<List<RepeaterDto>> = fetch { api.getRepeaters() }
    suspend fun fetchBands(): Result<List<BandDto>> = fetch { api.getBands() }
    suspend fun fetchDigitalModes(): Result<List<DigitalModeDto>> = fetch { api.getDigitalModes() }
    suspend fun fetchTalkgroups(): Result<List<TalkgroupDto>> = fetch { api.getTalkgroups() }

    /** Fails only when every SWPC feed failed; a partial answer leaves the missing fields null. */
    suspend fun fetchPropagation(): Result<PropagationDto> = coroutineScope {
        val flux = async { fetch { swpc.getSolarFlux() } }
        val kp = async { fetch { swpc.getPlanetaryKIndex() } }
        val flares = async { fetch { swpc.getXrayFlares() } }
        val fluxResult = flux.await()
        val kpResult = kp.await()
        val flaresResult = flares.await()
        val snapshot = SwpcPropagationMapper.map(
            flux = fluxResult.getOrNull(),
            kp = kpResult.getOrNull(),
            flares = flaresResult.getOrNull(),
            now = Instant.now(),
        )
        if (snapshot != null) {
            Result.success(snapshot)
        } else {
            Result.failure(fluxResult.exceptionOrNull() ?: IllegalStateException("No SWPC feed returned data"))
        }
    }

    private suspend fun <T> fetch(call: suspend () -> T): Result<T> =
        try {
            Result.success(call())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
