package edu.cit.medil.libtek.features.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import edu.cit.medil.libtek.features.api.ApiClient

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen(
                onRegisterSuccess = {
                    Toast.makeText(
                        this,
                        "Registration Successful! Please login.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                },
                onLoginClick = {
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7F1D1D))
                    .border(5.dp, Color(0xFFCA8A04), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LIB\nTEK",
                    color = Color(0xFFCA8A04),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .background(
                    Color(0xFF7F1D1D),
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CREATE ACCOUNT",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; fullNameError = null },
                placeholder = { Text("Full Name", color = Color.Gray) },
                isError = fullNameError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            fullNameError?.let {
                Text(
                    it,
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                placeholder = { Text("Email Address", color = Color.Gray) },
                isError = emailError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            emailError?.let {
                Text(
                    it,
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                placeholder = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            passwordError?.let {
                Text(
                    it,
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                placeholder = { Text("Confirm Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            confirmPasswordError?.let {
                Text(
                    it,
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    var hasError = false
                    if (fullName.trim().isEmpty()) {
                        fullNameError = "Full name is required"; hasError = true
                    }
                    if (email.trim().isEmpty()) {
                        emailError = "Email is required"; hasError = true
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        emailError = "Please enter a valid email"; hasError = true
                    }
                    if (password.isEmpty()) {
                        passwordError = "Password is required"; hasError = true
                    } else if (password.length < 6) {
                        passwordError = "Minimum 6 characters"; hasError = true
                    }
                    if (password != confirmPassword) {
                        confirmPasswordError = "Passwords do not match"; hasError = true
                    }

                    if (!hasError) {
                        isLoading = true
                        val payload = mapOf(
                            "fullName" to fullName.trim(),
                            "email" to email.trim(),
                            "password" to password,
                            "role" to "USER"
                        )

                        ApiClient.apiService.register(payload)
                            .enqueue(object : Callback<AuthResponse> {
                                override fun onResponse(
                                    call: Call<AuthResponse>,
                                    response: Response<AuthResponse>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        onRegisterSuccess()
                                    } else {
                                        val msg = response.body()?.error?.message
                                            ?: "Registration Failed"
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Network Error: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(25.dp),
                enabled = !isLoading
            ) {
                Text(
                    if (isLoading) "Creating Account..." else "Sign Up",
                    color = Color(0xFF7F1D1D),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Text("Already have an account? ", color = Color.White, fontSize = 14.sp)
                Text(
                    text = "Login",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}