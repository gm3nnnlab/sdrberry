package org.chornobyl.hamdash.network

import org.chornobyl.hamdash.data.model.BandDto
import org.chornobyl.hamdash.data.model.DigitalModeDto
import org.chornobyl.hamdash.data.model.TalkgroupDto
import retrofit2.http.GET

/**
 * Retrofit contract for a future real amateur-radio data backend. [org.chornobyl.hamdash.BuildConfig.MOCK_API_BASE_URL]
 * is a placeholder that resolves to nothing today, so calls through this interface fail
 * fast and [org.chornobyl.hamdash.data.repository.RadioDataRepositoryImpl] falls back to
 * the local Room-backed data. Swapping in a real API only means pointing the base URL at
 * one and, if needed, adjusting field names here — no other layer changes.
 * Repeaters and propagation are not part of this backend; they come live from
 * [HearhamApiService] and [SwpcApiService].
 */
interface RadioApiService {
    @GET("v1/bands")
    suspend fun getBands(): List<BandDto>

    @GET("v1/digital-modes")
    suspend fun getDigitalModes(): List<DigitalModeDto>

    @GET("v1/talkgroups")
    suspend fun getTalkgroups(): List<TalkgroupDto>
}
