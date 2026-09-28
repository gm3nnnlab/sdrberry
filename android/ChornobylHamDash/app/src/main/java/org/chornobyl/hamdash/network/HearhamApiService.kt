package org.chornobyl.hamdash.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

/** HearHam.live's public repeater list, free to use in apps (worldwide, ~9.5 MB, uncompressed). */
interface HearhamApiService {
    @Streaming
    @GET("api/repeaters/v1")
    suspend fun getRepeaters(): ResponseBody
}
