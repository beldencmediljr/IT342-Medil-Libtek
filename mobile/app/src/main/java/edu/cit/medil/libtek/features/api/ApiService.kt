// FILE: ApiService.kt
package edu.cit.medil.libtek.features.api

import edu.cit.medil.libtek.features.auth.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class DashboardData(
    val currentOccupancy: Int,
    val maxCapacity: Int,
    val hasFine: Boolean,
    val fineAmount: Double,
    val activeBooks: Int,
    val boothsToday: Int,
    val hoursThisWeek: Int,
    val upcomingReservation: UpcomingReservation?,
    val recentActivity: List<ActivityLog>
)

data class UpcomingReservation(
    val location: String,
    val date: String,
    val time: String,
    val status: String
)

data class ActivityLog(
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val type: String
)

data class DashboardResponse(val success: Boolean, val data: DashboardData?)
data class ProfileResponse(val success: Boolean, val data: ProfileData?)

data class ProfileData(
    val activeBookings: Int,
    val studyTimeHours: Int,
    val booksRead: Int,
    val phone: String?,
    val verificationStatus: String
)

interface ApiService {

    // Auth endpoints — backend maps to /api/v1/auth/...
    @POST("/api/v1/auth/login")
    fun login(@Body credentials: Map<String, String>): Call<AuthResponse>

    @POST("/api/v1/auth/register")
    fun register(@Body data: Map<String, String>): Call<AuthResponse>

    // Google auth — these endpoints don't exist in your backend yet.
    // They are declared here so the app compiles. Wire them up when you
    // add Google Sign-In support to the backend.
    @POST("/api/v1/auth/google")
    fun googleLogin(@Body tokenData: Map<String, String>): Call<AuthResponse>

    @POST("/api/v1/auth/google-register")
    fun googleRegister(@Body tokenData: Map<String, String>): Call<AuthResponse>

    // User dashboard/profile — backend maps to /api/v1/user/...
    @GET("/api/v1/user/dashboard")
    fun getDashboardStats(@Header("Authorization") token: String): Call<DashboardResponse>

    @GET("/api/v1/user/profile")
    fun getProfileData(@Header("Authorization") token: String): Call<ProfileResponse>
}