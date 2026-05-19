package edu.cit.medil.libtek.features.auth

data class User(
    val id: Long?,
    val full_name: String?,
    val email: String?,
    val role: String?,
    val is_verified: Boolean? = false
)

data class AuthData(
    val accessToken: String?,
    val refreshToken: String?,
    val user: User?
)

data class ApiError(
    val message: String?
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?,
    val error: ApiError?
)