package edu.cit.medil.libtek.features.catalog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import edu.cit.medil.libtek.features.api.ResourceDto
import edu.cit.medil.libtek.features.api.ReservationDto
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(tokenManager: TokenManager, onBookingSuccess: () -> Unit) {
    var activeTab by remember { mutableStateOf("books") }
    var searchQuery by remember { mutableStateOf("") }

    var resources by remember { mutableStateOf<List<ResourceDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        isLoading = true
        ApiClient.apiService.getResources().enqueue(object : Callback<List<ResourceDto>> {
            override fun onResponse(call: Call<List<ResourceDto>>, response: Response<List<ResourceDto>>) {
                if (response.isSuccessful) resources = response.body() ?: emptyList()
                isLoading = false
            }
            override fun onFailure(call: Call<List<ResourceDto>>, t: Throwable) { isLoading = false }
        })
    }

    LaunchedEffect(Unit) { loadData() }

    val books = resources.filter { it.type.equals("Book", ignoreCase = true) }
    val booths = resources.filter { it.type.equals("Booth", ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF9FAFB))) {
        Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            Text("Library Catalog", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp, top = 8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search books or booths...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(50.dp).background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFF7F1D1D), unfocusedBorderColor = Color.Transparent),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TabButton("Books", activeTab == "books", Modifier.weight(1f)) { activeTab = "books" }
                TabButton("Booths", activeTab == "booths", Modifier.weight(1f)) { activeTab = "booths" }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().background(Color.White).border(1.dp, Color(0xFFF3F4F6)).padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val size = if (activeTab == "books") books.size else booths.size
            Text(text = "$size results", fontSize = 12.sp, color = Color.Gray)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7F1D1D)) }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                if (activeTab == "books") {
                    items(books.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }) { book ->
                        ResourceCardItem(book, tokenManager) { onBookingSuccess() }
                    }
                } else {
                    items(booths.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }) { booth ->
                        ResourceCardItem(booth, tokenManager) { onBookingSuccess() }
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
fun ResourceCardItem(resource: ResourceDto, tokenManager: TokenManager, onNavigateToBookings: () -> Unit) {
    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Box(modifier = Modifier.width(70.dp).height(100.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE5E7EB)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.List, contentDescription = null, tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(resource.name ?: "Unnamed Resource", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    if (resource.type.equals("Book", ignoreCase = true)) {
                        Text(resource.author ?: "Unknown Author", fontSize = 12.sp, color = Color.DarkGray)
                        Text("ISBN: ${resource.isbn ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(resource.location ?: "Unknown Location", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("Capacity: ${resource.capacity ?: "N/A"}", fontSize = 12.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.background(if (resource.available) Color(0xFFECFDF5) else Color(0xFFFEF2F2), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(text = if (resource.available) "Available" else "Occupied", color = if (resource.available) Color(0xFF047857) else Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFF7F1D1D))
            } else {
                Button(
                    onClick = {
                        isProcessing = true
                        val token = "Bearer ${tokenManager.getAccessToken()}"
                        val payload = mapOf(
                            "studentName" to (tokenManager.getUserName() ?: ""),
                            "resourceName" to (resource.name ?: ""),
                            "resourceType" to (resource.type ?: ""),
                            "reservationDate" to "Today",
                            "status" to "ACTIVE"
                        )
                        ApiClient.apiService.createReservation(token, payload).enqueue(object : Callback<ReservationDto> {
                            override fun onResponse(call: Call<ReservationDto>, response: Response<ReservationDto>) {
                                isProcessing = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Resource Booked Successfully", Toast.LENGTH_SHORT).show()
                                    onNavigateToBookings() // Navigate immediately to bookings view
                                } else {
                                    Toast.makeText(context, "Failed to book resource", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<ReservationDto>, t: Throwable) {
                                isProcessing = false
                                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                            }
                        })
                    },
                    enabled = resource.available,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D), disabledContainerColor = Color(0xFFE5E7EB)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val buttonText = if (resource.type.equals("Book", ignoreCase = true)) "Reserve Book" else "Book Now"
                    Text(if (resource.available) buttonText else "Not Available", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}