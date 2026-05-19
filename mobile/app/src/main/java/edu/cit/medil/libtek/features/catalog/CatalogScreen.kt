package edu.cit.medil.libtek.features.catalog

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen() {
    var activeTab by remember { mutableStateOf("books") }
    var searchQuery by remember { mutableStateOf("") }

    // Dummy data translating your React state
    val books = listOf(
        Book("Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848", true),
        Book("Clean Code", "Robert C. Martin", "978-0132350884", true),
        Book("Design Patterns", "Gang of Four", "978-0201633612", false),
        Book("Database Systems", "Ramez Elmasri", "978-0133970777", true)
    )

    val booths = listOf(
        Booth("Study Booth 1", "2-4 people", "Floor 2", true),
        Booth("Study Booth 5", "4-6 people", "Floor 2", true),
        Booth("Conference Room A", "8-10 people", "Floor 3", false),
        Booth("Quiet Pod 1", "1-2 people", "Floor 1", true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)) // Gray-50
    ) {
        // Header Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Library Catalog",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search books or booths...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF7F1D1D),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Tabs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TabButton(
                    text = "Books",
                    isActive = activeTab == "books",
                    modifier = Modifier.weight(1f)
                ) { activeTab = "books" }

                TabButton(
                    text = "Booths",
                    isActive = activeTab == "booths",
                    modifier = Modifier.weight(1f)
                ) { activeTab = "booths" }
            }
        }

        // Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFF3F4F6))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (activeTab == "books") "${books.size} results" else "${booths.size} results",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "Filter",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7F1D1D)
            )
        }

        // Content List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (activeTab == "books") {
                items(books.filter { it.title.contains(searchQuery, ignoreCase = true) }) { book ->
                    BookCard(book)
                }
            } else {
                items(booths.filter { it.name.contains(searchQuery, ignoreCase = true) }) { booth ->
                    BoothCard(booth)
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
fun BookCard(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                // Placeholder for Open Library API Image Fetching
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.List, contentDescription = "Book", tint = Color.Gray)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(book.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Text(book.author, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
                    Text("ISBN: ${book.isbn}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                if (book.isAvailable) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (book.isAvailable) "Available" else "Checked Out",
                            color = if (book.isAvailable) Color(0xFF047857) else Color(0xFFDC2626),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Trigger Reservation Flow */ },
                enabled = book.isAvailable,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7F1D1D),
                    disabledContainerColor = Color(0xFFE5E7EB)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (book.isAvailable) "Reserve Book" else "Not Available", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BoothCard(booth: Booth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(booth.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Floor", modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(booth.floor, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (booth.isAvailable) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (booth.isAvailable) "Available" else "Occupied",
                        color = if (booth.isAvailable) Color(0xFF047857) else Color(0xFFDC2626),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text("Capacity: ${booth.capacity}", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Trigger Time Slot Booking Flow */ },
                enabled = booth.isAvailable,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7F1D1D),
                    disabledContainerColor = Color(0xFFE5E7EB)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (booth.isAvailable) "Book Now" else "Not Available", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Data Models
data class Book(val title: String, val author: String, val isbn: String, val isAvailable: Boolean)
data class Booth(val name: String, val capacity: String, val floor: String, val isAvailable: Boolean)