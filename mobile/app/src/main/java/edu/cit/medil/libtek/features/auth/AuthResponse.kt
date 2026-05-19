package edu.cit.medil.libtek.features.auth

data class AuthResponse(
    val success: Boolean,
    val data: AuthDataPayload?,
    val error: AuthError?
)

data class AuthDataPayload(
    val user: User?,
    val accessToken: String?,
    val refreshToken: String?
)

data class AuthError(
    val code: String?,
    val message: String?
)

data class User(
    val id: Long?,
    val email: String?,
    val full_name: String?,
    val role: String?,
    val id_image_url: String?,
    val is_verified: Boolean?
)

data class AuthData(
    val accessToken: String,
    val refreshToken: String?,
    val user: User?
)