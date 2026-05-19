package edu.cit.medil.libtek.features.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import edu.cit.medil.libtek.features.auth.LoginScreen
import edu.cit.medil.libtek.features.auth.RegisterScreen
import edu.cit.medil.libtek.features.core.MainScreen
import edu.cit.medil.libtek.util.TokenManager

sealed class NavRoute(val route: String) {
    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object Dashboard : NavRoute("dashboard")
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val startDest = if (tokenManager.isLoggedIn() && tokenManager.isStudent()) {
        NavRoute.Dashboard.route
    } else {
        NavRoute.Login.route
    }

    NavHost(navController = navController, startDestination = startDest) {

        composable(NavRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoute.Dashboard.route) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(NavRoute.Register.route)
                },
                onTriggerGoogleAuth = {
                    Toast.makeText(context, "Google Auth SDK Initialized", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable(NavRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoute.Dashboard.route) {
            MainScreen(
                tokenManager = tokenManager,
                onNavigateToNotifications = { /* Navigation handled in MainActivity */ },
                onNavigateToChangePassword = { /* Navigation handled in MainActivity */ },
                onLogout = {
                    tokenManager.clearAuthData()
                    navController.navigate(NavRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}