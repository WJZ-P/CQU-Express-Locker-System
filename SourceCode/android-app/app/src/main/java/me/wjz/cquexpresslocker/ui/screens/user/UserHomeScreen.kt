package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import me.wjz.cquexpresslocker.viewmodels.user.UserHomeViewModel
import me.wjz.cquexpresslocker.viewmodels.user.UserHomeUiState
import me.wjz.cquexpresslocker.viewmodels.user.UserHomeData
import me.wjz.cquexpresslocker.viewmodels.user.StorageItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    onNavigateToExpressDetail: (String) -> Unit,
    onNavigateToSendExpress: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: UserHomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UserHomeUiState.Unauthorized) {
            onSessionExpired()
        }
    }

    when (val state = uiState) {
        is UserHomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UserHomeUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("加载失败: ${state.message}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("重试")
                    }
                }
            }
        }
        is UserHomeUiState.Success -> {
            UserHomeContent(
                data = state.data,
                onNavigateToExpressDetail = onNavigateToExpressDetail,
                onNavigateToSendExpress = onNavigateToSendExpress,
                onNavigateToStorage = onNavigateToStorage
            )
        }
        UserHomeUiState.Unauthorized -> {
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
private fun UserHomeContent(
    data: UserHomeData,
    onNavigateToExpressDetail: (String) -> Unit,
    onNavigateToSendExpress: () -> Unit,
    onNavigateToStorage: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部欢迎
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "用户${data.userName}，你好！",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "今天有 ${data.numOfPackages} 个快递待取",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "通知",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        
        // 快捷功能
        item {
            Text(
                text = "快捷服务",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem(
                    icon = Icons.Default.QrCodeScanner,
                    label = "扫码取件",
                    onClick = { }
                )
                QuickActionItem(
                    icon = Icons.Default.Keyboard,
                    label = "输入取件码",
                    onClick = { }
                )
                QuickActionItem(
                    icon = Icons.Default.Archive,
                    label = "寄存物品",
                    onClick = onNavigateToStorage
                )
                QuickActionItem(
                    icon = Icons.Default.Send,
                    label = "发快递",
                    onClick = onNavigateToSendExpress
                )
            }
        }
        
        // 待取快递
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "待取快递",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { }) {
                    Text("查看全部")
                }
            }
        }
        
        // 快递列表
        items(data.pendingExpressList) { item ->
            ExpressCard(
                item = item,
                onClick = { onNavigateToExpressDetail(item.trackingNo) }
            )
        }
        
        // 最近寄存
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最近寄存",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { }) {
                    Text("查看全部")
                }
            }
        }
        
        // 寄存物品卡片
        data.recentStorage?.let { storage ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Archive,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("寄存物品", fontWeight = FontWeight.Medium)
                            Text(
                                "${storage.location} | 取件码: ${storage.pickupCode}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                        AssistChip(
                            onClick = { },
                            label = { Text(storage.status) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

data class ExpressItem(
    val trackingNo: String,
    val company: String,
    val location: String,
    val pickupCode: String,
    val time: String
)

@Composable
private fun ExpressCard(
    item: ExpressItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.company, fontWeight = FontWeight.Medium)
                Text(
                    item.trackingNo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Text(
                    "${item.location} | 取件码: ${item.pickupCode}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                item.time,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}
