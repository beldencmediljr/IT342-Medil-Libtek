package edu.cit.medil.libtek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegName: EditText
    private lateinit var etRegEmail: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var etRegConfirmPassword: EditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnGoogleSignUp: MaterialButton
    private lateinit var tvGoToLogin: TextView

    private lateinit var tokenManager: TokenManager
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "RegisterActivity"
        private const val RC_GOOGLE_SIGN_UP = 9002
        private const val MIN_PASSWORD_LENGTH = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tokenManager = TokenManager(this)
        initializeViews()
        setupGoogleSignIn()
        setupListeners()
    }

    private fun initializeViews() {
        etRegName = findViewById(R.id.etRegName)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            if (validateInputs()) {
                performRegistration()
            }
        }

        btnGoogleSignUp.setOnClickListener {
            performGoogleSignUp()
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val name = etRegName.text.toString().trim()
        val email = etRegEmail.text.toString().trim()
        val password = etRegPassword.text.toString().trim()
        val confirmPassword = etRegConfirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            etRegName.error = "Full name is required"
            return false
        }

        if (email.isEmpty()) {
            etRegEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.error = "Please enter a valid email format"
            return false
        }

        if (password.isEmpty()) {
            etRegPassword.error = "Password is required"
            return false
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            etRegPassword.error = "Password must be at least $MIN_PASSWORD_LENGTH characters"
            return false
        }

        if (password != confirmPassword) {
            etRegConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun performRegistration() {
        val name = etRegName.text.toString().trim()
        val email = etRegEmail.text.toString().trim()
        val password = etRegPassword.text.toString().trim()

        Log.d(TAG, "Registering: name=$name, email=$email")

        // FIXED: Match backend field names exactly
        val user = mapOf(
            "fullName" to name,      // Backend expects camelCase in JSON
            "email" to email,
            "password" to password,   // Backend has getPassword()/setPassword()
            "role" to "USER"
        )

        ApiClient.apiService.register(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response body: ${response.body()}")
                Log.d(TAG, "Error body: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@RegisterActivity,
                        "Registration Successful! Please login.",
                        Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorMsg = response.body()?.error?.message
                        ?: "Registration Failed (Code: ${response.code()})"
                    Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.message}", t)
                Toast.makeText(this@RegisterActivity,
                    "Network Error: ${t.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performGoogleSignUp() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_UP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_UP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                idToken?.let { sendGoogleTokenToBackend(it) }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-Up failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendGoogleTokenToBackend(idToken: String) {
        val googleAuth = mapOf("idToken" to idToken, "role" to "USER")

        ApiClient.apiService.googleRegister(googleAuth).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        tokenManager.saveAuthData(it)
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Google Sign-Up Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}