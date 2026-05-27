package edu.cit.medil.libtek.features.core

import android.content.Intent
import android.os.Bundle
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

import edu.cit.medil.libtek.features.auth.LoginActivity
import edu.cit.medil.libtek.features.catalog.CatalogScreen
import edu.cit.medil.libtek.features.dashboard.DashboardScreen
import edu.cit.medil.libtek.features.profile.ChangePasswordScreen
import edu.cit.medil.libtek.features.profile.NotificationsScreen
import edu.cit.medil.libtek.features.profile.ProfileScreen
import edu.cit.medil.libtek.features.profile.FinesScreen
import edu.cit.medil.libtek.features.reservation.ReservationScreen
import edu.cit.medil.libtek.util.TokenManager

enum class AppState { MAIN, NOTIFICATIONS, CHANGE_PASSWORD, FINES }

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        setContent {
            var appState by remember { mutableStateOf(AppState.MAIN) }

            when (appState) {
                AppState.NOTIFICATIONS -> NotificationsScreen(tokenManager, onBack = { appState = AppState.MAIN })
                AppState.CHANGE_PASSWORD -> ChangePasswordScreen(tokenManager, onBack = { appState = AppState.MAIN })
                AppState.FINES -> FinesScreen(tokenManager, onBack = { appState = AppState.MAIN })
                AppState.MAIN -> {
                    MainScreen(
                        tokenManager = tokenManager,
                        onNavigateToNotifications = { appState = AppState.NOTIFICATIONS },
                        onNavigateToChangePassword = { appState = AppState.CHANGE_PASSWORD },
                        onNavigateToFines = { appState = AppState.FINES },
                        onLogout = {
                            tokenManager.clearAuthData()
                            val intent = Intent(this, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    tokenManager: TokenManager,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToFines: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Catalog", "Bookings", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.DateRange, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF7F1D1D), contentColor = Color.White) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF7F1D1D), selectedTextColor = Color.White,
                            indicatorColor = Color.White, unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> DashboardScreen(
                    tokenManager = tokenManager,
                    onNavigateToCatalog = { selectedItem = 1 },
                    onNavigateToNotifications = onNavigateToNotifications
                )
                1 -> CatalogScreen(
                    tokenManager = tokenManager,
                    onBookingSuccess = { selectedItem = 2 }
                )
                2 -> ReservationScreen(
                    tokenManager = tokenManager,
                    onNavigateToCatalog = { selectedItem = 1 }
                )
                3 -> ProfileScreen(
                    tokenManager = tokenManager,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onNavigateToFines = onNavigateToFines,
                    onLogoutClick = onLogout
                )
            }
        }
    }
}