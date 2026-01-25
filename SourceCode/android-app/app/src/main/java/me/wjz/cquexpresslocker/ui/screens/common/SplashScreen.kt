package me.wjz.cquexpresslocker.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.viewmodels.SplashViewModel
import me.wjz.cquexpresslocker.viewmodels.SplashUiState

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToUserMain: () -> Unit,
    onNavigateToCourierMain: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val splashState by viewModel.splashState.collectAsState()
    
    // 检查 token 有效性
    LaunchedEffect(Unit) {
        viewModel.checkTokenValidity()
    }
    
    // 根据状态导航
    LaunchedEffect(splashState) {
        when (val state = splashState) {
            is SplashUiState.GoToLogin -> onNavigateToLogin()
            is SplashUiState.GoToHome -> {
                if (state.isUser) {
                    onNavigateToUserMain()
                } else {
                    onNavigateToCourierMain()
                }
            }
            SplashUiState.Loading -> {} // 保持加载状态
        }
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
