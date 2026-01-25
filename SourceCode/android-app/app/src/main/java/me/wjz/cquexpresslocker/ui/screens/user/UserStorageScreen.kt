package me.wjz.cquexpresslocker.ui.screens.user

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.network.StorageListItem
import me.wjz.cquexpresslocker.viewmodels.user.UserStorageUiState
import me.wjz.cquexpresslocker.viewmodels.user.UserStorageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStorageScreen(
    viewModel: UserStorageViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("存物品", "我的寄存")
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("寄存") }
        )
        
        // Tab切换
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        when (selectedTab) {
            0 -> StorageNewScreen(viewModel)
            1 -> StorageListScreen(uiState, viewModel)
        }
    }
}

@Composable
private fun StorageNewScreen(viewModel: UserStorageViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedCompartmentSize by remember { mutableStateOf("medium") }
    var selectedLockerId by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("24") }
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 步骤说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("寄存流程", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                StorageStep(1, "选择快递柜和仓位大小")
                StorageStep(2, "填写物品描述")
                StorageStep(3, "选择寄存时长")
                StorageStep(4, "获取取件码，分享给朋友")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 创建寄存按钮
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.AddBox, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("新建寄存", fontSize = 16.sp)
        }
        
        // 加载状态处理
        when (uiState) {
            is UserStorageUiState.Loading -> {
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UserStorageUiState.CreateStorageSuccess -> {
                Spacer(modifier = Modifier.height(24.dp))
                SuccessMessage(
                    message = "寄存成功！取件码：${(uiState as UserStorageUiState.CreateStorageSuccess).data.openCode}",
                    onDismiss = { viewModel.resetState() }
                )
            }
            is UserStorageUiState.Error -> {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorMessage(
                    message = (uiState as UserStorageUiState.Error).message,
                    onDismiss = { viewModel.resetState() }
                )
            }
            else -> {}
        }
    }
    
    // 创建寄存对话框
    if (showDialog) {
        CreateStorageDialog(
            onDismiss = { showDialog = false },
            onConfirm = { lockerId, size, duration, description ->
                viewModel.createStorage(lockerId, size, duration, description)
                showDialog = false
            },
            viewModel = viewModel
        )
    }
}

@Composable
private fun StorageStep(number: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "$number",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
private fun StorageListScreen(uiState: UserStorageUiState, viewModel: UserStorageViewModel) {
    // 首次进入时加载列表
    LaunchedEffect(Unit) {
        viewModel.loadStorageList()
    }
    
    when (uiState) {
        is UserStorageUiState.Loading, UserStorageUiState.Initial -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UserStorageUiState.StorageListLoaded -> {
            if (uiState.data.list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Archive,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("暂无寄存记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.data.list) { item ->
                        StorageCard(item)
                    }
                }
            }
        }
        is UserStorageUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadStorageList() }) {
                        Text("重试")
                    }
                }
            }
        }
        else -> {}
    }
}

@Composable
private fun StorageCard(item: StorageListItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
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
                Text(item.lockerName, fontWeight = FontWeight.Medium)
                AssistChip(
                    onClick = { },
                    label = { Text(mapStorageStatus(item.status)) },
                    colors = if (item.status == "active") {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("柜门", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.compartmentNo)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("取件码", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        item.openCode,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "物品: ${item.itemDescription}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                "寄存: ${item.createTime} 至 ${item.expireTime}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (item.status == "active") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { /* 分享取件码 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("分享取件码")
                }
            }
        }
    }
}

@Composable
private fun CreateStorageDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit,
    viewModel: UserStorageViewModel
) {
    var lockerId by remember { mutableStateOf("1") }
    var compartmentSize by remember { mutableStateOf("medium") }
    var duration by remember { mutableStateOf("24") }
    var itemDescription by remember { mutableStateOf("") }
    
    val availability by viewModel.lockerAvailability.collectAsState()
    
    // 当快递柜ID改变时，加载该柜的可用格口信息
    LaunchedEffect(lockerId) {
        if (lockerId.isNotBlank()) {
            viewModel.loadLockerAvailability(lockerId)
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建寄存") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 快递柜选择
                OutlinedTextField(
                    value = lockerId,
                    onValueChange = { lockerId = it },
                    label = { Text("快递柜ID（数字）") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                // 显示快递柜可用格口信息
                if (availability != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("小仓", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${availability!!.smallCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("中仓", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${availability!!.mediumCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("大仓", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${availability!!.largeCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                // 仓位大小选择
                Text("仓位大小", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("small", "medium", "large").forEach { size ->
                        FilterChip(
                            selected = compartmentSize == size,
                            onClick = { compartmentSize = size },
                            label = { 
                                Text(
                                    mapCompartmentSize(size),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // 寄存时长
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("寄存时长(小时)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                
                // 物品描述
                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("物品描述") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (lockerId.isNotBlank() && duration.isNotBlank()) {
                        onConfirm(lockerId, compartmentSize, duration.toIntOrNull() ?: 24, itemDescription)
                    }
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun SuccessMessage(message: String, onDismiss: () -> Unit) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text("成功", fontWeight = FontWeight.Bold)
                Text(message, fontSize = 12.sp)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text("错误", fontWeight = FontWeight.Bold)
                Text(message, fontSize = 12.sp)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    }
}

private fun mapStorageStatus(status: String): String {
    return when (status) {
        "active" -> "寄存中"
        "completed" -> "已取出"
        "expired" -> "已过期"
        else -> status
    }
}

private fun mapCompartmentSize(size: String): String {
    return when (size) {
        "small" -> "小仓\n(¥0.5/h)"
        "medium" -> "中仓\n(¥1/h)"
        "large" -> "大仓\n(¥1.5/h)"
        else -> size
    }
}
