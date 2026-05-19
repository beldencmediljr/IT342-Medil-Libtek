package edu.cit.medil.libtek.features.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.features.api.ApiClient
import edu.cit.medil.libtek.features.api.ReservationDto
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(tokenManager: TokenManager, onNavigateToCatalog: () -> Unit) {
    var activeTab by remember { mutableStateOf("upcoming") }
    var reservations by remember { mutableStateOf<List<ReservationDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun fetchReservations() {
        isLoading = true
        val token = "Bearer ${tokenManager.getAccessToken()}"
        ApiClient.apiService.getUserReservations(token).enqueue(object : Callback<List<ReservationDto>> {
            override fun onResponse(call: Call<List<ReservationDto>>, response: Response<List<ReservationDto>>) {
                if (response.isSuccessful) reservations = response.body() ?: emptyList()
                isLoading = false
            }
            override fun onFailure(call: Call<List<ReservationDto>>, t: Throwable) { isLoading = false }
        })
    }

    LaunchedEffect(Unit) { fetchReservations() }

    val upcomingList = reservations.filter { it.status.equals("ACTIVE", ignoreCase = true) || it.status.equals("PENDING", ignoreCase = true) }
    val pastList = reservations.filter { !it.status.equals("ACTIVE", ignoreCase = true) && !it.status.equals("PENDING", ignoreCase = true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCatalog, // Properly wired to jump to Catalog
                containerColor = Color(0xFF7F1D1D),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Reservation")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF9FAFB)).padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                Text("My Reservations", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp, top = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TabButton("Upcoming (${upcomingList.size})", activeTab == "upcoming", Modifier.weight(1f)) { activeTab = "upcoming" }
                    TabButton("Past (${pastList.size})", activeTab == "past", Modifier.weight(1f)) { activeTab = "past" }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7F1D1D)) }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    val listToDisplay = if (activeTab == "upcoming") upcomingList else pastList
                    if (listToDisplay.isEmpty()) {
                        item { Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text("No reservations found.", color = Color.Gray) } }
                    } else {
                        items(listToDisplay) { res ->
                            if (activeTab == "upcoming") {
                                UpcomingCard(res = res, tokenManager = tokenManager, onStatusChanged = { fetchReservations() })
                            } else {
                                PastCard(res)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isActive: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isActive) Color(0xFF7F1D1D) else Color(0xFFF3F4F6), contentColor = if (isActive) Color.White else Color.Gray),
        shape = RoundedCornerShape(8.dp)
    ) { Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
}

@Composable
fun UpcomingCard(res: ReservationDto, tokenManager: TokenManager, onStatusChanged: () -> Unit) {
    var isCanceling by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF7F1D1D)).padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(res.resourceName ?: "Resource", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(Icons.Default.DateRange, "Date", res.reservationDate ?: "Unknown Date")
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(Icons.Default.LocationOn, "Type", res.resourceType ?: "Unknown Type")

                Spacer(modifier = Modifier.height(16.dp))

                if (isCanceling) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFFDC2626))
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                isCanceling = true
                                val payload = mapOf("status" to "CANCELLED")
                                val token = "Bearer ${tokenManager.getAccessToken()}"
                                ApiClient.apiService.updateReservationStatus(token, res.id, payload).enqueue(object : Callback<ReservationDto> {
                                    override fun onResponse(call: Call<ReservationDto>, response: Response<ReservationDto>) {
                                        isCanceling = false
                                        if (response.isSuccessful) onStatusChanged()
                                    }
                                    override fun onFailure(call: Call<ReservationDto>, t: Throwable) { isCanceling = false }
                                })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Cancel Booking", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}

@Composable
fun PastCard(res: ReservationDto) {
    val statusColor = if (res.status.equals("COMPLETED", ignoreCase = true)) Color.Gray else Color(0xFFDC2626)
    val bgColor = if (res.status.equals("COMPLETED", ignoreCase = true)) Color(0xFFF3F4F6) else Color(0xFFFEF2F2)

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(res.resourceName ?: "Resource", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(res.reservationDate ?: "Unknown Date", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                }
                Box(modifier = Modifier.background(bgColor, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = res.status?.uppercase() ?: "UNKNOWN", color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = Color(0xFF7F1D1D), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}