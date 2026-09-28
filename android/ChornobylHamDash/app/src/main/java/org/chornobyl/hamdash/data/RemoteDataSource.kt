package org.chornobyl.hamdash.data

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
    suspend fun fetchRepeaters(): Result<List<RepeaterDto>> = runCatching { api.getRepeaters() }
    suspend fun fetchBands(): Result<List<BandDto>> = runCatching { api.getBands() }
    suspend fun fetchDigitalModes(): Result<List<DigitalModeDto>> = runCatching { api.getDigitalModes() }
    suspend fun fetchTalkgroups(): Result<List<TalkgroupDto>> = runCatching { api.getTalkgroups() }
    suspend fun fetchPropagation(): Result<PropagationDto> = runCatching { api.getPropagation() }
}
