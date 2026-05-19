// FILE: ApiClient.kt
package edu.cit.medil.libtek.features.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Base URL must be ONLY the host — no path suffix
    // For emulator talking to local machine:
    private const val BASE_URL = "http://10.0.2.2:8080/"
    // For real device on same WiFi, replace with your machine's LAN IP:
    // private const val BASE_URL = "http://192.168.1.XXX:8080/"
    // For production:
    // private const val BASE_URL = "https://your-deployed-api.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}