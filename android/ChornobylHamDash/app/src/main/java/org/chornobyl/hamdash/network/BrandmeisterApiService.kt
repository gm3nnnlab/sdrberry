package org.chornobyl.hamdash.network

import retrofit2.http.GET

/** BrandMeister DMR network's public API. */
interface BrandmeisterApiService {
    /** Every talkgroup on the network as a map of talkgroup ID to name. */
    @GET("v2/talkgroup")
    suspend fun getTalkgroups(): Map<String, String>
}
