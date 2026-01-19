package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.wjz.cquexpresslocker.navigation.UserNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(
    onNavigateToExpressDetail: (String) -> Unit,
    onNavigateToSendExpress: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val bottomNavItems = listOf(
        UserNavItem.Home,
        UserNavItem.Pickup,
        UserNavItem.Scan,
        UserNavItem.Storage,
        UserNavItem.Profile
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
            startDestination = UserNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(UserNavItem.Home.route) {
                UserHomeScreen(
                    onNavigateToExpressDetail = onNavigateToExpressDetail,
                    onNavigateToSendExpress = onNavigateToSendExpress
                )
            }
            composable(UserNavItem.Pickup.route) {
                UserPickupScreen(onNavigateToExpressDetail = onNavigateToExpressDetail)
            }
            composable(UserNavItem.Scan.route) {
                UserScanScreen()
            }
            composable(UserNavItem.Storage.route) {
                UserStorageScreen()
            }
            composable(UserNavItem.Profile.route) {
                UserProfileScreen(
                    onNavigateToHistory = onNavigateToHistory,
                    onLogout = onLogout
                )
            }
        }
    }
}
