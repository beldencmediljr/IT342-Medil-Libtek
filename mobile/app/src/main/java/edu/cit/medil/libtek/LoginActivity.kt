package edu.cit.medil.libtek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import edu.cit.medil.libtek.api.ApiClient
import edu.cit.medil.libtek.data.model.AuthResponse
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogleSignIn: MaterialButton
    private lateinit var tvGoToRegister: TextView
    private lateinit var tvForgotPassword: TextView

    private lateinit var tokenManager: TokenManager
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_GOOGLE_SIGN_IN = 9001
        private const val MIN_PASSWORD_LENGTH = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        if (tokenManager.isLoggedIn() && tokenManager.isStudent()) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_login)
        initializeViews()
        setupGoogleSignIn()
        setupListeners()
    }

    private fun initializeViews() {
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        btnGoogleSignIn.setOnClickListener {
            performGoogleSignIn()
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Please contact library staff", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        if (email.isEmpty()) {
            etLoginEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.error = "Please enter a valid email format"
            return false
        }

        if (password.isEmpty()) {
            etLoginPassword.error = "Password is required"
            return false
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            etLoginPassword.error = "Password must be at least $MIN_PASSWORD_LENGTH characters"
            return false
        }

        return true
    }

    private fun performLogin() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        Log.d(TAG, "Logging in: email=$email")

        val credentials = mapOf("email" to email, "password" to password)

        ApiClient.apiService.login(credentials).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response body: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()?.data

                    authData?.let {
                        when (it.user?.role) {
                            "USER", "STUDENT" -> {
                                tokenManager.saveAuthData(it)
                                Toast.makeText(this@LoginActivity,
                                    "Welcome, ${it.user?.full_name}!",
                                    Toast.LENGTH_SHORT).show()
                                navigateToMain()
                            }
                            "ADMIN" -> {
                                showAdminRedirectDialog()
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity,
                                    "Unknown role: ${it.user?.role}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    val errorMsg = response.body()?.error?.message
                        ?: "Login Failed (Code: ${response.code()})"
                    Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.message}", t)
                Toast.makeText(this@LoginActivity,
                    "Network Error: ${t.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                idToken?.let { sendGoogleTokenToBackend(it) }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendGoogleTokenToBackend(idToken: String) {
        val googleAuth = mapOf("idToken" to idToken)

        ApiClient.apiService.googleLogin(googleAuth).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        tokenManager.saveAuthData(it)
                        navigateToMain()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Google Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAdminRedirectDialog() {
        AlertDialog.Builder(this)
            .setTitle("Admin Access Detected")
            .setMessage("Administrators must use the LibTek Web Portal.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}