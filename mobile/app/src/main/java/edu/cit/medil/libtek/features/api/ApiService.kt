package edu.cit.medil.libtek.features.api

import edu.cit.medil.libtek.features.auth.AuthResponse
import retrofit2.Call
import retrofit2.http.*

data class DashboardData(
    val currentOccupancy: Int,
    val maxCapacity: Int,
    val hasFine: Boolean,
    val fineAmount: Double,
    val activeBooks: Int,
    val boothsToday: Int,
    val hoursThisWeek: Int,
    val upcomingReservation: List<UpcomingReservation>?,
    val recentActivity: List<ActivityLog>
)

data class UpcomingReservation(val location: String, val date: String, val time: String, val status: String)
data class ActivityLog(val title: String, val subtitle: String, val timeAgo: String, val type: String)
data class DashboardResponse(val success: Boolean, val data: DashboardData?)
data class ProfileResponse(val success: Boolean, val data: ProfileData?)

data class ProfileData(
    val activeBookings: Int,
    val studyTimeHours: Int,
    val booksRead: Int,
    val phone: String?,
    val verificationStatus: String
)

data class ResourceDto(val id: Long, val type: String?, val name: String?, val author: String?, val isbn: String?, val category: String?, val capacity: String?, val location: String?, val available: Boolean)
data class ReservationDto(val id: Long, val studentName: String?, val resourceType: String?, val resourceName: String?, val reservationDate: String?, val status: String?)

data class FineDto(
    val id: Long,
    val studentName: String?,
    val studentId: String?,
    val resourceName: String?,
    val daysOverdue: Int,
    val amount: Double,
    val status: String?,
    val receiptNumber: String?,
    val notes: String?
)

interface ApiService {
    @POST("/api/v1/auth/login")
    fun login(@Body credentials: Map<String, String>): Call<AuthResponse>

    @POST("/api/v1/auth/register")
    fun register(@Body data: Map<String, String>): Call<AuthResponse>

    @POST("/api/v1/auth/google")
    fun googleLogin(@Body tokenData: Map<String, String>): Call<AuthResponse>

    @GET("/api/v1/user/dashboard")
    fun getDashboardStats(@Header("Authorization") token: String): Call<DashboardResponse>

    @GET("/api/v1/user/profile")
    fun getProfileData(@Header("Authorization") token: String): Call<ProfileResponse>

    @PUT("/api/v1/user/profile/update")
    fun updateProfileData(@Header("Authorization") token: String, @Body payload: Map<String, String>): Call<Map<String, Any>>

    @PUT("/api/v1/user/profile/change-password")
    fun changePassword(@Header("Authorization") token: String, @Body payload: Map<String, String>): Call<Map<String, Any>>

    @GET("/api/resources")
    fun getResources(): Call<List<ResourceDto>>

    @GET("/api/v1/user/profile/reservations")
    fun getUserReservations(@Header("Authorization") token: String): Call<List<ReservationDto>>

    @POST("/api/reservations")
    fun createReservation(@Header("Authorization") token: String, @Body payload: Map<String, String>): Call<ReservationDto>

    @PUT("/api/reservations/{id}/status")
    fun updateReservationStatus(@Header("Authorization") token: String, @Path("id") id: Long, @Body payload: Map<String, String>): Call<ReservationDto>

    @POST("/api/verifications")
    fun uploadIdVerification(@Header("Authorization") token: String, @Body payload: Map<String, String>): Call<Map<String, Any>>

    @GET("/api/v1/user/profile/fines")
    fun getUserFines(@Header("Authorization") token: String): Call<List<FineDto>>
}