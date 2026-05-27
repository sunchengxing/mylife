package com.mylife.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mylife.app.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Clothing : Screen("clothing", "衣", Icons.Filled.Checkroom)
    data object Food : Screen("food", "食", Icons.Filled.Restaurant)
    data object Housing : Screen("housing", "住", Icons.Filled.Home)
    data object Transport : Screen("transport", "行", Icons.Filled.DirectionsCar)
    data object Profile : Screen("profile", "我的", Icons.Filled.Person)
}

val screens = listOf(
    Screen.Clothing, Screen.Food, Screen.Housing, Screen.Transport, Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLifeNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val route = currentDestination?.route
                    Text(
                        when (route) {
                            "clothing" -> "衣橱"
                            "food" -> "美食"
                            "housing" -> "居住"
                            "transport" -> "出行"
                            "profile" -> "我的"
                            else -> "衣食住行"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "clothing",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("clothing") { ClothingScreen() }
            composable("food") { FoodScreen() }
            composable("housing") { HousingScreen() }
            composable("transport") { TransportScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
