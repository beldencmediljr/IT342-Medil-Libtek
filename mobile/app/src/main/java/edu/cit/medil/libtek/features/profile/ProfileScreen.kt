package edu.cit.medil.libtek.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.features.api.ApiClient
import edu.cit.medil.libtek.features.api.ProfileData
import edu.cit.medil.libtek.features.api.ProfileResponse
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileScreen(onLogoutClick: () -> Unit, onUploadIdClick: () -> Unit) {
    // THE FIX: Extracted LocalContext out of the remember block
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var profileData by remember { mutableStateOf<ProfileData?>(null) }

    LaunchedEffect(Unit) {
        ApiClient.apiService.getProfileData("Bearer ${tokenManager.getAccessToken()}").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) profileData = response.body()?.data
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) { }
        })
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(16.dp))

        // Profile Header
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFF3F4F6)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Gray, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(tokenManager.getUserName() ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF003366))
                        Text("Student ID: ${tokenManager.getUserId()}", fontSize = 12.sp, color = Color.Gray)
                        Text(tokenManager.getUserEmail() ?: "", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onUploadIdClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)), shape = RoundedCornerShape(8.dp)) {
                    Text("Upload Student ID", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Stats
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatBox("${profileData?.activeBookings ?: 0}", "Active Bookings", Color(0xFFDC2626), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatBox("${profileData?.studyTimeHours ?: 0}h", "Study Time", Color(0xFF10B981), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatBox("${profileData?.booksRead ?: 0}", "Books Read", Color(0xFF3B82F6), Modifier.weight(1f))
        }

        // Account Information
        Text("Account Information", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(horizontal = 16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(value = tokenManager.getUserName() ?: "", onValueChange = {}, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = tokenManager.getUserEmail() ?: "", onValueChange = {}, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = profileData?.phone ?: "+63", onValueChange = {}, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)), shape = RoundedCornerShape(8.dp)) {
                    Text("Save Changes")
                }
            }
        }

        // Settings / Logout
        Text("Settings", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(horizontal = 16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Manage email and push notifications", fontSize = 12.sp, color = Color.Gray)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Update your account password", fontSize = 12.sp, color = Color.Gray)
            }
        }

        OutlinedButton(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red), shape = RoundedCornerShape(8.dp)) {
            Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatBox(value: String, label: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
        Column(modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}