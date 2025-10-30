package com.example.ohuatl.navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.ohuatl.ui.screens.HomeScreen
import com.example.ohuatl.ui.screens.LoginScreen
import com.example.ohuatl.ui.screens.RewardsScreen
import com.example.ohuatl.ui.screens.HistorialScreen
import com.example.ohuatl.ui.screens.RegistroScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue


@Composable
fun Appnavigation() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("home", "rewards", "historial")) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "rewards",
                        onClick = { navController.navigate("rewards") },
                        icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null) },
                        label = { Text("Recompensas") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "historial",
                        onClick = { navController.navigate("historial") },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("Historial") }
                    )
                }
            }
        }
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegistroScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("rewards") { RewardsScreen(navController) }
            composable("historial") { HistorialScreen(navController) }
        }
    }
}