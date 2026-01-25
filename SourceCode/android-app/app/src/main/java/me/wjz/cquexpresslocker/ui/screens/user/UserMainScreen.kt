package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.wjz.cquexpresslocker.navigation.UserNavItem
import me.wjz.cquexpresslocker.viewmodels.user.UserHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(
    onNavigateToExpressDetail: (String) -> Unit,
    onNavigateToPickup: (String) -> Unit,
    onNavigateToSendExpress: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToEditProfile: (String) -> Unit,
    onLogout: () -> Unit,
    onSessionExpired: () -> Unit,
    shouldRefreshProfile: Boolean = false,
    onProfileRefreshed: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // 在 UserMainScreen 创建共享的 ViewModel
    val sharedUserHomeViewModel: UserHomeViewModel = viewModel()
    
    // 监听刷新信号
    LaunchedEffect(shouldRefreshProfile) {
        if (shouldRefreshProfile) {
            sharedUserHomeViewModel.refresh()
            onProfileRefreshed()
        }
    }
    
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
                    onNavigateToSendExpress = onNavigateToSendExpress,
                    onNavigateToStorage = {
                        navController.navigate(UserNavItem.Storage.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSessionExpired = onSessionExpired,
                    viewModel = sharedUserHomeViewModel
                )
            }
            composable(UserNavItem.Pickup.route) {
                UserPickupScreen(
                    onNavigateToExpressDetail = onNavigateToExpressDetail,
                    onNavigateToPickup = onNavigateToPickup
                )
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
                    onNavigateToEditProfile = { nickname ->
                        onNavigateToEditProfile(nickname)
                    },
                    onLogout = onLogout,
                    onRefreshProfile = { sharedUserHomeViewModel.refresh() },
                    viewModel = sharedUserHomeViewModel
                )
            }
        }
    }
}
