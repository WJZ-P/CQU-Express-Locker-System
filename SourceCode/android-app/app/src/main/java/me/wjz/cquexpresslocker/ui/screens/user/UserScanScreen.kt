package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
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
import me.wjz.cquexpresslocker.viewmodels.user.ScanUiState
import me.wjz.cquexpresslocker.viewmodels.user.UserScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScanScreen(
    viewModel: UserScanViewModel = viewModel(),
    onExpressPickedUp: (expressId: String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("扫码") }
        )
        
        // 扫码区域（模拟）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "将二维码放入框内扫描",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.openCamera() }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("打开相机")
                        }
                    }
                }
            }
        }
        
        // 底部提示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "扫码说明",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("扫描快递柜上的二维码，快速取件", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("输入取件码可取走他人寄存的物品", fontSize = 14.sp)
                }
            }
        }
    }
    
    // 功能选择菜单
    if (uiState is ScanUiState.ShowFunctionMenu) {
        FunctionMenuDialog(
            onPickup = { viewModel.selectPickupFunction() },
            onInputCode = { viewModel.selectInputCodeFunction() },
            onCancel = { viewModel.cancel() }
        )
    }
    
    // 输入取件码对话框
    if (uiState is ScanUiState.InputPickupCode) {
        InputPickupCodeDialog(
            onConfirm = { code -> viewModel.pickupByCode(code) },
            onCancel = { viewModel.cancel() }
        )
    }
    
    // 扫码结果对话框
    when (val state = uiState) {
        is ScanUiState.ScannedResult -> {
            ScanResultDialog(
                express = state.express,
                onPickup = { viewModel.pickupExpress(state.express) },
                onCancel = { viewModel.cancel() }
            )
        }
        is ScanUiState.PickupSuccess -> {
            PickupSuccessDialog(
                compartmentNo = state.compartmentNo,
                lockerName = state.lockerName,
                onClose = {
                    onExpressPickedUp(state.expressId)
                    viewModel.cancel()
                }
            )
        }
        is ScanUiState.PickupByCodeSuccess -> {
            PickupByCodeSuccessDialog(
                compartmentNo = state.compartmentNo,
                lockerName = state.lockerName,
                onClose = {
                    onExpressPickedUp(state.expressId)
                    viewModel.cancel()
                }
            )
        }
        is ScanUiState.Error -> {
            ErrorDialog(
                message = state.message,
                onClose = { viewModel.cancel() }
            )
        }
        is ScanUiState.NoExpress -> {
            NoExpressDialog(
                onClose = { viewModel.cancel() }
            )
        }
        is ScanUiState.PickupInProgress, is ScanUiState.PickupByCodeInProgress -> {
            LoadingDialog()
        }
        is ScanUiState.Loading -> {
            LoadingDialog()
        }
        else -> {
            // 无对话框
        }
    }
}

@Composable
private fun FunctionMenuDialog(
    onPickup: () -> Unit,
    onInputCode: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("选择功能") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = onPickup,
                    modifier = Modifier.fillMaxWidth()
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
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("取件", fontWeight = FontWeight.Bold)
                            Text(
                                "取走您的快递或寄存物品",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Card(
                    onClick = onInputCode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Password,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("输入取件码", fontWeight = FontWeight.Bold)
                            Text(
                                "使用他人分享的取件码取物",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun InputPickupCodeDialog(
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pickupCode by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("输入取件码") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "请输入他人分享给您的6位取件码",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = pickupCode,
                    onValueChange = { 
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            pickupCode = it 
                        }
                    },
                    label = { Text("取件码") },
                    placeholder = { Text("请输入6位数字") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("取消")
                }
                Button(
                    onClick = { onConfirm(pickupCode) },
                    modifier = Modifier.weight(1f),
                    enabled = pickupCode.length == 6
                ) {
                    Text("确定")
                }
            }
        }
    )
}

@Composable
private fun ScanResultDialog(
    express: me.wjz.cquexpresslocker.network.ExpressItemData,
    onPickup: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("扫码结果") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoRow("快递单号", express.trackingNo)
                InfoRow("快递公司", express.company)
                InfoRow("快递柜", express.lockerName)
                InfoRow("格口", express.compartmentNo)
                InfoRow("大小", express.compartmentNo.let {
                    when {
                        it.contains("小") -> "小仓"
                        it.contains("中") -> "中仓"
                        it.contains("大") -> "大仓"
                        else -> "未知"
                    }
                })
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("取消")
                }
                Button(
                    onClick = onPickup,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("一键取件")
                }
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PickupSuccessDialog(
    compartmentNo: String,
    lockerName: String,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("取件成功") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("快递已开柜，请取走快递", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow("快递柜", lockerName)
                InfoRow("格口号", compartmentNo)
            }
        },
        confirmButton = {
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun PickupByCodeSuccessDialog(
    compartmentNo: String,
    lockerName: String,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("取件成功") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("柜门已开启，请取走物品", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow("快递柜", lockerName)
                InfoRow("格口号", compartmentNo)
            }
        },
        confirmButton = {
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun ErrorDialog(
    message: String,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("错误") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun NoExpressDialog(
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("提示") },
        text = { Text("暂无待取快递") },
        confirmButton = {
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("加载中...") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        confirmButton = {}
    )
}
