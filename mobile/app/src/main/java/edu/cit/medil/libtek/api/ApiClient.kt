package edu.cit.medil.libtek.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Change this to your actual backend URL
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/" // For local Spring Boot with Android Emulator
    // OR: "http://192.168.1.xxx:8080/api/v1/" // For real device on same WiFi
    // OR: "https://your-deployed-api.com/api/v1/" // For production

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