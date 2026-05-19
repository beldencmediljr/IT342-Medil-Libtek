package edu.cit.medil.libtek.features.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.features.api.ApiClient
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(tokenManager: TokenManager, onBack: () -> Unit) {
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF9FAFB)).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = oldPassword, onValueChange = { oldPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newPassword, onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFF7F1D1D))
                    } else {
                        Button(
                            onClick = {
                                if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                                    isProcessing = true
                                    val payload = mapOf("oldPassword" to oldPassword, "newPassword" to newPassword)
                                    ApiClient.apiService.changePassword("Bearer ${tokenManager.getAccessToken()}", payload).enqueue(object : Callback<Map<String, Any>> {
                                        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                            isProcessing = false
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                                                onBack()
                                            } else {
                                                Toast.makeText(context, "Incorrect current password", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                            isProcessing = false
                                            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                } else {
                                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Update Password", color = Color.White, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}