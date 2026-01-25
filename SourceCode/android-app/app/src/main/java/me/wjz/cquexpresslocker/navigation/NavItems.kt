package me.wjz.cquexpresslocker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 用户端底部导航项
 */
sealed class UserNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : UserNavItem("user_home", "首页", Icons.Default.Home)
    data object Pickup : UserNavItem("user_pickup", "取件", Icons.Default.Inventory)
    data object Scan : UserNavItem("user_scan", "扫码", Icons.Default.QrCodeScanner)
    data object Storage : UserNavItem("user_storage", "寄存", Icons.Default.Archive)
    data object Profile : UserNavItem("user_profile", "我的", Icons.Default.Person)
}

/**
 * 快递员端底部导航项
 */
sealed class CourierNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : CourierNavItem("courier_home", "首页", Icons.Default.Home)
    data object Deliver : CourierNavItem("courier_deliver", "投递", Icons.Default.LocalShipping)
    data object Scan : CourierNavItem("courier_scan", "扫码", Icons.Default.QrCodeScanner)
    data object Pickup : CourierNavItem("courier_pickup", "取件", Icons.Default.Inventory)
    data object Profile : CourierNavItem("courier_profile", "我的", Icons.Default.Person)
}

/**
 * 应用路由
 */
object AppRoutes {
    // 通用
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // 用户端
    const val USER_MAIN = "user_main"
    const val USER_EXPRESS_DETAIL = "user_express_detail/{expressId}"
    const val USER_SEND_EXPRESS = "user_send_express"
    const val USER_HISTORY = "user_history"
    const val USER_EDIT_PROFILE = "user_edit_profile/{nickname}"
    
    // 快递员端
    const val COURIER_MAIN = "courier_main"
    const val COURIER_EXPRESS_DETAIL = "courier_express_detail/{expressId}"
    
    // 二维码扫描
    const val QR_SCANNER = "qr_scanner"
}
