package edu.cit.medil.libtek.util

import android.content.Context
import android.content.SharedPreferences
import edu.cit.medil.libtek.features.auth.AuthData
import edu.cit.medil.libtek.features.auth.User

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "LibTekAuth"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_VERIFIED = "is_verified"
        private const val KEY_ID_IMAGE_URL = "id_image_url"
    }

    fun saveAuthData(authData: AuthData) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, authData.accessToken)
            putString(KEY_REFRESH_TOKEN, authData.refreshToken)
            authData.user?.let { user ->
                putString(KEY_USER_ID, user.id?.toString() ?: "0")
                putString(KEY_USER_EMAIL, user.email)
                putString(KEY_USER_NAME, user.full_name)
                putString(KEY_USER_ROLE, user.role)
                putBoolean(KEY_IS_VERIFIED, user.is_verified ?: false)
                putString(KEY_ID_IMAGE_URL, user.id_image_url ?: "")
            }
            apply()
        }
    }

    // THE FIX: Added the missing function to update the user's name locally after editing their profile
    fun updateStoredName(newName: String) {
        prefs.edit().putString(KEY_USER_NAME, newName).apply()
    }

    fun getAuthData(): AuthData? {
        val token = getAccessToken()
        if (token == null) return null

        val user = User(
            id = getUserId(),
            email = getUserEmail(),
            full_name = getUserName(),
            role = getUserRole(),
            id_image_url = getIdImageUrl(),
            is_verified = isVerified()
        )
        return AuthData(
            accessToken = token,
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null),
            user = user
        )
    }

    fun isStudent(): Boolean = getUserRole() == "USER" || getUserRole() == "STUDENT"
    fun isAdmin(): Boolean = getUserRole() == "ADMIN"
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getUserId(): Long = prefs.getString(KEY_USER_ID, "0")?.toLongOrNull() ?: 0L
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)
    fun isVerified(): Boolean = prefs.getBoolean(KEY_IS_VERIFIED, false)
    fun getIdImageUrl(): String? = prefs.getString(KEY_ID_IMAGE_URL, null)

    fun clearAuthData() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null
}