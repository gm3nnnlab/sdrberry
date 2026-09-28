package org.chornobyl.hamdash.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.chornobyl.hamdash.data.model.DigitalModeDto
import org.chornobyl.hamdash.data.model.HearhamRepeaterDto
import org.chornobyl.hamdash.data.model.PropagationDto
import org.chornobyl.hamdash.data.model.RepeaterDto
import org.chornobyl.hamdash.data.model.TalkgroupDto
import org.chornobyl.hamdash.network.BrandmeisterApiService
import org.chornobyl.hamdash.network.HearhamApiService
import org.chornobyl.hamdash.network.RadioApiService
import org.chornobyl.hamdash.network.SwpcApiService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Thin, failure-tolerant wrapper around the network APIs. Every call returns a
 * [Result] so the repository can fall back to local data without special-casing
 * network exceptions itself. The MVP's mock base URL never resolves, so the
 * [RadioApiService] calls are expected to fail today — that failure path is
 * intentional and exercised. Repeaters come live from HearHam, talkgroups from
 * BrandMeister and propagation from NOAA SWPC.
 */
class RemoteDataSource(
    private val api: RadioApiService,
    private val swpc: SwpcApiService,
    private val hearham: HearhamApiService,
    private val brandmeister: BrandmeisterApiService,
    private val json: Json,
) {
    /** Streams the worldwide list so the ~9.5 MB body is never held as one string. */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun fetchRepeaters(): Result<List<RepeaterDto>> = fetch {
        val entries = withContext(Dispatchers.IO) {
            hearham.getRepeaters().use { body ->
                body.byteStream().use { json.decodeFromStream<List<HearhamRepeaterDto>>(it) }
            }
        }
        HearhamRepeaterMapper.map(entries, LocalDate.now(ZoneOffset.UTC))
    }

    suspend fun fetchDigitalModes(): Result<List<DigitalModeDto>> = fetch { api.getDigitalModes() }
    suspend fun fetchTalkgroups(): Result<List<TalkgroupDto>> =
        fetch { BrandmeisterTalkgroupMapper.map(brandmeister.getTalkgroups()) }

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
