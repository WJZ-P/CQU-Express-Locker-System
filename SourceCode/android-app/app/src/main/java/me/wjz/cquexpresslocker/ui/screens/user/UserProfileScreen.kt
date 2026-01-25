package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.utils.TokenManager
import me.wjz.cquexpresslocker.viewmodels.user.UserHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToEditProfile: (String) -> Unit,
    onLogout: () -> Unit,
    onRefreshProfile: () -> Unit = {},
    viewModel: UserHomeViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is me.wjz.cquexpresslocker.viewmodels.user.UserHomeUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // 用户信息卡片
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 头像
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    state.data.userName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.data.phoneNumber,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            
                            IconButton(onClick = { onNavigateToEditProfile(state.data.userName) }) {
                                Icon(Icons.Default.Edit, contentDescription = "编辑")
                            }
                        }
                    }
                }
                
                // 统计数据
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("待取件", state.data.numOfPackages.toString())
                        StatItem("已完成", "56")
                        StatItem("寄存中", "1")
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                
                // 功能菜单
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Default.History,
                                title = "历史记录",
                                onClick = onNavigateToHistory
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileMenuItem(
                                icon = Icons.Default.Face,
                                title = "人脸管理",
                                subtitle = "已录入",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileMenuItem(
                                icon = Icons.Default.LocationOn,
                                title = "常用地址",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileMenuItem(
                                icon = Icons.Default.Notifications,
                                title = "消息通知",
                                onClick = { }
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                
                // 其他设置
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Default.Help,
                                title = "帮助中心",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileMenuItem(
                                icon = Icons.Default.Info,
                                title = "关于我们",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileMenuItem(
                                icon = Icons.Default.Settings,
                                title = "设置",
                                onClick = { }
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
                
                // 退出登录
                item {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                TokenManager.clearAll()
                                onLogout()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("退出登录")
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
        is me.wjz.cquexpresslocker.viewmodels.user.UserHomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is me.wjz.cquexpresslocker.viewmodels.user.UserHomeUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("加载失败: ${(state as me.wjz.cquexpresslocker.viewmodels.user.UserHomeUiState.Error).message}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("重试")
                    }
                }
            }
        }
        me.wjz.cquexpresslocker.viewmodels.user.UserHomeUiState.Unauthorized -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        if (subtitle != null) {
            Text(
                subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
