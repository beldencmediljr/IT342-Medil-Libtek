package edu.cit.medil.libtek.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.features.api.ActivityLog
import edu.cit.medil.libtek.features.api.ApiClient
import edu.cit.medil.libtek.features.api.DashboardResponse
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(tokenManager: TokenManager, onBack: () -> Unit) {
    var logs by remember { mutableStateOf<List<ActivityLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        ApiClient.apiService.getDashboardStats("Bearer ${tokenManager.getAccessToken()}").enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful) logs = response.body()?.data?.recentActivity ?: emptyList()
                isLoading = false
            }
            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) { isLoading = false }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF9FAFB))) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7F1D1D)) }
            } else if (logs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No new notifications.", color = Color.Gray) }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(logs) { log ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF7F1D1D))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(log.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(log.subtitle, fontSize = 12.sp, color = Color.DarkGray)
                                    Text(log.timeAgo, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}