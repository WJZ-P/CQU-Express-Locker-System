package me.wjz.cquexpresslocker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.wjz.cquexpresslocker.ui.screens.common.*
import me.wjz.cquexpresslocker.ui.screens.user.*
import me.wjz.cquexpresslocker.ui.screens.courier.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = AppRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 启动页
        composable(AppRoutes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToUserMain = {
                    navController.navigate(AppRoutes.USER_MAIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToCourierMain = {
                    navController.navigate(AppRoutes.COURIER_MAIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // 登录
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onLoginSuccess = { isUser ->
                    val route = if (isUser) AppRoutes.USER_MAIN else AppRoutes.COURIER_MAIN
                    navController.navigate(route) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        // 注册
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        
        // 用户主页（含底部导航）
        composable(AppRoutes.USER_MAIN) {
            UserMainScreen(
                onNavigateToExpressDetail = { expressId ->
                    navController.navigate("user_express_detail/$expressId")
                },
                onNavigateToSendExpress = {
                    navController.navigate(AppRoutes.USER_SEND_EXPRESS)
                },
                onNavigateToHistory = {
                    navController.navigate(AppRoutes.USER_HISTORY)
                },
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.USER_MAIN) { inclusive = true }
                    }
                },
                onSessionExpired = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.USER_MAIN) { inclusive = true }
                    }
                }
            )
        }
        
        // 用户发快递
        composable(AppRoutes.USER_SEND_EXPRESS) {
            SendExpressScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 用户历史记录
        composable(AppRoutes.USER_HISTORY) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 快递员主页（含底部导航）
        composable(AppRoutes.COURIER_MAIN) {
            CourierMainScreen(
                onNavigateToExpressDetail = { expressId ->
                    navController.navigate("courier_express_detail/$expressId")
                },
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.COURIER_MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
