package edu.cit.medil.libtek.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.R
import edu.cit.medil.libtek.features.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen(
                onRegisterSuccess = {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onLoginClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onLoginClick: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.35f).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "LibTek Logo",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)
                .background(Color(0xFF7F1D1D), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("REGISTER", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

            OutlinedTextField(
                value = fullName, onValueChange = { fullName = it },
                placeholder = { Text("Full Name", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Email Address", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && fullName.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        val payload = mapOf("email" to email, "fullName" to fullName, "password" to password, "role" to "USER")
                        ApiClient.apiService.register(payload).enqueue(object : Callback<AuthResponse> {
                            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                                isLoading = false
                                if (response.isSuccessful && response.body()?.success == true) {
                                    onRegisterSuccess()
                                } else {
                                    val err = response.body()?.error?.message ?: "Registration Failed"
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                isLoading = false
                                Toast.makeText(context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(25.dp),
                enabled = !isLoading
            ) { Text(if (isLoading) "Processing..." else "Register", color = Color(0xFF7F1D1D), fontWeight = FontWeight.Bold) }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Already have an account? ", color = Color.White, fontSize = 14.sp)
                Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { onLoginClick() })
            }
        }
    }
}