package edu.cit.medil.libtek.features.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import edu.cit.medil.libtek.features.auth.LoginScreen
import edu.cit.medil.libtek.features.auth.RegisterScreen
import edu.cit.medil.libtek.features.core.MainScreen
import edu.cit.medil.libtek.util.TokenManager

// THE FIX: Renamed 'Screen' to 'NavRoute' to avoid the Redeclaration collision
sealed class NavRoute(val route: String) {
    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object Dashboard : NavRoute("dashboard")
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()

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
                onRegisterClick = { navController.navigate(NavRoute.Register.route) }
            )
        }

        composable(NavRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(NavRoute.Dashboard.route) {
            MainScreen(
                tokenManager = tokenManager,
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