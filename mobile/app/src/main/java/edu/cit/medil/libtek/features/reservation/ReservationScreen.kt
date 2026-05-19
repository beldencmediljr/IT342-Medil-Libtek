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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen() {
    var activeTab by remember { mutableStateOf("upcoming") }

    // Dummy data translating your React state
    val upcomingReservations = listOf(
        ReservationData(1, "Study Booth 5", "Today, April 23", "3:00 PM - 5:00 PM", "Floor 2, East Wing", "confirmed"),
        ReservationData(2, "Introduction to Algorithms", "Tomorrow, April 24", "Due: May 1, 2026", "Computer Science", "confirmed")
    )

    val pastReservations = listOf(
        ReservationData(3, "Study Booth 3", "Monday, April 21", "1:00 PM - 3:00 PM", "", "completed"),
        ReservationData(4, "Database Systems", "Monday, April 14", "Overdue (PHP 50 fine)", "", "overdue")
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Open booking flow */ },
                containerColor = Color(0xFF7F1D1D),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Reservation")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(innerPadding)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "My Reservations",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                )

                // Tabs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TabButton(
                        text = "Upcoming (${upcomingReservations.size})",
                        isActive = activeTab == "upcoming",
                        modifier = Modifier.weight(1f)
                    ) { activeTab = "upcoming" }

                    TabButton(
                        text = "Past (${pastReservations.size})",
                        isActive = activeTab == "past",
                        modifier = Modifier.weight(1f)
                    ) { activeTab = "past" }
                }
            }

            // List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                val listToDisplay = if (activeTab == "upcoming") upcomingReservations else pastReservations

                if (listToDisplay.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No reservations found.", color = Color.Gray)
                        }
                    }
                } else {
                    items(listToDisplay) { res ->
                        if (activeTab == "upcoming") {
                            UpcomingCard(res)
                        } else {
                            PastCard(res)
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
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) Color(0xFF7F1D1D) else Color(0xFFF3F4F6),
            contentColor = if (isActive) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun UpcomingCard(res: ReservationData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF7F1D1D)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(res.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            // Details
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(Icons.Default.DateRange, "Date", res.date)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(Icons.Default.LocationOn, "Location", res.location)

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { /* Modify */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7F1D1D)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF7F1D1D)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Modify", fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = { /* Cancel */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Cancel", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun PastCard(res: ReservationData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(res.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(res.date, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (res.status == "completed") Color(0xFFF3F4F6) else Color(0xFFFEF2F2),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = res.status.uppercase(),
                        color = if (res.status == "completed") Color.Gray else Color(0xFFDC2626),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(res.time, fontSize = 12.sp, color = Color.DarkGray)

            if (res.status == "overdue") {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp)).padding(8.dp)
                ) {
                    Text("Please settle your fine at the Accounting Office", fontSize = 12.sp, color = Color(0xFFB91C1C))
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

data class ReservationData(val id: Int, val name: String, val date: String, val time: String, val location: String, val status: String)