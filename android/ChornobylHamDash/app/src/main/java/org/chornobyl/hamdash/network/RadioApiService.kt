package org.chornobyl.hamdash.network

import org.chornobyl.hamdash.data.model.DigitalModeDto
import retrofit2.http.GET

/**
 * Retrofit contract for a future real amateur-radio data backend. [org.chornobyl.hamdash.BuildConfig.MOCK_API_BASE_URL]
 * is a placeholder that resolves to nothing today, so calls through this interface fail
 * fast and [org.chornobyl.hamdash.data.repository.RadioDataRepositoryImpl] falls back to
 * the local Room-backed data. Swapping in a real API only means pointing the base URL at
 * one and, if needed, adjusting field names here — no other layer changes.
 * Repeaters, talkgroups and propagation are not part of this backend; they come live
 * from [HearhamApiService], [BrandmeisterApiService] and [SwpcApiService]. Bands come
 * from the IARU Region 1 band plans bundled with the app.
 */
interface RadioApiService {
    @GET("v1/digital-modes")
    suspend fun getDigitalModes(): List<DigitalModeDto>
}
