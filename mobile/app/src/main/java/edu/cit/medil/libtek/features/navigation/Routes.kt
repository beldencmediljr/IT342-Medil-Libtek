package edu.cit.medil.libtek.features.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Catalog : Screen("catalog")
    object Reservations : Screen("reservations")
    object Profile : Screen("profile")
}