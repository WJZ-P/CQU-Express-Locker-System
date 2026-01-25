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
import me.wjz.cquexpresslocker.network.ExpressItemData
import me.wjz.cquexpresslocker.viewmodels.user.UserPickupUiState
import me.wjz.cquexpresslocker.viewmodels.user.UserPickupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPickupScreen(
    onNavigateToExpressDetail: (String) -> Unit,
    onNavigateToPickup: (String) -> Unit,
    viewModel: UserPickupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var pickupCode by remember { mutableStateOf("") }
    var showCodeInput by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部标题
        TopAppBar(
            title = { Text("取件") }
        )

        // 取件方式
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { /* 扫码取件 */ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("扫码取件", fontWeight = FontWeight.Medium)
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showCodeInput = !showCodeInput }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Keyboard,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("输入取件码", fontWeight = FontWeight.Medium)
                }
            }
        }

        // 取件码输入
        if (showCodeInput) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = pickupCode,
                        onValueChange = { pickupCode = it },
                        label = { Text("请输入6位取件码") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* 开柜取件 */ },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = pickupCode.length == 6
                    ) {
                        Text("开柜取件")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 根据状态显示内容
        when (uiState) {
            is UserPickupUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UserPickupUiState.Success -> {
                val items = (uiState as UserPickupUiState.Success).items

                Text(
                    text = "待取件 (${items.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        PickupCard(
                            item = item,
                            onClick = { onNavigateToExpressDetail(item.expressId) },
                            onOpenCompartment = { expressId -> onNavigateToPickup(expressId) }
                        )
                    }
                }
            }
            is UserPickupUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无待取件快递", fontSize = 16.sp)
                }
            }
            is UserPickupUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = (uiState as UserPickupUiState.Error).message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("重试")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PickupCard(
    item: ExpressItemData,
    onClick: () -> Unit,
    onOpenCompartment: (expressId: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.company, fontWeight = FontWeight.Bold)
                AssistChip(
                    onClick = { },
                    label = { Text(mapStatusToChinese(item.status)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                item.trackingNo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    item.lockerName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("柜门位置", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.compartmentNo, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("取件码", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        item.pickupCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onOpenCompartment(item.expressId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("一键开柜")
            }
        }
    }
}

private fun mapStatusToChinese(status: String): String {
    return when (status) {
        "pending" -> "待取件"
        "picked" -> "已取件"
        else -> status
    }
}
