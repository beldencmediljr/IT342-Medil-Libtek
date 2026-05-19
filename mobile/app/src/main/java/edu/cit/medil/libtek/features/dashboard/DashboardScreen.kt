package edu.cit.medil.libtek.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.features.api.ApiClient
import edu.cit.medil.libtek.features.api.DashboardResponse
import edu.cit.medil.libtek.features.api.DashboardData
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DashboardScreen(
    tokenManager: TokenManager,
    onNavigateToCatalog: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    var dashboardData by remember { mutableStateOf<DashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = "Bearer ${tokenManager.getAccessToken()}"
        ApiClient.apiService.getDashboardStats(token).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful) dashboardData = response.body()?.data
                isLoading = false
            }
            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) { isLoading = false }
        })
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF7F1D1D)), contentAlignment = Alignment.Center) {
                    Text(text = "L", color = Color(0xFFCA8A04), fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Welcome, ${tokenManager.getUserName() ?: "Student"}!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                    Text("Student ID: ${tokenManager.getUserId()}", fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onNavigateToNotifications) {
                Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color(0xFFCA8A04))
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp), color = Color(0xFF7F1D1D))
            return@Column
        }

        if (dashboardData?.hasFine == true) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp)).padding(16.dp)) {
                Column {
                    Text("! Outstanding Fine", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    Text("Please settle your balance of PHP ${dashboardData?.fineAmount} at the University Accounting Office.", color = Color(0xFFDC2626), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Live Occupancy", fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                    Text("● Live", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text("${dashboardData?.currentOccupancy ?: 0}/${dashboardData?.maxCapacity ?: 100}", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7F1D1D), modifier = Modifier.padding(top = 16.dp))
                Text("Current Occupancy", color = Color.Gray, fontSize = 12.sp)
                LinearProgressIndicator(progress = (dashboardData?.currentOccupancy ?: 0).toFloat() / (dashboardData?.maxCapacity ?: 100).toFloat(), modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(8.dp).clip(RoundedCornerShape(50)), color = Color(0xFF10B981), trackColor = Color(0xFFF3F4F6))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCard("Active Books", "${dashboardData?.activeBooks ?: 0}", Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatCard("Booth Today", "${dashboardData?.boothsToday ?: 0}", Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatCard("This Week", "${dashboardData?.hoursThisWeek ?: 0}h", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Upcoming Today", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(horizontal = 16.dp))
        dashboardData?.upcomingReservation?.let { res ->
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(res.location, color = Color.White, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.background(Color(0xFF10B981), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(res.status, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                    Text("${res.date}  |  ${res.time}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        } ?: Text("No upcoming bookings.", color = Color.Gray, modifier = Modifier.padding(16.dp))

        Text("Quick Actions", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(horizontal = 16.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onNavigateToCatalog, modifier = Modifier.weight(1f).height(48.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7F1D1D)), shape = RoundedCornerShape(8.dp)) { Text("Book a Booth", color = Color(0xFF7F1D1D)) }
            Button(onClick = onNavigateToCatalog, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)), shape = RoundedCornerShape(8.dp)) { Text("Browse Books", color = Color.White) }
        }

        Text("Recent Activity", fontWeight = FontWeight.Bold, color = Color(0xFF003366), modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp))
        dashboardData?.recentActivity?.let { logs ->
            if (logs.isEmpty()) { Text("No recent activities found.", color = Color.Gray, modifier = Modifier.padding(16.dp)) } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    logs.forEach { log ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF7F1D1D))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(log.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${log.subtitle} • ${log.timeAgo}", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003366))
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}