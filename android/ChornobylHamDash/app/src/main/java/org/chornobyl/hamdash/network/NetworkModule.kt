package org.chornobyl.hamdash.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.chornobyl.hamdash.BuildConfig
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json as KxJson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {
    val json: KxJson by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    val radioApiService: RadioApiService by lazy {
        retrofit(BuildConfig.MOCK_API_BASE_URL).create(RadioApiService::class.java)
    }

    val swpcApiService: SwpcApiService by lazy {
        retrofit(BuildConfig.SWPC_BASE_URL).create(SwpcApiService::class.java)
    }

    val hearhamApiService: HearhamApiService by lazy {
        retrofit(BuildConfig.HEARHAM_BASE_URL).create(HearhamApiService::class.java)
    }

    val brandmeisterApiService: BrandmeisterApiService by lazy {
        retrofit(BuildConfig.BRANDMEISTER_BASE_URL).create(BrandmeisterApiService::class.java)
    }

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
