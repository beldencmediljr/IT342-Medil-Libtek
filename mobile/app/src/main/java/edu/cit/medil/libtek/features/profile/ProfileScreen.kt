package edu.cit.medil.libtek.features.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Lock
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

@Composable
fun ProfileScreen(
    tokenManager: TokenManager,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var profileData by remember { mutableStateOf<ProfileData?>(null) }
    var mutableName by remember { mutableStateOf(tokenManager.getUserName() ?: "") }
    var mutablePhone by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    fun loadProfile() {
        ApiClient.apiService.getProfileData("Bearer ${tokenManager.getAccessToken()}").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    profileData = response.body()?.data
                    profileData?.phone?.let { mutablePhone = it }
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) { }
        })
    }

    LaunchedEffect(Unit) { loadProfile() }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            isUploading = true
            scope.launch(Dispatchers.IO) {
                try {
                    // Step 1: Open stream and decode image bounds safely to prevent OOM errors
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    context.contentResolver.openInputStream(uri).use { stream ->
                        BitmapFactory.decodeStream(stream, null, options)
                    }

                    // Step 2: Calculate scale factor to downsample large modern device cameras (Aim for ~1080p max resolution)
                    val requiredWidth = 1080
                    val requiredHeight = 1080
                    var sampleSize = 1
                    if (options.outWidth > requiredWidth || options.outHeight > requiredHeight) {
                        val halfWidth = options.outWidth / 2
                        val halfHeight = options.outHeight / 2
                        while ((halfWidth / sampleSize) >= requiredWidth && (halfHeight / sampleSize) >= requiredHeight) {
                            sampleSize *= 2
                        }
                    }

                    // Step 3: Decode full bitmap using calculated safe memory footprint configuration options
                    val scaleOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = false
                        inSampleSize = sampleSize
                    }

                    val scaledBitmap = context.contentResolver.openInputStream(uri).use { stream ->
                        BitmapFactory.decodeStream(stream, null, scaleOptions)
                    }

                    if (scaledBitmap != null) {
                        // Step 4: Compress downscale bitmap output into standard compressed JPEG structure arrays
                        val outputStream = ByteArrayOutputStream()
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                        val compressedBytes = outputStream.toByteArray()
                        scaledBitmap.recycle() // Explicitly release memory allocations back to system runtime natively

                        // Step 5: Wrap payload utilizing clean inline formatting flags
                        val base64Image = android.util.Base64.encodeToString(compressedBytes, android.util.Base64.NO_WRAP)
                        val payload = mapOf(
                            "studentName" to mutableName,
                            "studentId" to tokenManager.getUserId().toString(),
                            "email" to (tokenManager.getUserEmail() ?: ""),
                            "idImageUrl" to base64Image
                        )

                        withContext(Dispatchers.Main) {
                            ApiClient.apiService.uploadIdVerification("Bearer ${tokenManager.getAccessToken()}", payload)
                                .enqueue(object : Callback<Map<String, Any>> {
                                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                        isUploading = false
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "ID Uploaded successfully. Pending Review.", Toast.LENGTH_LONG).show()
                                            loadProfile()
                                        } else {
                                            Log.e("LibTekUpload", "Server rejected payload with status code response: ${response.code()}")
                                            Toast.makeText(context, "Upload failed at server", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                        isUploading = false
                                        Log.e("LibTekUpload", "Network fail execution path exception caught", t)
                                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            isUploading = false
                            Toast.makeText(context, "Failed to parse picked image file source data structures", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isUploading = false
                        Log.e("LibTekUpload", "Fatal task processing failure tracked during lifecycle execution", e)
                        Toast.makeText(context, "Failed to safely process image source data parameters", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(16.dp))

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
            }
        }

        val status = profileData?.verificationStatus ?: "Not Verified"
        val isVerified = status.equals("Verified", ignoreCase = true)
        val isPendingReview = status.equals("Pending Review", ignoreCase = true)

        val verificationColor = when {
            isVerified -> Color(0xFF10B981)
            isPendingReview -> Color(0xFFF59E0B)
            status.equals("Rejected", ignoreCase = true) -> Color(0xFFDC2626)
            else -> Color.Gray
        }

        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = verificationColor.copy(alpha = 0.1f)), border = androidx.compose.foundation.BorderStroke(1.dp, verificationColor.copy(alpha = 0.3f))) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isVerified) Icons.Default.CheckCircle else Icons.Default.Warning, contentDescription = null, tint = verificationColor)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Verification Status: $status", fontWeight = FontWeight.Bold, color = verificationColor)
                }
                if (!isVerified && !isPendingReview) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF7F1D1D))
                    } else {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) { Text("Upload ID", fontSize = 11.sp, color = Color.White) }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatBox("${profileData?.activeBookings ?: 0}", "Active Bookings", Color(0xFFDC2626), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatBox("${profileData?.studyTimeHours ?: 0}h", "Study Time", Color(0xFF10B981), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatBox("${profileData?.booksRead ?: 0}", "Books Read", Color(0xFF3B82F6), Modifier.weight(1f))
        }

        Text("Account Information", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(horizontal = 16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(value = mutableName, onValueChange = { mutableName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = tokenManager.getUserEmail() ?: "", onValueChange = {}, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = mutablePhone, onValueChange = { mutablePhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFF7F1D1D))
                } else {
                    Button(
                        onClick = {
                            isSaving = true
                            val payload = mapOf("fullName" to mutableName, "phone" to mutablePhone)
                            ApiClient.apiService.updateProfileData("Bearer ${tokenManager.getAccessToken()}", payload).enqueue(object : Callback<Map<String, Any>> {
                                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                    isSaving = false
                                    if (response.isSuccessful) {
                                        tokenManager.updateStoredName(mutableName)
                                        Toast.makeText(context, "Account Changes Saved", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                    isSaving = false
                                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }

        Text("Settings", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(horizontal = 16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                SettingsClickableItem(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "Manage email and push notifications", onClick = onNavigateToNotifications)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsClickableItem(icon = Icons.Default.Lock, title = "Change Password", subtitle = "Update your account password", onClick = onNavigateToChangePassword)
            }
        }

        OutlinedButton(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red), shape = RoundedCornerShape(8.dp)) {
            Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsClickableItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).background(Color(0xFFF3F4F6), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color(0xFF003366), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
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