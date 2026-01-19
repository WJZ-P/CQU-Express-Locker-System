package me.wjz.cquexpresslocker.ui.screens.courier

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.wjz.cquexpresslocker.navigation.CourierNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierMainScreen(
    onNavigateToExpressDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val bottomNavItems = listOf(
        CourierNavItem.Home,
        CourierNavItem.Deliver,
        CourierNavItem.Scan,
        CourierNavItem.Pickup,
        CourierNavItem.Profile
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = CourierNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(CourierNavItem.Home.route) {
                CourierHomeScreen()
            }
            composable(CourierNavItem.Deliver.route) {
                CourierDeliverScreen()
            }
            composable(CourierNavItem.Scan.route) {
                CourierScanScreen()
            }
            composable(CourierNavItem.Pickup.route) {
                CourierPickupScreen()
            }
            composable(CourierNavItem.Profile.route) {
                CourierProfileScreen(onLogout = onLogout)
            }
        }
    }
}
