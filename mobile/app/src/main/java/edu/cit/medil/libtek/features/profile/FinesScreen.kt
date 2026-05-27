package edu.cit.medil.libtek.features.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.medil.libtek.features.api.ApiClient
import edu.cit.medil.libtek.features.api.FineDto
import edu.cit.medil.libtek.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinesScreen(tokenManager: TokenManager, onBack: () -> Unit) {
    val context = LocalContext.current
    var finesList by remember { mutableStateOf<List<FineDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = "Bearer ${tokenManager.getAccessToken()}"
        ApiClient.apiService.getUserFines(token).enqueue(object : Callback<List<FineDto>> {
            override fun onResponse(call: Call<List<FineDto>>, response: Response<List<FineDto>>) {
                if (response.isSuccessful) {
                    finesList = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Failed to load fines data", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<FineDto>>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }

    val unpaidFines = finesList.filter { it.status.equals("unpaid", ignoreCase = true) }
    val totalUnpaidAmount = unpaidFines.sumOf { it.amount }
    val hasFines = totalUnpaidAmount > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Fines", fontWeight = FontWeight.Bold, color = Color(0xFF003366)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF003366))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9FAFB))
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7F1D1D))
                }
            } else {
                // Summary Card at the Top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            if (hasFines) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (hasFines) Color(0xFFFCA5A5) else Color(0xFFA7F3D0),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (hasFines) Icons.Default.Info else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (hasFines) Color(0xFFDC2626) else Color(0xFF059669),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            if (hasFines) {
                                Text(
                                    text = "Outstanding Balance",
                                    color = Color(0xFF7F1D1D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "PHP ${String.format("%.2f", totalUnpaidAmount)}",
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Please settle your balance at the University Accounting Office and present the receipt to the librarian.",
                                    color = Color(0xFF991B1B),
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    text = "No Outstanding Fines",
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Keep returning resources on time to avoid penalties. Thank you!",
                                    color = Color(0xFF047857),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Fines History",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (finesList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No fines recorded on your account.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(finesList) { fine ->
                            val isUnpaid = fine.status.equals("unpaid", ignoreCase = true)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = fine.resourceName ?: "Unknown Resource",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF003366),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isUnpaid) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = (fine.status ?: "unpaid").uppercase(),
                                                color = if (isUnpaid) Color(0xFF991B1B) else Color(0xFF065F46),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Days Overdue",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${fine.daysOverdue} days",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Amount Due",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "PHP ${String.format("%.2f", fine.amount)}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isUnpaid) Color(0xFF7F1D1D) else Color.Black
                                            )
                                        }
                                    }

                                    if (!isUnpaid) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = Color(0xFFF3F4F6))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        if (!fine.receiptNumber.isNullOrEmpty()) {
                                            Text(
                                                text = "Receipt No: ${fine.receiptNumber}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF374151)
                                            )
                                        }
                                        if (!fine.notes.isNullOrEmpty()) {
                                            Text(
                                                text = "Notes: ${fine.notes}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
