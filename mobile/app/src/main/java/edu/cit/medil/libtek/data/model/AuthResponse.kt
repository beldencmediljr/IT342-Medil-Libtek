package edu.cit.medil.libtek.data.model

data class AuthResponse(
    val success: Boolean,
    val data: AuthData?,
    val error: ErrorData?,
    val timestamp: String
)

data class AuthData(
    val user: UserData?,
    val accessToken: String?,
    val refreshToken: String?
)

data class UserData(
    val id: Long?,              // Changed to Long to match backend
    val email: String,
    val full_name: String,      // Backend uses snake_case in JSON
    val role: String,
    val id_image_url: String?,
    val is_verified: Boolean?
)

data class ErrorData(
    val code: String,
    val message: String,
    val details: Any?
)