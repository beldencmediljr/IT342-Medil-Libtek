// THE FIX: Package strictly matches the folder it is placed in
package edu.cit.medil.libtek.features.core

import android.os.Bundle
// THE FIX: AppCompatActivity prevents Theme.Material3 crashes
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import edu.cit.medil.libtek.features.catalog.CatalogScreen
import edu.cit.medil.libtek.features.dashboard.DashboardScreen
import edu.cit.medil.libtek.features.navigation.AppNavigation
import edu.cit.medil.libtek.features.profile.ProfileScreen
import edu.cit.medil.libtek.features.reservation.ReservationScreen
import edu.cit.medil.libtek.util.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        setContent {
            AppNavigation(tokenManager = tokenManager)
        }
    }
}

@Composable
fun MainScreen(tokenManager: TokenManager, onLogout: () -> Unit) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Catalog", "Bookings", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.DateRange, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF7F1D1D),
                contentColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF7F1D1D),
                            selectedTextColor = Color.White,
                            indicatorColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> DashboardScreen(tokenManager = tokenManager)
                1 -> CatalogScreen()
                2 -> ReservationScreen()
                3 -> ProfileScreen(
                    onLogoutClick = onLogout,
                    onUploadIdClick = { }
                )
            }
        }
    }
}