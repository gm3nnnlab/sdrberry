package org.chornobyl.hamdash.data

import kotlinx.coroutines.CancellationException
import org.chornobyl.hamdash.data.model.BandDto
import org.chornobyl.hamdash.data.model.DigitalModeDto
import org.chornobyl.hamdash.data.model.PropagationDto
import org.chornobyl.hamdash.data.model.RepeaterDto
import org.chornobyl.hamdash.data.model.TalkgroupDto
import org.chornobyl.hamdash.network.RadioApiService

/**
 * Thin, failure-tolerant wrapper around [RadioApiService]. Every call returns a
 * [Result] so the repository can fall back to local data without special-casing
 * network exceptions itself. The MVP's mock base URL never resolves, so these calls
 * are expected to fail today — that failure path is intentional and exercised.
 */
class RemoteDataSource(private val api: RadioApiService) {
    suspend fun fetchRepeaters(): Result<List<RepeaterDto>> = fetch { api.getRepeaters() }
    suspend fun fetchBands(): Result<List<BandDto>> = fetch { api.getBands() }
    suspend fun fetchDigitalModes(): Result<List<DigitalModeDto>> = fetch { api.getDigitalModes() }
    suspend fun fetchTalkgroups(): Result<List<TalkgroupDto>> = fetch { api.getTalkgroups() }
    suspend fun fetchPropagation(): Result<PropagationDto> = fetch { api.getPropagation() }

    private suspend fun <T> fetch(call: suspend () -> T): Result<T> =
        try {
            Result.success(call())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
