package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.network.ExpressDetail
import me.wjz.cquexpresslocker.viewmodels.user.ExpressDetailUiState
import me.wjz.cquexpresslocker.viewmodels.user.ExpressDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpressDetailScreen(
    expressId: String,
    onNavigateToPickup: (String) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    val viewModel: ExpressDetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExpressDetailViewModel(expressId) as T
            }
        }
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("快递详情") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )
        
        // 内容
        when (uiState) {
            is ExpressDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ExpressDetailUiState.Success -> {
                val detail = (uiState as ExpressDetailUiState.Success).detail
                ExpressDetailContent(detail, expressId, onNavigateToPickup)
            }
            is ExpressDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as ExpressDetailUiState.Error).message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("重试")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpressDetailContent(
    detail: ExpressDetail,
    expressId: String,
    onNavigateToPickup: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 快递信息卡片
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(detail.company, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    AssistChip(
                        onClick = { },
                        label = { Text(mapStatusToChinese(detail.status)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                
                Text(
                    detail.trackingNo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                
                Divider()
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(detail.lockerName, fontWeight = FontWeight.Medium)
                }
            }
        }
        
        // 柜子信息
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("柜子信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                InfoRow("位置", detail.lockerName)
                InfoRow("格口号", detail.compartmentNo)
                InfoRow("格口尺寸", detail.compartmentSize)
            }
        }
        
        // 时间信息
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("时间信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                InfoRow("到达时间", detail.arrivalTime)
                InfoRow("取件截止", detail.deadline)
            }
        }
        
        // 收寄人信息
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("收寄人信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("寄件人", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${detail.senderName} ${detail.senderPhone}", fontWeight = FontWeight.Medium)
                }
                
                Divider()
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("收件人", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${detail.receiverName} ${detail.receiverPhone}", fontWeight = FontWeight.Medium)
                }
            }
        }
        
        // 取件码
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("取件码", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    detail.pickupCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // 开柜按钮
        Button(
            onClick = { onNavigateToPickup(expressId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = detail.status == "pending"
        ) {
            Text("一键开柜取件")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

private fun mapStatusToChinese(status: String): String {
    return when (status) {
        "pending" -> "待取件"
        "picked" -> "已取件"
        else -> status
    }
}
