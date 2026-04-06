package edu.cit.medil.libtek.api

import edu.cit.medil.libtek.data.model.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    fun register(@Body user: Map<String, String>): Call<AuthResponse>

    @POST("auth/login")
    fun login(@Body credentials: Map<String, String>): Call<AuthResponse>

    @POST("auth/google")
    fun googleLogin(@Body googleAuth: Map<String, String>): Call<AuthResponse>

    @POST("auth/google-register")
    fun googleRegister(@Body googleAuth: Map<String, String>): Call<AuthResponse>

    @GET("auth/me")
    fun validateSession(@Header("Authorization") token: String): Call<AuthResponse>
}