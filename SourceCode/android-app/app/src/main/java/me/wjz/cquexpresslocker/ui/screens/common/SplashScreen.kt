package me.wjz.cquexpresslocker.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToUserMain: () -> Unit,
    onNavigateToCourierMain: () -> Unit
) {
    // 模拟检查登录状态
    LaunchedEffect(Unit) {
        delay(1500)
        // TODO: 检查本地token，判断跳转
        onNavigateToLogin()
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "重大智柜",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Logo placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📦",
                fontSize = 64.sp
            )
        }
        Text(
            text = "智能快递，便捷生活",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
