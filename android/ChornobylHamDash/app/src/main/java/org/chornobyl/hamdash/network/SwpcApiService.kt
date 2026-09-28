package org.chornobyl.hamdash.network

import org.chornobyl.hamdash.data.model.SwpcFlareDto
import org.chornobyl.hamdash.data.model.SwpcFluxDto
import org.chornobyl.hamdash.data.model.SwpcKpDto
import retrofit2.http.GET

/** Public-domain space-weather feeds from NOAA's Space Weather Prediction Center. */
interface SwpcApiService {
    @GET("products/summary/10cm-flux.json")
    suspend fun getSolarFlux(): List<SwpcFluxDto>

    @GET("products/noaa-planetary-k-index.json")
    suspend fun getPlanetaryKIndex(): List<SwpcKpDto>

    @GET("json/goes/primary/xray-flares-7-day.json")
    suspend fun getXrayFlares(): List<SwpcFlareDto>
}
